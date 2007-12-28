package org.kohsuke.stapler.idea.psi.impl;

import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlAttribute;
import org.kohsuke.stapler.idea.psi.JellyAttributeValue;
import org.kohsuke.stapler.idea.psi.JellyPsi;
import org.kohsuke.stapler.idea.psi.JellyTag;
import org.kohsuke.stapler.idea.psi.JellyAttribute;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kohsuke Kawaguchi
 */
final class JellyAttributeValueImpl extends JellyPsiImpl implements JellyAttributeValue {
    JellyAttributeValueImpl(JellyPsi parent, XmlAttributeValue sourceElement) {
        super(parent, sourceElement);
    }

    @NotNull
    public XmlAttributeValue getSourceElement() {
        return (XmlAttributeValue)super.getSourceElement();
    }

    public JellyAttribute getParent() {
        return (JellyAttribute)super.getParent();
    }

    public JellyAttribute getJellyParent() {
        return (JellyAttribute)super.getJellyParent();
    }
}
