package org.kohsuke.stapler.idea.extension;

import java.util.ArrayList;
import java.util.List;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import org.kohsuke.stapler.idea.psi.JellyFile;
import org.kohsuke.stapler.idea.structureview.JavaFileLinkTreeElement;

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
        final PsiManager psiManager = PsiManager.getInstance(parent.getProject());
        final List<JavaFileLinkTreeElement> files = new ArrayList<>();
        final String expectedPath = getExpectedJavaPath(getPathWithJavaExtension(parent).toString());

        ProjectFileIndex.getInstance(parent.getProject())
                        .iterateContent(virtualFile -> {
                                            // check against the expected path
                                            if (expectedPath.equals(virtualFile.getPath())) {
                                                PsiFile file = psiManager.findFile(virtualFile);
                                                if (file instanceof PsiJavaFile) {
                                                    files.add(new JavaFileLinkTreeElement((PsiJavaFile) file));
                                                }
                                            }
                                            return true;
                                        }
                        );

        return files.toArray(JavaFileLinkTreeElement.EMPTY_ARRAY);
    }
}
