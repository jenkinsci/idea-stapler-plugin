package org.kohsuke.stapler.idea.extension;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.ide.structureView.StructureViewExtension;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.text.StringUtilRt;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract class for jelly/java structure views
 *
 * @author Julien Greffe
 */
public abstract class AbstractStructureViewExtension implements StructureViewExtension {

    private final static String SRC_JAVA = "src/main/java";
    private final static String SRC_RESOURCES = "src/main/resources";

    /**
     * Replace src/main/java to src/main/resources (location of expected related jelly files)
     */
    protected String getExpectedJellyParentPath(String path) {
        return path.replace(SRC_JAVA, SRC_RESOURCES);
    }

    protected String getExpectedJavaPath(String path) {
        return path.replace(SRC_RESOURCES, SRC_JAVA);
    }

    /**
     * Construct path without extension
     */
    protected CharSequence getPathWithoutExtension(PsiElement javaFile) {
        CharSequence path = javaFile.getContainingFile().getVirtualFile().getPath();
        int i = StringUtilRt.lastIndexOf(path, '.', 0, path.length());
        return i < 0 ? path : path.subSequence(0, i);
    }

    protected CharSequence getPathWithJavaExtension(PsiElement jellyFile) {
        return jellyFile.getContainingFile().getVirtualFile().getParent().getPath()
               + JavaFileType.DOT_DEFAULT_EXTENSION;
    }

    @Override
    public @Nullable Object getCurrentEditorElement(Editor editor, PsiElement parent) {
        return null;
    }
}
