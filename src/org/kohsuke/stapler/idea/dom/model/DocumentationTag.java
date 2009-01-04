package org.kohsuke.stapler.idea.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.Implementation;

import java.util.List;

/**
 * &lt;st:documentation> element.
 *
 * @author Kohsuke Kawaguchi
 */
@Implementation(DocumentationTag.Impl.class)
public interface DocumentationTag extends DomElement {
    List<AttributeTag> getAttributes();

    /**
     * Looks up {@link AttributeTag} by their {@link AttributeTag#getSafeName()}.
     */
    AttributeTag getAttributeByName(String name);

    public static abstract class Impl implements DocumentationTag {
        public AttributeTag getAttributeByName(String name) {
            for(AttributeTag a : getAttributes())
                if(a.getSafeName().equals(name))
                    return a;
            return null;
        }
    }
}
