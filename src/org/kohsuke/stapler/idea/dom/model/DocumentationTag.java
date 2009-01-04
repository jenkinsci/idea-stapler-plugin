package org.kohsuke.stapler.idea.dom.model;

import com.intellij.util.xml.DomElement;

import java.util.List;

/**
 * @author Kohsuke Kawaguchi
 */
public interface DocumentationTag extends DomElement {
    List<AttributeTag> getAttributes();
}
