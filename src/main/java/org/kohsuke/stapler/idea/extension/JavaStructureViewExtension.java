package org.kohsuke.stapler.idea.extension;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPackage;
import org.kohsuke.stapler.idea.psi.JellyFile;

import java.util.ArrayList;
import java.util.List;

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
        final List<LeafPsiStructureViewTreeElement> files = new ArrayList<>();
        String qualifiedClassName = ((PsiClass) parent).getQualifiedName();
        if (qualifiedClassName != null) {
            PsiPackage psiPackage = JavaPsiFacade.getInstance(parent.getProject())
                                                 .findPackage(qualifiedClassName);
            if (psiPackage != null) {
                PsiFile[] maybeViewFiles = psiPackage.getFiles(getCurrentScope(parent));
                for (PsiFile file : maybeViewFiles) {
                    if (file instanceof JellyFile) {
                        files.add(new LeafPsiStructureViewTreeElement(file));
                    }
                }
            }
        }
        return files.toArray(StructureViewTreeElement.EMPTY_ARRAY);
    }
}
