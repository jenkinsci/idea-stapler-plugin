package org.kohsuke.stapler.idea.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.Implementation;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlTagChild;

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

    /**
     * Generates documentation in HTML.
     */
    String generateHtmlDoc();

    public static abstract class Impl implements DocumentationTag {
        public AttributeTag getAttributeByName(String name) {
            for(AttributeTag a : getAttributes())
                if(a.getSafeName().equals(name))
                    return a;
            return null;
        }

        public String generateHtmlDoc() {
            XmlTag tag = getXmlTag();
            if(tag==null)   return null; // being defensive

            StringBuilder buf = new StringBuilder();
            for (XmlTagChild child : tag.getValue().getChildren()) {
                if (child instanceof XmlTag) {
                    XmlTag childTag = (XmlTag) child;
                    if(childTag.getLocalName().equals("attribute") && childTag.getNamespace().equals("jelly:stapler"))
                        continue; // skip <st:attribute />
                }
                buf.append(child.getText());
            }
            return buf.toString();
        }
    }
}
