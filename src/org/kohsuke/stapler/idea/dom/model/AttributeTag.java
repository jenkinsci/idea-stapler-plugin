package org.kohsuke.stapler.idea.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Implementation;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kohsuke Kawaguchi
 */
@Implementation(AttributeTag.Impl.class)
public interface AttributeTag extends DomElement {
    @Attribute
    GenericAttributeValue<String> getName();

    /**
     * Return the name attribute value, except that if it's not specified,
     * this method returns "" to avoid NPE.
     */
    @NotNull
    String getSafeName();

    public static abstract class Impl implements AttributeTag {
        @NotNull
        public String getSafeName() {
            String s = getName().getStringValue();
            if(s==null) return "";
            return s;
        }
    }
}
