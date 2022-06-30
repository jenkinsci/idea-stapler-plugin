package org.kohsuke.stapler.idea;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import com.intellij.xml.XmlAttributeDescriptor;
import com.intellij.xml.XmlElementDescriptor;
import org.jetbrains.annotations.Nullable;
import org.kohsuke.stapler.idea.descriptor.StaplerCustomJellyTagfileXmlAttributeDescriptor;
import org.kohsuke.stapler.idea.descriptor.StaplerCustomJellyTagfileXmlElementDescriptor;
import org.kohsuke.stapler.idea.dom.model.DocumentationTag;

/**
 * @author Kohsuke Kawaguchi
 */
public class JellyDocumentationProvider extends AbstractDocumentationProvider {

    /**
     * This method is called upon Ctrl+Q on usages.
     *
     * @param element
     *      This represents the declaration, not the originalElement.
     * @param originalElement
     *      This is where Ctrl+Q is invoked.
     */
    @Override
    public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        if (originalElement == null) {
            return null;
        }
        PsiElement p = originalElement.getParent();
        if (p instanceof XmlTag) {
            XmlTag t = (XmlTag) p;
            XmlElementDescriptor d = t.getDescriptor();
            if (d instanceof StaplerCustomJellyTagfileXmlElementDescriptor) {
                StaplerCustomJellyTagfileXmlElementDescriptor dd = (StaplerCustomJellyTagfileXmlElementDescriptor) d;
                DocumentationTag m = dd.getModel();
                if(m==null)     return null;
                return m.generateHtmlDoc();
            }
        } else
        if (p instanceof XmlAttribute) {
            XmlAttribute a = (XmlAttribute) p;
            XmlAttributeDescriptor ad = a.getDescriptor();
            if (ad instanceof StaplerCustomJellyTagfileXmlAttributeDescriptor) {
                StaplerCustomJellyTagfileXmlAttributeDescriptor o = (StaplerCustomJellyTagfileXmlAttributeDescriptor) ad;
                return o.getModel().generateHtmlDoc();
            }
        } else {
            // if the nearest namespaced tag is <st:documentation> or <st:attribute>,
            // render that document. This is just like what happens when you hit Ctrl+Q
            // inside javadoc.
            for( XmlTag tag = PsiTreeUtil.getParentOfType(originalElement, XmlTag.class);
                 tag!=null;
                 tag = tag.getParentTag() ) {
                String ns = tag.getNamespace();
                if(ns.equals("jelly:stapler")) {
                    String ln = tag.getLocalName();
                    if(ln.equals("documentation") || ln.equals("attribute")) {
                        // to be pedantic, it could be new AttributeTag as well,
                        // but that doesn't make any difference in the end result.
                        return new DocumentationTag(tag).generateHtmlDoc();
                    }
                }
                if(!ns.equals(""))
                    break;
            }
        }
        return null;
    }
}
