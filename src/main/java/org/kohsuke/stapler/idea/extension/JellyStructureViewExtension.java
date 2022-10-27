package org.kohsuke.stapler.idea.extension;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import org.kohsuke.stapler.idea.psi.JellyFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Additional java files in jelly structure view
 * Items are retrieved in related src/main/java directory
 *
 * @author Julien Greffe
 */
public class JellyStructureViewExtension extends AbstractStructureViewExtension {

    @Override
    public Class<? extends PsiElement> getType() {
        return JellyFile.class;
    }

    @Override
    public StructureViewTreeElement[] getChildren(PsiElement parent) {
        final List<LeafPsiStructureViewTreeElement> files = new ArrayList<>();
        // get the source root virtual file of current element
        VirtualFile sourceRoot = ProjectRootManager.getInstance(parent.getProject())
                                                   .getFileIndex()
                                                   .getSourceRootForFile(parent.getContainingFile().getVirtualFile());
        VirtualFile parentDirectory = parent.getContainingFile().getVirtualFile().getParent();
        if (sourceRoot != null && parentDirectory != null) {
            // construct the qualified classname of expected class from jelly file
            String qualifiedClassName = VfsUtilCore.getRelativePath(parentDirectory, sourceRoot, '.');
            if (qualifiedClassName != null) {
                PsiClass psiClass = JavaPsiFacade.getInstance(parent.getProject())
                                                 .findClass(qualifiedClassName, getCurrentScope(parent));
                if (psiClass != null) {
                    files.add(new LeafPsiStructureViewTreeElement(psiClass));
                }
            }
        }
        return files.toArray(StructureViewTreeElement.EMPTY_ARRAY);
    }
}
