package org.kohsuke.stapler.idea.extension;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.intellij.ide.structureView.StructureViewExtension;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
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
    protected Path getExpectedJellyParentPath(String path) {
        return Paths.get(path.replace(SRC_JAVA, SRC_RESOURCES));
    }

    /**
     * Replace src/main/resources to src/main/java (location of expected related java files)
     */
    protected Path getExpectedJavaPath(String path) {
        return Paths.get(path.replace(SRC_RESOURCES, SRC_JAVA));
    }

    /**
     * Construct potential jelly directory path linked to the current java element
     */
    protected CharSequence getJellyDirectoryPath(PsiElement javaElement) {
        CharSequence containingPath = javaElement.getContainingFile().getParent().getVirtualFile().getPath();

        StringBuilder sb = new StringBuilder();
        constructRecursivePotentialJellyPath(sb, javaElement);

        return containingPath + sb.toString();
    }

    /**
     * Recursively look for parent classes and add name to path
     */
    private void constructRecursivePotentialJellyPath(StringBuilder sb, PsiElement javaElement) {
        if (javaElement instanceof PsiClass) {
            constructRecursivePotentialJellyPath(sb, javaElement.getParent());
            sb.append("/");
            sb.append(((PsiClass) javaElement).getName());
        }
    }

    /**
     * Return a path of expected Java file
     */
    protected String getJavaFilePath(PsiElement jellyElement) {
        return jellyElement.getContainingFile().getVirtualFile().getParent().getPath();
    }

    @Override
    public @Nullable Object getCurrentEditorElement(Editor editor, PsiElement parent) {
        return null;
    }
}
