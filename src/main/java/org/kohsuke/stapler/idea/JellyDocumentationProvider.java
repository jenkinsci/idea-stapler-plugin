package org.kohsuke.stapler.idea;

import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import com.intellij.xml.XmlAttributeDescriptor;
import com.intellij.xml.XmlElementDescriptor;
import org.kohsuke.stapler.idea.descriptor.XmlAttributeDescriptorImpl;
import org.kohsuke.stapler.idea.descriptor.XmlElementDescriptorImpl;
import org.kohsuke.stapler.idea.dom.model.DocumentationTag;

import java.util.List;

/**
 * @author Kohsuke Kawaguchi
 */
public class JellyDocumentationProvider implements DocumentationProvider {
    public String getQuickNavigateInfo(PsiElement element) {
        return null;
    }

    /**
     * Upon returning non-null from {@link #generateDoc(PsiElement, PsiElement)},
     * I noticed that this method is getting invoked.
     */
    @Override
    public List<String> getUrlFor(PsiElement element, PsiElement originalElement) {
        return null;
    }

    /**
     * This method is called upon Ctrl+Q on usages.
     *
     * @param element
     *      This represents the declaration, not the usage.
     * @param usage
     *      This is where Ctrl+Q is invoked.
     */
    @Override
    public String generateDoc(PsiElement element, PsiElement usage) {
        PsiElement p = usage.getParent();
        if (p instanceof XmlTag) {
            XmlTag t = (XmlTag) p;
            XmlElementDescriptor d = t.getDescriptor();
            if (d instanceof XmlElementDescriptorImpl) {
                XmlElementDescriptorImpl dd = (XmlElementDescriptorImpl) d;
                DocumentationTag m = dd.getModel();
                if(m==null)     return null;
                return m.generateHtmlDoc();
            }
        } else
        if (p instanceof XmlAttribute) {
            XmlAttribute a = (XmlAttribute) p;
            XmlAttributeDescriptor ad = a.getDescriptor();
            if (ad instanceof XmlAttributeDescriptorImpl) {
                XmlAttributeDescriptorImpl o = (XmlAttributeDescriptorImpl) ad;
                return o.getModel().generateHtmlDoc();
            }
        } else {
            // if the nearest namespaced tag is <st:documentation> or <st:attribute>,
            // render that document. This is just like what happens when you hit Ctrl+Q
            // inside javadoc.
            for( XmlTag tag = PsiTreeUtil.getParentOfType(usage, XmlTag.class);
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

    @Override
    public PsiElement getDocumentationElementForLookupItem(PsiManager psiManager, Object object, PsiElement element) {
        return null;
    }

    @Override
    public PsiElement getDocumentationElementForLink(PsiManager psiManager, String link, PsiElement context) {
        return null;
    }

    @Override
    public String getQuickNavigateInfo(PsiElement psiElement, PsiElement psiElement1) {
        return null;
    }
}
