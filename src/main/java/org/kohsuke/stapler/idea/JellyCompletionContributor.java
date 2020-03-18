package org.kohsuke.stapler.idea;

import com.intellij.codeInsight.TailType;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupItem;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.XmlElementPattern;
import com.intellij.psi.impl.source.xml.TagNameReference;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlToken;
import com.intellij.util.ProcessingContext;
import com.intellij.xml.XmlElementDescriptor;
import org.jetbrains.annotations.NotNull;
import org.kohsuke.stapler.idea.descriptor.XmlNSDescriptorImpl;

/**
 * Tag name completion for Jelly tag libraries defined as tag files.
 *
 * <p>
 * One would think that contributing {@link XmlNSDescriptorImpl} and
 * implementing {@link XmlNSDescriptorImpl#getRootElementsDescriptors(XmlDocument)} is enough
 * to cause the IDEA XML module to show tag name completions, but apparently it is not.
 *
 * <p>
 * The problem is when we have text like the following:
 *
 * <pre><xmp>
   <j:jelly xmlns:t="/lib" xmlns:j="jelly:core">
       <html>
           <body>
               <!-- hit t, ctrl+space to make sure completion kicks in  -->
               <t:
           </body>
       </html>
   </j:jelly>
 * </xmp></pre>
 *
 * <p>
 * The html/body tags get dtd.XmlElementDescriptorImpl as their {@link XmlElementDescriptor}
 * (they represent in-memory for-this-document-only temporary DTDs), and unless the body tag
 * appear elsewhere and have some children, it will return null from its
 * {@link XmlElementDescriptor#getElementDescriptor(XmlTag, XmlTag)} when given &lt;t:something/>,
 * and {@link TagNameReference}, which calls {@link XmlNSDescriptorImpl#getRootElementsDescriptors(XmlDocument)}
 * to list up possible children, uses this as a signal that &lt;t:something/>
 * is not a valid child in this context.
 *
 * <p>
 * While the above doesn't really explain why &lt;j:*> can be completed in this context,
 * I eventually decided that just writing another {@link CompletionContributor} on its own
 * is the easiest thing to do here.
 *
 * <p>
 * So what this {@link CompletionContributor} does is to list up all the legal
 * Jelly tags available in the current namespace bindings as completion candidates.
 *
 * @author Kohsuke Kawaguchi
 */
public class JellyCompletionContributor extends CompletionContributor {
    public JellyCompletionContributor() {
        extend(CompletionType.BASIC, // in case of XML completion, this always seems to be BASIC
                XML_ELEMENT_NAME_PATTERN,
                new CompletionProvider<CompletionParameters>(true) {
                    // REFERENCE: spring plugin adds CompletionContributor as well.
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
                        XmlElement name = (XmlElement)parameters.getPosition();

                        // do this only inside Jelly files
                        if(!name.getContainingFile().getName().endsWith(".jelly"))
                            return;

                        // this pseudo-tag represents the tag being completed.
                        XmlTag tag = (XmlTag) name.getParent();
                        Module module = ModuleUtil.findModuleForPsiElement(tag);

                        /*
                        When completion is invoked after text like "<p:",
                        the following code gives us "p:".
                        This would potentially speed up the following "knownNamespaces" loop.
                        --------------------
                        System.out.println("Completing tag name for "+tag.getName());
                        PsiElement opos = parameters.getOriginalPosition();
                        System.out.println("originalPos:"+ opos);
                        if (opos instanceof XmlToken) {
                            XmlToken originalToken = (XmlToken) opos;
                            System.out.println(originalToken.getParent());
                        }
                        */

                        // just add every available jelly tags in the current namespaace
                        String[] uris = tag.knownNamespaces();
                        for (String uri : uris) {
                            String prefix = tag.getPrefixByNamespace(uri);
                            if(prefix!=null && prefix.length()>0)
                                prefix+=':';
                            XmlNSDescriptorImpl d = XmlNSDescriptorImpl.get(uri, module);
                            if(d!=null) {
                                for( XmlElementDescriptor e : d.getRootElementsDescriptors(null/*I'm not using this parameter*/)) {
                                    LookupItem item = LookupItem.fromString(prefix + e.getName());
                                    /*
                                        In some context (see completion-test.jelly in particular),
                                        I noticed that contributions from here and XmlCompletionContributer
                                        overlaps and the user ends up seeing the same candidate twice.
                                        To fix this, we need to create the LookupItem such that
                                        its equals() method returns true when compared with the ones
                                        from XmlCompletionContributer.

                                        So I'm not sure what the TailType means but this is why we do this.
                                     */
                                    item.setTailType(TailType.UNKNOWN);
                                    result.addElement(item);
                                }
                            }
                        }
                    }
                }
        );
    }

    // REFERENCE: the following is useful for figuring out how CompletionParameters
    // are populated and passed to us.
    //
    // On XML completion, type is always BASIC,
    // and pos for element/attribute name completions are XmlToken.
    //
    // TODO: XmlCompletionContributor is another implementation of CompletionContributer.
//    @Override
//    public boolean fillCompletionVariants(CompletionParameters parameters, CompletionResultSet result) {
//        System.out.println("type="+parameters.getCompletionType());
//        System.out.println("pos ="+parameters.getPosition());
//        return super.fillCompletionVariants(parameters, result);
//    }

    /**
     * {@link ElementPattern} that matches attribute names.
     */
    private static final ElementPattern XML_ATTRIBUTE_NAME_PATTERN =
            new XmlElementPattern(XmlToken.class) {}.withParent(XmlAttribute.class);

    /**
     * {@link ElementPattern} that matches attribute names.
     */
    private static final ElementPattern XML_ELEMENT_NAME_PATTERN =
            new XmlElementPattern(XmlToken.class) {}.withParent(XmlTag.class);
}
