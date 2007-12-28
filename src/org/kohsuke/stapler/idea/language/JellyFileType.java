package org.kohsuke.stapler.idea.language;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

import org.jetbrains.annotations.NotNull;
import org.kohsuke.stapler.idea.icons.Icons;

/**
 * @author Kohsuke Kawaguchi
 */
public final class JellyFileType extends LanguageFileType {
    public static final JellyFileType INSTANCE = new JellyFileType();

    private JellyFileType() {
        super(JellyLanguage.INSTANCE);
    }

    @NotNull
    public String getName() {
        return "Jelly";
    }

    @NotNull
    public String getDescription() {
        return "Jelly view files";
    }

    @NotNull
    public String getDefaultExtension() {
        return "jelly";
    }

    public Icon getIcon() {
        return Icons.STAPLER;
    }
}
