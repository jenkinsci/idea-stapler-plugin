package org.kohsuke.stapler.idea.dom.model;

import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * {@link XmlTag} that wraps &lt;st:attribute>.
 *
 * @author Kohsuke Kawaguchi
 */
public class AttributeTag extends TagWithHtmlContent {
    public AttributeTag(@NotNull XmlTag tag) {
        super(tag);
    }

    /**
     * Return the name attribute value, except that if it's not specified,
     * this method returns "" to avoid NPE.
     */
    @NotNull
    public String getName() {
        return Optional.ofNullable(tag.getAttribute("name"))
                .map(XmlAttribute::getValue)
                .orElse("");
    }

    public boolean isRequired() {
        Optional<String> mayBeUseAttrValue = Optional.ofNullable(tag.getAttribute("use")).map(XmlAttribute::getValue);
        return mayBeUseAttrValue.isPresent() && mayBeUseAttrValue.get().equals("required");
    }
}
