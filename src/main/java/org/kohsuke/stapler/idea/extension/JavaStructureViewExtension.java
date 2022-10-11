package org.kohsuke.stapler.idea.extension;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.kohsuke.stapler.idea.psi.JellyFile;
import org.kohsuke.stapler.idea.psi.link.LinkJellyFileImpl;
import org.kohsuke.stapler.idea.structureview.LinkJellyFileTreeElement;

/**
 * Additional jelly files in java structure view
 * Items are retrieved in related src/main/java directory
 *
 * @author Julien Greffe
 */
public class JavaStructureViewExtension extends AbstractStructureViewExtension {

    @Override
    public Class<? extends PsiElement> getType() {
        return PsiClass.class;
    }

    @Override
    public StructureViewTreeElement[] getChildren(PsiElement parent) {
        final PsiManager psiManager = PsiManager.getInstance(parent.getProject());
        final List<LinkJellyFileTreeElement> files = new ArrayList<>();
        final Path expectedPath = getExpectedJellyParentPath(getJellyDirectoryPath(parent).toString());

        VirtualFile containingJellyDirectory = VfsUtil.findFile(expectedPath, false);
        if (containingJellyDirectory != null) {
            for (VirtualFile jellyFile : containingJellyDirectory.getChildren()) {
                PsiFile file = psiManager.findFile(jellyFile);
                if (file instanceof JellyFile) {
                    files.add(new LinkJellyFileTreeElement(
                        new LinkJellyFileImpl(file.getViewProvider(), file.getFileElementType())));
                }
            }
        }
        return files.toArray(LinkJellyFileTreeElement.EMPTY_ARRAY);
    }
}
