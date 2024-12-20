package io.jenkins.stapler.idea.jelly.symbols;

import com.intellij.openapi.fileTypes.FileType;
import org.jetbrains.annotations.NotNull;

public class SvgFileType implements FileType {
    public static final SvgFileType INSTANCE = new SvgFileType();

    @Override
    public @NotNull String getName() {
        return "SVG";
    }

    @Override
    public @NotNull String getDescription() {
        return "SVG files";
    }

    @Override
    public @NotNull String getDefaultExtension() {
        return "svg";
    }

    @Override
    public javax.swing.Icon getIcon() {
        return null;
    }

    @Override
    public boolean isBinary() {
        return false;
    }
}
