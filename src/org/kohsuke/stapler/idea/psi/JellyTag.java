package org.kohsuke.stapler.idea.psi;

import com.intellij.psi.xml.XmlTag;

/**
 * @author Kohsuke Kawaguchi
 */
public interface JellyTag extends JellyPsi {
    XmlTag getSourceElement();

    TagDefinition getDefinition();
}
