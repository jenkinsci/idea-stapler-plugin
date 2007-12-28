package org.kohsuke.stapler.idea.psi.impl;

import com.intellij.psi.xml.XmlAttribute;
import org.kohsuke.stapler.idea.psi.JellyAttribute;
import org.kohsuke.stapler.idea.psi.JellyPsi;
import org.kohsuke.stapler.idea.psi.JellyTag;
import org.jetbrains.annotations.NotNull;

/**
 * {@link JellyAttribute} implementation.
 * 
 * @author Kohsuke Kawaguchi
 */
final class JellyAttributeImpl extends JellyPsiImpl implements JellyAttribute {
    JellyAttributeImpl(JellyPsi parent, XmlAttribute sourceElement) {
        super(parent, sourceElement);
    }

    @NotNull
    public XmlAttribute getSourceElement() {
        return (XmlAttribute)super.getSourceElement();
    }

    public JellyTag getParent() {
        return (JellyTag)super.getParent();
    }

    public JellyTag getJellyParent() {
        return (JellyTag)super.getJellyParent();
    }
}

