package org.kohsuke.stapler.idea.dom.model;

import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlTagChild;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kohsuke Kawaguchi
 */
public abstract class TagWithHtmlContent {
    public final XmlTag tag;

    TagWithHtmlContent(@NotNull XmlTag tag) {
        this.tag = tag;
    }

    /**
     * Generates documentation in HTML.
     */
    public String generateHtmlDoc() {
        StringBuilder buf = new StringBuilder();
        for (XmlTagChild child : tag.getValue().getChildren()) {
            if (child instanceof XmlTag) {
                XmlTag childTag = (XmlTag) child;
                if(childTag.getNamespace().equals("jelly:stapler"))
                    continue; // skip <st:* />
            }
            buf.append(child.getText());
        }
        return buf.toString();
    }
}
