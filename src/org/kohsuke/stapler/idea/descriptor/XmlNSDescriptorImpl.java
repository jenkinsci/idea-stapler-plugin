package org.kohsuke.stapler.idea.descriptor;

import com.intellij.xml.XmlNSDescriptor;
import com.intellij.xml.XmlElementDescriptor;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiDirectory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Kohsuke Kawaguchi
 */
public class XmlNSDescriptorImpl implements XmlNSDescriptor {
    /**
     * Namespace URI of a tag library.
     */
    public final String uri;

    /**
     * Directory full of tag files that defines this namespace.
     */
    public final PsiDirectory dir;

    public XmlNSDescriptorImpl(String uri, PsiDirectory dir) {
        this.uri = uri;
        this.dir = dir;
    }

    public XmlElementDescriptor getElementDescriptor(@NotNull XmlTag tag) {
        PsiFile f = dir.findFile(tag.getLocalName() + ".jelly");
        if (f instanceof XmlFile)
            return new XmlElementDescriptorImpl(this, (XmlFile)f);
        return null;
    }

    @NotNull
    public XmlElementDescriptor[] getRootElementsDescriptors(@Nullable XmlDocument document) {
        return new XmlElementDescriptor[0];
    }

    public XmlFile getDescriptorFile() {
        return null;
    }

    public boolean isHierarhyEnabled() {
        return true; // ???
    }

    public PsiElement getDeclaration() {
        return dir.findFile("taglib");
    }

    public String getName(PsiElement context) {
        return uri;
    }

    public String getName() {
        return uri;
    }

    public void init(PsiElement element) {
    }

    public Object[] getDependences() {
        return new Object[]{dir};
    }
}
