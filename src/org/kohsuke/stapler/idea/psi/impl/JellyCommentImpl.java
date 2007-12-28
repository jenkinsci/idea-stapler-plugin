package org.kohsuke.stapler.idea.psi.impl;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlElementType;
import com.intellij.psi.xml.XmlComment;
import org.kohsuke.stapler.idea.psi.JellyComment;
import org.kohsuke.stapler.idea.psi.JellyPsi;

/**
 * @author Kohsuke Kawaguchi
 */
final class JellyCommentImpl extends JellyPsiImpl implements JellyComment {
    JellyCommentImpl(JellyPsi parent, XmlComment sourceElement) {
        super(parent, sourceElement);
    }

    public IElementType getTokenType() {
        return XmlElementType.XML_COMMENT;
    }
}

