package org.kohsuke.stapler.idea.psi.impl;

import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlTag;
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
        XmlTag src = getSourceElement();
        if(TagReference.isApplicable(src))
            return new TagReference(src);
        return null;
    }
}
