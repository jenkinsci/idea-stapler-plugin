package org.kohsuke.stapler.idea.dom.model;

import com.intellij.util.xml.DomFileDescription;
import com.intellij.psi.xml.XmlFile;
import com.intellij.openapi.module.Module;
import org.kohsuke.stapler.idea.dom.model.JellyTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Establishes the DOM model for Jelly scripts.
 *
 * @author Kohsuke Kawaguchi
 */
public class JellyDomDescription extends DomFileDescription<JellyTag>  {
    public JellyDomDescription() {
        super(JellyTag.class, "jelly", "jelly:core");
    }

    @Override
    public boolean isMyFile(@NotNull XmlFile file, @Nullable Module module) {
        return file.getName().endsWith(".jelly");
    }
}
