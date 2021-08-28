package org.kohsuke.stapler.idea.dom.model;

import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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
        List<AttributeTag> r = new ArrayList<>();
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
