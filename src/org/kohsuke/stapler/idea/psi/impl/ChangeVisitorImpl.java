package org.kohsuke.stapler.idea.psi.impl;

import com.intellij.openapi.util.TextRange;
import com.intellij.pom.xml.XmlChangeVisitor;
import com.intellij.pom.xml.events.XmlAttributeSet;
import com.intellij.pom.xml.events.XmlDocumentChanged;
import com.intellij.pom.xml.events.XmlElementChanged;
import com.intellij.pom.xml.events.XmlTagChildAdd;
import com.intellij.pom.xml.events.XmlTagChildChanged;
import com.intellij.pom.xml.events.XmlTagChildRemoved;
import com.intellij.pom.xml.events.XmlTagNameChanged;
import com.intellij.pom.xml.events.XmlTextChanged;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLock;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.Alarm;
import org.kohsuke.stapler.idea.language.JellyLanguage;
import org.kohsuke.stapler.idea.psi.JellyFile;
import org.kohsuke.stapler.idea.psi.JellyPsi;

import java.util.HashSet;
import java.util.Set;

/**
 * This code is responsible for invalidating Jelly PSI tree whenever a change is made.
 * 
 * @author Kohsuke Kawaguchi
 */
public class ChangeVisitorImpl implements XmlChangeVisitor {
    public static final ChangeVisitorImpl INSTANCE = new ChangeVisitorImpl();

    public void visitXmlAttributeSet(XmlAttributeSet xmlAttributeSet) {
        XmlTag tag = xmlAttributeSet.getTag();
        clearParentCaches(tag);
    }

    public void visitDocumentChanged(XmlDocumentChanged xmlDocumentChanged) {
        XmlDocument doc = xmlDocumentChanged.getDocument();
        JellyFile j = getAntFile(doc);
        if (j != null)
            j.clearCaches();
    }

    public void visitXmlElementChanged(XmlElementChanged xmlElementChanged) {
        clearParentCaches(xmlElementChanged.getElement());
    }

    public void visitXmlTagChildAdd(XmlTagChildAdd xmlTagChildAdd) {
        clearParentCaches(xmlTagChildAdd.getChild());
    }

    public void visitXmlTagChildChanged(XmlTagChildChanged xmlTagChildChanged) {
        clearParentCaches(xmlTagChildChanged.getChild());
    }

    public void visitXmlTagChildRemoved(XmlTagChildRemoved xmlTagChildRemoved) {
        clearParentCaches(xmlTagChildRemoved.getTag());
    }

    public void visitXmlTagNameChanged(XmlTagNameChanged xmlTagNameChanged) {
        clearParentCaches(xmlTagNameChanged.getTag());
    }

    public void visitXmlTextChanged(XmlTextChanged xmlTextChanged) {
        clearParentCaches(xmlTextChanged.getText());
    }

    private static void clearParentCaches(XmlElement el) {
        TextRange textRange = el.getTextRange();
        JellyFile file = getAntFile(el);
        if (file == null)
            return;
        PsiElement element = file.findElementAt(textRange.getStartOffset());

        if (element != null)
            do
                element = element.getParent();
            while (element != null && !(element instanceof JellyFile) && (!element.isValid() || element.getTextLength() <= textRange.getLength()));
        if (element == null)
            element = file;

        synchronized (PsiLock.LOCK) {
            ((JellyPsi) element).clearCaches();
        }
    }

    private static JellyFile getAntFile(XmlElement el) {
        return JellyLanguage.getJellyFile(el.getContainingFile());
    }
}
