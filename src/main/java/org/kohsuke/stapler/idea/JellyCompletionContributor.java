package org.kohsuke.stapler.idea;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.xml.TagNameReference;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlToken;
import com.intellij.util.ProcessingContext;
import com.intellij.xml.XmlAttributeDescriptor;
import com.intellij.xml.XmlElementDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.kohsuke.stapler.idea.descriptor.StaplerCustomJellyTagLibraryXmlNSDescriptor;
import org.kohsuke.stapler.idea.descriptor.StaplerCustomJellyTagfileXmlAttributeDescriptor;
import org.kohsuke.stapler.idea.descriptor.StaplerCustomJellyTagfileXmlElementDescriptor;
import org.kohsuke.stapler.idea.icons.Icons;

/**
 * Tag name completion for Jelly tag libraries defined as tag files.
 *
 * <p>One would think that contributing {@link StaplerCustomJellyTagLibraryXmlNSDescriptor} and implementing
 * {@link StaplerCustomJellyTagLibraryXmlNSDescriptor#getRootElementsDescriptors(XmlDocument)} is enough to cause the
 * IDEA XML module to show tag name completions, but apparently it is not.
 *
 * <p>The problem is when we have text like the following:
 *
 * <pre><xmp>
 * <j:jelly xmlns:t="/lib" xmlns:j="jelly:core">
 * <html>
 * <body>
 * <!-- hit t, ctrl+space to make sure completion kicks in  -->
 * <t:
 * </body>
 * </html>
 * </j:jelly>
 * </xmp></pre>
 *
 * <p>The html/body tags get dtd.XmlElementDescriptorImpl as their {@link XmlElementDescriptor} (they represent
 * in-memory for-this-document-only temporary DTDs), and unless the body tag appear elsewhere and have some children, it
 * will return null from its {@link XmlElementDescriptor#getElementDescriptor(XmlTag, XmlTag)} when given
 * &lt;t:something/>, and {@link TagNameReference}, which calls
 * {@link StaplerCustomJellyTagLibraryXmlNSDescriptor#getRootElementsDescriptors(XmlDocument)} to list up possible
 * children, uses this as a signal that &lt;t:something/> is not a valid child in this context.
 *
 * <p>While the above doesn't really explain why &lt;j:*> can be completed in this context, I eventually decided that
 * just writing another {@link CompletionContributor} on its own is the easiest thing to do here.
 *
 * <p>So what this {@link CompletionContributor} does is to list up all the legal Jelly tags available in the current
 * namespace bindings as completion candidates.
 *
 * @author Kohsuke Kawaguchi
 */
public class JellyCompletionContributor extends CompletionContributor {

    public JellyCompletionContributor() {
        extend(
                CompletionType.BASIC, // in case of XML completion, this always seems to be BASIC
                PlatformPatterns.psiElement(XmlToken.class).withParent(XmlTag.class),
                new CompletionProvider<>() {
                    @Override
                    protected void addCompletions(
                            @NotNull CompletionParameters parameters,
                            @NotNull ProcessingContext context,
                            @NotNull CompletionResultSet result) {
                        XmlElement name = (XmlElement) parameters.getPosition();
                        XmlTag tag = (XmlTag) name.getParent();
                        Module module = ModuleUtil.findModuleForPsiElement(tag);

                        result.stopHere();

                        createMergedNamespaceMap(tag).forEach((prefix, uri) -> {
                            StaplerCustomJellyTagLibraryXmlNSDescriptor d =
                                    StaplerCustomJellyTagLibraryXmlNSDescriptor.get(uri, module);

                            // If a user has already provided a prefix then only include results from that namespace
                            String existingPrefix = getPrefix(tag.getName());
                            if (existingPrefix != null && !existingPrefix.equalsIgnoreCase(prefix)) {
                                return;
                            }

                            if (d != null) {
                                for (XmlElementDescriptor component : d.getRootElementsDescriptors(null)) {
                                    createAutocompleteElement(
                                            result,
                                            prefix,
                                            uri,
                                            (StaplerCustomJellyTagfileXmlElementDescriptor) component,
                                            tag,
                                            existingPrefix != null);
                                }
                            }
                        });
                    }
                });
    }

    private static void createAutocompleteElement(
            CompletionResultSet result,
            String prefix,
            String uri,
            StaplerCustomJellyTagfileXmlElementDescriptor component,
            XmlTag tag,
            boolean includePrefix) {
        result.addElement(LookupElementBuilder.create((includePrefix ? "" : prefix + ":") + component.getName())
                .withPresentableText(prefix + ":" + component.getName())
                .withIcon(Icons.COMPONENT)
                .withTailText(uri, true)
                .withInsertHandler((context, item) -> {
                    PsiFile psiFile = context.getFile();
                    Project project = context.getProject();
                    Editor editor = context.getEditor();

                    if (psiFile instanceof XmlFile xmlFile) {
                        XmlTag rootTag = xmlFile.getRootTag();

                        if (rootTag != null) {
                            // Import the namespace if necessary
                            WriteCommandAction.runWriteCommandAction(project, () -> {
                                rootTag.setAttribute("xmlns:" + prefix, uri);
                            });

                            // Unblock the document
                            PsiDocumentManager psiDocumentManager =
                                    PsiDocumentManager.getInstance(context.getProject());
                            psiDocumentManager.doPostponedOperationsAndUnblockDocument(editor.getDocument());

                            // Suffix required attributes and tab the user to the first one
                            createRequiredAttributesTemplate(component, tag, project, editor, prefix);
                        }
                    }
                }));
    }

    private static void createRequiredAttributesTemplate(
            StaplerCustomJellyTagfileXmlElementDescriptor component,
            XmlTag tag,
            Project project,
            Editor editor,
            String prefix) {
        TemplateManager templateManager = TemplateManager.getInstance(project);
        StringBuilder templateText = new StringBuilder();

        List<String> requiredAttributes = getRequiredAttributes(component, tag);
        for (String attributeName : requiredAttributes) {
            templateText
                    .append(" ")
                    .append(attributeName)
                    .append("=\"$")
                    .append(attributeName)
                    .append("$\"");
        }

        if (component.getContentType() == XmlElementDescriptor.CONTENT_TYPE_ANY) {
            templateText.append(">\n  $content$\n</" + prefix + ":" + component.getName() + ">");
        } else {
            templateText.append(" />");
        }

        Template template = templateManager.createTemplate("myTemplate", "Jelly", templateText.toString());

        for (String attributeName : requiredAttributes) {
            // Add each attribute as a placeholder variable for tabbing
            template.addVariable(attributeName, "", attributeName.toLowerCase(), true);
        }
        template.addVariable("content", "", "content", true);

        templateManager.startTemplate(editor, template);
    }

    private static List<String> getRequiredAttributes(
            StaplerCustomJellyTagfileXmlElementDescriptor component, XmlTag tag) {
        List<String> requiredAttributes = new ArrayList<>();
        for (XmlAttributeDescriptor attributesDescriptor : component.getAttributesDescriptors(tag)) {
            var mapped = (StaplerCustomJellyTagfileXmlAttributeDescriptor) attributesDescriptor;
            if (mapped.isRequired()) {
                requiredAttributes.add(mapped.getName());
            }
        }
        return requiredAttributes;
    }

    public static String getPrefix(String tagName) {
        if (tagName == null || !tagName.contains(":")) {
            return null;
        }

        // Remove the opening '<' and check if there is a colon in the string
        tagName = tagName.replaceAll("^<", "");
        int colonIndex = tagName.indexOf(':');

        // If there's no colon or the colon is at the start, return null
        if (colonIndex <= 0) {
            return null;
        }

        // Return the prefix, which is the substring before the first ':'
        return tagName.substring(0, colonIndex);
    }

    // We have a list of default namespaces (e.g. l:layout) that we want to suggest,
    // but we also want to suggest the users custom ones
    public static Map<String, String> createMergedNamespaceMap(XmlTag tag) {
        String[] uris = tag.knownNamespaces();
        Map<String, String> namespaceMap = new HashMap<>();

        for (String uri : uris) {
            String prefix = tag.getPrefixByNamespace(uri);
            if (prefix != null && !prefix.isEmpty()) {
                namespaceMap.put(prefix, uri);
            }
        }

        Map<String, String> mergedNamespaceMap = new HashMap<>(EXPECTED_NAMESPACES);
        mergedNamespaceMap.putAll(namespaceMap);

        return mergedNamespaceMap;
    }

    public static final Map<String, String> EXPECTED_NAMESPACES = Map.of(
            "l", "/lib/layout",
            "f", "/lib/form",
            "t", "/lib/hudson");
}
