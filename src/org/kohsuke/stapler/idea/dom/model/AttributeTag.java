package org.kohsuke.stapler.idea.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;

/**
 * @author Kohsuke Kawaguchi
 */
public interface AttributeTag extends DomElement {
    @Attribute
    GenericAttributeValue<String> getName();
}
