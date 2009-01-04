package org.kohsuke.stapler.idea.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DefinesXml;

/**
 * @author Kohsuke Kawaguchi
 */
// @DefinesXml // tells IntelliJ that auto-completion and error checks are driven by DomElement
            // (in this case it's driven by JellyTagExtender)
public interface JellyTag extends DomElement {
    // this seems to ignore namespace URI, even though
    // we just want <st:documentation>.
    DocumentationTag getDocumentation();
}
