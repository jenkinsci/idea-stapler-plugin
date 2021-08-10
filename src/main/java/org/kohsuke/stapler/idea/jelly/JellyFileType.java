package org.kohsuke.stapler.idea.jelly;

import com.intellij.icons.AllIcons;
import com.intellij.ide.highlighter.XmlLikeFileType;
import com.intellij.lang.xml.XMLLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class JellyFileType extends XmlLikeFileType {
    public static final JellyFileType INSTANCE = new JellyFileType();

    protected JellyFileType() {
        super(XMLLanguage.INSTANCE);
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
        return AllIcons.FileTypes.Xml;
    }
}
