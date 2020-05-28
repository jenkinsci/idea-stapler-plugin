package org.kohsuke.stapler.idea.dom.model;

import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlTagChild;
import net.java.textilej.parser.MarkupParser;
import net.java.textilej.parser.builder.HtmlDocumentBuilder;
import net.java.textilej.parser.markup.confluence.ConfluenceDialect;
import org.jetbrains.annotations.NotNull;

import java.io.StringWriter;

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
        // PSI returns "&lt;" in XML as "&lt;" instead of its proper infose '<',
        // so we have to take care of them here:
        int idx;
        while((idx=buf.indexOf("&lt;"))>=0)
            buf.replace(idx,idx+4,"<");
        while((idx=buf.indexOf("&gt;"))>=0)
            buf.replace(idx,idx+4,">");
        while((idx=buf.indexOf("&amp;"))>=0)
            buf.replace(idx,idx+4,"&");

        StringWriter w = new StringWriter();
        MarkupParser parser = new MarkupParser(new ConfluenceDialect());
        HtmlDocumentBuilder builder = new HtmlDocumentBuilder(w) {
            @Override
            public void lineBreak() {
                // no line break since IDEs usually don't wrap text. 
            }
        };

        builder.setEmitAsDocument(false);
        parser.setBuilder(builder);
        parser.parse(buf.toString());
        return w.toString();
    }
}
