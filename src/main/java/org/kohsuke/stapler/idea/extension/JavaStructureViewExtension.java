package org.kohsuke.stapler.idea.extension;

import java.util.ArrayList;
import java.util.List;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.xml.XmlFileTreeElement;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.xml.XmlFile;
import org.kohsuke.stapler.idea.psi.JellyFile;

/**
 * Additional jelly files in java structure view
 * Items are retrieved in related src/main/java directory
 *
 * @author Julien Greffe
 */
public class JavaStructureViewExtension extends AbstractStructureViewExtension {

    @Override
    public Class<? extends PsiElement> getType() {
        return PsiJavaFile.class;
    }

    @Override
    public StructureViewTreeElement[] getChildren(PsiElement parent) {
        final PsiManager psiManager = PsiManager.getInstance(parent.getProject());
        final List<XmlFileTreeElement> files = new ArrayList<>();
        final String expectedPath = getExpectedJellyParentPath(getPathWithoutExtension(parent).toString());

        ProjectFileIndex.getInstance(parent.getProject())
                        .iterateContent(virtualFile -> {
                                            // check against the expected path
                                            if (expectedPath.equals(virtualFile.getParent().getPath())) {
                                                PsiFile file = psiManager.findFile(virtualFile);
                                                if (file instanceof JellyFile) {
                                                    files.add(new XmlFileTreeElement((XmlFile) file));
                                                }
                                            }
                                            return true;
                                        }
                        );

        return files.toArray(XmlFileTreeElement.EMPTY_ARRAY);
    }
}
