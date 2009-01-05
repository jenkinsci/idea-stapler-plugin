package org.kohsuke.stapler.idea;

import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.xml.XmlElementDescriptor;
import com.intellij.xml.XmlAttributeDescriptor;
import org.kohsuke.stapler.idea.descriptor.XmlElementDescriptorImpl;
import org.kohsuke.stapler.idea.descriptor.XmlAttributeDescriptorImpl;
import org.kohsuke.stapler.idea.dom.model.DocumentationTag;

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
    public String getUrlFor(PsiElement element, PsiElement originalElement) {
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
    public String generateDoc(PsiElement element, PsiElement usage) {
        PsiElement p = usage.getParent();
        if (p instanceof XmlTag) {
            XmlTag t = (XmlTag) p;
            XmlElementDescriptor d = t.getDescriptor();
            if (d instanceof XmlElementDescriptorImpl) {
                XmlElementDescriptorImpl dd = (XmlElementDescriptorImpl) d;
                DocumentationTag doc = dd.getModel().getDocumentation();
                return doc.generateHtmlDoc();
            }
        }
        if (p instanceof XmlAttribute) {
            XmlAttribute a = (XmlAttribute) p;
            XmlAttributeDescriptor ad = a.getDescriptor();
            if (ad instanceof XmlAttributeDescriptorImpl) {
                XmlAttributeDescriptorImpl o = (XmlAttributeDescriptorImpl) ad;
                return o.getModel().generateHtmlDoc();
            }
        }
        return null;
    }

    public PsiElement getDocumentationElementForLookupItem(PsiManager psiManager, Object object, PsiElement element) {
        return null;
    }

    public PsiElement getDocumentationElementForLink(PsiManager psiManager, String link, PsiElement context) {
        return null;
    }
}
