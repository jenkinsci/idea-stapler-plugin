package org.kohsuke.stapler.idea.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Implementation;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kohsuke Kawaguchi
 */
@Implementation(AttributeTag.Impl.class)
public interface AttributeTag extends DomElement {
    @Attribute
    GenericAttributeValue<String> getName();

    @Attribute
    GenericAttributeValue<String> getUse();

    /**
     * Return the name attribute value, except that if it's not specified,
     * this method returns "" to avoid NPE.
     */
    @NotNull
    String getSafeName();

    boolean isRequired();

    /**
     * Generates documentation in HTML.
     */
    String generateHtmlDoc();

    public static abstract class Impl implements AttributeTag {
        @NotNull
        public String getSafeName() {
            String s = getName().getStringValue();
            if(s==null) return "";
            return s;
        }

        public boolean isRequired() {
            return "required".equals(getUse().getStringValue());
        }

        public String generateHtmlDoc() {
            XmlTag t = getXmlTag();
            if(t==null)     return null;

            return t.getValue().getText();
        }
    }
}
