package org.kohsuke.stapler.idea.psi;

import com.intellij.psi.xml.XmlAttribute;

/**
 * Attribute name and value.
 * @author Kohsuke Kawaguchi
 */
public interface JellyAttribute extends JellyPsi {
    XmlAttribute getSourceElement();
    JellyTag getParent();
    JellyTag getJellyParent();
}

