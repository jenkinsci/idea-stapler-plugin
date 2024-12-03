package org.kohsuke.stapler.idea.language;

import com.intellij.ide.highlighter.XmlLikeFileType;
import javax.swing.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kohsuke.stapler.idea.icons.Icons;

/**
 * Jelly file type definition
 *
 * @author Julien Greffe
 */
public class JellyFileType extends XmlLikeFileType {

    public static final JellyFileType INSTANCE = new JellyFileType();

    public JellyFileType() {
        super(JellyLanguage.INSTANCE);
    }

    @Override
    public @NonNls @NotNull String getName() {
        return "Jelly";
    }

    @Override
    public @NotNull String getDescription() {
        return "Jelly";
    }

    @Override
    public @NotNull String getDefaultExtension() {
        return "jelly";
    }

    @Override
    public @Nullable Icon getIcon() {
        return Icons.JELLY;
    }
}
