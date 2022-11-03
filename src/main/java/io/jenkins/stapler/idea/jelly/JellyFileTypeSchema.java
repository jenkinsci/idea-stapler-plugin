package io.jenkins.stapler.idea.jelly;

import com.intellij.internal.statistic.collectors.fus.fileTypes.FileTypeUsageSchemaDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

public class JellyFileTypeSchema implements FileTypeUsageSchemaDescriptor {
    /**
     * Default extension of the Apache Commons Jelly
     */
    public static final String JELLY_EXTENSION = "jelly";
    /**
     * Extension introduced by Stapler Framework to differentiate Jelly views from Tag Libraries that are made up of script files.
     * For example:
     * <a href="https://github.com/jenkinsci/stapler/blob/1709.ve4c10835694b_/jelly/src/main/java/org/kohsuke/stapler/jelly/CustomTagLibrary.java#L128">CustomTagLibrary.java#L128</a>
     * <a href="https://github.com/jenkinsci/stapler/blob/1709.ve4c10835694b_/jelly/src/main/java/org/kohsuke/stapler/jelly/ThisTagLibrary.java#L77">ThisTagLibrary.java#L77</a>
     */
    public static final String STAPLER_JELLY_TAG_EXTENSION = "jellytag";

    @Override
    public boolean describes(@NotNull Project project, @NotNull VirtualFile file) {
        return isJelly(file);
    }

    private static boolean isJelly(@NotNull VirtualFile file) {
        String fileExtension = file.getExtension();
        return JELLY_EXTENSION.equals(fileExtension) || STAPLER_JELLY_TAG_EXTENSION.equals(fileExtension);
    }
}
