package org.kohsuke.stapler.idea;

import static io.jenkins.stapler.idea.jelly.NamespaceUtil.collectProjectNamespaces;
import static io.jenkins.stapler.idea.jelly.suggestions.XsdParser.getTagsFromSchema;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlToken;
import com.intellij.util.ProcessingContext;
import com.intellij.xml.XmlAttributeDescriptor;
import com.intellij.xml.XmlElementDescriptor;
import io.jenkins.stapler.idea.jelly.suggestions.JellyElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.kohsuke.stapler.idea.descriptor.StaplerCustomJellyTagLibraryXmlNSDescriptor;
import org.kohsuke.stapler.idea.icons.Icons;

/**
 * Provides tag name completion for Jelly templates.
 *
 * <p>IntelliJ offers completions automatically, however these aren't extensible and don't work for custom taglibs. As a
 * result of this we've created our own custom completion contributor to handle completions
 *
 * <ul>
 *   <li>Suggests components from both default and user-defined namespaces.
 *   <li>Automatically imports namespaces if they are not already present.
 *   <li>Generates templates for components with required attributes, offering them to the user as completion
 *       suggestions.
 * </ul>
 */
public class JellyCompletionContributor extends CompletionContributor {

    /**
     * Core namespaces to suggest for Jelly files that do not yet define any namespaces. These namespaces ensure that
     * autocomplete functionality remains useful even in files without declared namespaces.
     */
    private static final Map<String, String> CORE_NAMESPACES = Map.of(
            "l", "/lib/layout",
            "f", "/lib/form",
            "t", "/lib/hudson",
            "j", "jelly:core",
            "st", "jelly:stapler",
            "d", "jelly:define");

    public JellyCompletionContributor() {
        extend(
                CompletionType.BASIC,
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

                        // Disable IntelliJ's default suggestions
                        result.stopHere();

                        createMergedNamespaceMap(tag).forEach((prefix, uri) -> {
                            // If a user has already provided a prefix then only include results from that namespace
                            String existingPrefix = getPrefix(tag.getName());
                            if (existingPrefix != null && !existingPrefix.equalsIgnoreCase(prefix)) {
                                return;
                            }

                            List<JellyElement> descriptors = getDescriptorsFromNamespace(uri, module, tag);
                            descriptors.forEach(descriptor ->
                                    addAutocompleteElement(result, prefix, uri, descriptor, existingPrefix != null));
                        });
                    }
                });
    }

    private static List<JellyElement> getDescriptorsFromNamespace(String uri, Module module, XmlTag tag) {
        // We don't have access to 'jelly:' descriptors, so pull them from our own resources
        if (uri.startsWith("jelly:")) {
            return getTagsFromSchema(uri.substring(6)).stream()
                    .map(e -> new JellyElement(
                            e,
                            XmlElementDescriptor.CONTENT_TYPE_EMPTY,
                            Collections.emptyList(),
                            AllIcons.FileTypes.Xhtml))
                    .toList();
        }

        StaplerCustomJellyTagLibraryXmlNSDescriptor tagLibrary =
                StaplerCustomJellyTagLibraryXmlNSDescriptor.get(uri, module);

        if (tagLibrary == null) {
            return List.of();
        }

        return Arrays.stream(tagLibrary.getTagDescriptors()).toList().stream()
                .map(e -> new JellyElement(
                        e.getName(), e.getContentType(), List.of(e.getAttributesDescriptors(tag)), Icons.COMPONENT))
                .collect(Collectors.toList());
    }

    private static void addAutocompleteElement(
            CompletionResultSet result, String prefix, String uri, JellyElement component, boolean includePrefix) {
        result.addElement(LookupElementBuilder.create((includePrefix ? "" : prefix + ":") + component.name())
                .withPresentableText(prefix + ":" + component.name())
                .withIcon(component.icon())
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
                            createRequiredAttributesTemplate(component, project, editor, prefix);
                        }
                    }
                }));
    }

    private static void createRequiredAttributesTemplate(
            JellyElement component, Project project, Editor editor, String prefix) {
        TemplateManager templateManager = TemplateManager.getInstance(project);
        StringBuilder templateText = new StringBuilder();

        List<String> requiredAttributes = getRequiredAttributes(component);
        for (String attributeName : requiredAttributes) {
            templateText
                    .append(" ")
                    .append(attributeName)
                    .append("=\"$")
                    .append(attributeName)
                    .append("$\"");
        }

        if (component.contentType() == XmlElementDescriptor.CONTENT_TYPE_ANY) {
            templateText
                    .append(">\n  $content$\n</")
                    .append(prefix)
                    .append(":")
                    .append(component.name())
                    .append(">");
        } else {
            templateText.append(" />");
        }

        Template template = templateManager.createTemplate("myTemplate", "Jelly", templateText.toString());

        // Add each attribute as a placeholder variable for tabbing
        for (String attributeName : requiredAttributes) {
            template.addVariable(attributeName, "", attributeName.toLowerCase(), true);
        }
        if (component.contentType() == XmlElementDescriptor.CONTENT_TYPE_ANY) {
            template.addVariable("content", "", "content", true);
        }

        templateManager.startTemplate(editor, template);
    }

    private static List<String> getRequiredAttributes(JellyElement component) {
        List<String> requiredAttributes = new ArrayList<>();
        for (XmlAttributeDescriptor attributesDescriptor : component.attributesDescriptors()) {
            if (attributesDescriptor.isRequired()) {
                requiredAttributes.add(attributesDescriptor.getName());
            }
        }
        return requiredAttributes;
    }

    private static String getPrefix(String tagName) {
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

    /**
     * Creates a merged map of namespaces with their corresponding URIs, combining core namespaces, project-specific
     * namespaces, and namespaces declared in the current file, in that order of precedence.
     */
    private static Map<String, String> createMergedNamespaceMap(XmlTag tag) {
        Map<String, String> projectNamespaces = collectProjectNamespaces(tag.getProject());
        String[] uris = tag.knownNamespaces();
        Map<String, String> namespaceMap = new HashMap<>();

        for (String uri : uris) {
            String prefix = tag.getPrefixByNamespace(uri);
            if (prefix != null && !prefix.isEmpty()) {
                namespaceMap.put(prefix, uri);
            }
        }

        Map<String, String> mergedNamespaceMap = new HashMap<>(CORE_NAMESPACES);
        mergedNamespaceMap.putAll(projectNamespaces);
        mergedNamespaceMap.putAll(namespaceMap);

        return mergedNamespaceMap;
    }
}
