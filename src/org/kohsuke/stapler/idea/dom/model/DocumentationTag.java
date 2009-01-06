package org.kohsuke.stapler.idea.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.Implementation;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlTagChild;

import java.util.List;
import java.util.ArrayList;

import org.jetbrains.annotations.NotNull;

/**
 * Wraps {@link XmlTag} for &lt;st:documentation> element.
 *
 * @author Kohsuke Kawaguchi
 */
public final class DocumentationTag extends TagWithHtmlContent {
    public DocumentationTag(@NotNull XmlTag tag) {
        super(tag);
    }

    public List<AttributeTag> getAttributes() {
        List<AttributeTag> r = new ArrayList<AttributeTag>();
        for( XmlTag a : tag.findSubTags("attribute","jelly:stapler") )
            r.add(new AttributeTag(a));
        return r;
    }

    /**
     * Looks up {@link AttributeTag} by their {@link AttributeTag#getName()}.
     */
    public AttributeTag getAttribute(String name) {
        for( XmlTag a : tag.findSubTags("attribute","jelly:stapler") ) {
            AttributeTag x = new AttributeTag(a);
            if(x.getName().equals(name))
                return x;
        }
        return null;
    }
}
