package org.kohsuke.stapler.idea.psi;

import com.intellij.psi.xml.XmlAttributeValue;
import org.jetbrains.annotations.NotNull;

/**
 * Attribute value (like abc=<b>"foo"</b>)
 * @author Kohsuke Kawaguchi
 */
public interface JellyAttributeValue extends JellyPsi {
    @NotNull
    XmlAttributeValue getSourceElement();
    JellyAttribute getParent();
    JellyAttribute getJellyParent();
}
