package org.kohsuke.stapler.idea.extension;

import java.util.ArrayList;
import java.util.List;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.kohsuke.stapler.idea.psi.JellyFile;
import org.kohsuke.stapler.idea.structureview.JellyFileLinkTreeElement;

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
        final List<JellyFileLinkTreeElement> files = new ArrayList<>();
        final String expectedPath = getExpectedJellyParentPath(getPathWithoutExtension(parent).toString());

        ProjectFileIndex.getInstance(parent.getProject())
                        .iterateContent(virtualFile -> {
                                            // check against the expected path
                                            if (expectedPath.equals(virtualFile.getParent().getPath())) {
                                                PsiFile file = psiManager.findFile(virtualFile);
                                                if (file instanceof JellyFile) {
                                                    files.add(new JellyFileLinkTreeElement((JellyFile) file));
                                                }
                                            }
                                            return true;
                                        }
                        );

        return files.toArray(JellyFileLinkTreeElement.EMPTY_ARRAY);
    }
}
