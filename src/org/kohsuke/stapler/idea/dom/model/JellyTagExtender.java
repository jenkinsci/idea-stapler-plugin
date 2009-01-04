package org.kohsuke.stapler.idea.dom.model;

import com.intellij.util.xml.reflect.DomExtender;
import com.intellij.util.xml.reflect.DomExtensionsRegistrar;
import com.intellij.util.xml.XmlName;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kohsuke Kawaguchi
 */
public class JellyTagExtender extends DomExtender<JellyTag> {
    public void registerExtensions(@NotNull JellyTag tag, @NotNull DomExtensionsRegistrar registrar) {
        // this alone won't trigger auto-completion nor error highlighting on value types
        // correction: it does try to complete 0.
        for(int i=0;i<10;i++)
            registrar.registerGenericAttributeValueChildExtension(new XmlName("test"+i),Integer.class);
        registrar.registerFixedNumberChildExtension(new XmlName("foo"),JellyTag.class);
        registrar.registerFixedNumberChildExtension(new XmlName("bar"),JellyTag.class);
        registrar.registerFixedNumberChildExtension(new XmlName("baz"),JellyTag.class);
    }
}
