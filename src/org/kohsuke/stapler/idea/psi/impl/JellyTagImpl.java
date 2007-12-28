package org.kohsuke.stapler.idea.psi.impl;

import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.util.TextRange;
import org.kohsuke.stapler.idea.psi.JellyPsi;
import org.kohsuke.stapler.idea.psi.JellyTag;
import org.kohsuke.stapler.idea.psi.TagDefinition;
import org.kohsuke.stapler.idea.psi.ref.TagReference;

/**
 * @author Kohsuke Kawaguchi
 */
final class JellyTagImpl extends JellyPsiImpl implements JellyTag {
    JellyTagImpl(JellyPsi parent, XmlTag sourceElement) {
        super(parent, sourceElement);
    }

    public XmlTag getSourceElement() {
        return (XmlTag)super.getSourceElement();
    }

    public TagDefinition getDefinition() {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public PsiReference getReference() {
        return new TagReference(getSourceElement());
    }
}
