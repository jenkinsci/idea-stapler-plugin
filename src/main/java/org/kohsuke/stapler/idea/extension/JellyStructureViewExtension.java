package org.kohsuke.stapler.idea.extension;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import org.kohsuke.stapler.idea.psi.JellyFile;
import org.kohsuke.stapler.idea.structureview.LinkPsiClassTreeElement;

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
        final List<LinkPsiClassTreeElement> files = new ArrayList<>();
        final Path expectedPath = getExpectedJavaPath(getJavaFilePath(parent).toString());

        Optional<PsiClass> psiClass = findDeeperMatchingJavaFile(psiManager, expectedPath, new ArrayList<>());
        psiClass.ifPresent(aClass -> files.add(new LinkPsiClassTreeElement(aClass)));

        return files.toArray(LinkPsiClassTreeElement.EMPTY_ARRAY);
    }

    protected Optional<PsiClass> findDeeperMatchingJavaFile(PsiManager psiManager, Path pathWithoutJavaExtension,
                                                            List<String> innerClasses) {
        VirtualFile virtualFile = VfsUtil.findFile(
            new File(pathWithoutJavaExtension + JavaFileType.DOT_DEFAULT_EXTENSION).toPath(), false);

        // virtual file exists
        if (virtualFile != null) {
            // look into inner classes to find proper element
            PsiFile file = psiManager.findFile(virtualFile);
            return findInnerClass(PsiTreeUtil.getChildOfType(file, PsiClass.class), innerClasses);
        }

        // look in parent path if starting with an upper-case
        if (pathWithoutJavaExtension.getParent() != null && currentFilenameStartsWithUppercase(
            pathWithoutJavaExtension.getParent().getFileName().toString())) {
            innerClasses.add(pathWithoutJavaExtension.getFileName().toString());
            return findDeeperMatchingJavaFile(psiManager, pathWithoutJavaExtension.getParent(), innerClasses);
        }

        return Optional.empty();
    }

    private boolean currentFilenameStartsWithUppercase(String fileName) {
        return fileName.toUpperCase().charAt(0) == fileName.charAt(0);
    }

    private Optional<PsiClass> findInnerClass(PsiClass element, List<String> innerClasses) {
        if (element == null) {
            return Optional.empty();
        }
        if (innerClasses.size() > 0) {
            for (PsiClass psiClass : PsiTreeUtil.getChildrenOfAnyType(element, PsiClass.class)) {
                if (innerClasses.get(0).equals(psiClass.getName())) {
                    // look deper
                    return findInnerClass(psiClass, innerClasses.subList(1, innerClasses.size()));
                }
            }
        }

        return Optional.of(element);
    }
}
