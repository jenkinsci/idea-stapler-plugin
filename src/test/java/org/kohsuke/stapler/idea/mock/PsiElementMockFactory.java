package org.kohsuke.stapler.idea.mock;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

import static java.lang.Boolean.TRUE;

/**
 * Mock for PsiClass
 * Can be standard class or inner class
 *
 * @author Julien Greffe
 */
public class PsiElementMockFactory {

    /**
     * List of generated mocks from one PsiClassMock declaration
     */
    private List<PsiElementMockWrapper<? extends PsiElement>> mocks = new ArrayList<>();

    /**
     * List of empty paths for VfsUtil
     */
    private Set<Path> nullVirtualFiles = new HashSet<>();

    private PsiElementMockWrapper<? extends PsiElement> psiElementRoot;

    /**
     * Create a new PsiClass mock
     *
     * @param path path to the class. Having some first-char uppercase indicates it's a class
     *             Inner classes can then be described like my/package/Class/With/Inner/One
     */
    public PsiElementMockFactory(String path) {
        recursiveMock(Paths.get(path), null);
        if (psiElementRoot != null) {
            psiElementRoot.stub();
        }
    }

    public PsiElementMockWrapper<? extends PsiElement> getPsiElementRoot() {
        return psiElementRoot;
    }

    public Set<Path> getNullVirtualFiles() {
        return nullVirtualFiles;
    }

    /**
     * Return mocks
     */
    public List<PsiElementMockWrapper<? extends PsiElement>> getMocks() {
        return mocks;
    }

    private PsiElementMockWrapper<? extends PsiElement> recursiveMock(Path path, Boolean inner) {
        if (isClass(path.getFileName())) {
            Path javaPath = Paths.get(path.toString() + ".java");

            // current path has a parent which is a class
            if (path.getParent() != null && isClass(path.getParent().getFileName())) {

                PsiElementMockWrapper<PsiClass> psiClass = new PsiElementMockWrapper<>(PsiClass.class, path);
                PsiElementMockWrapper<? extends PsiElement> parent = recursiveMock(path.getParent(), TRUE);

                // we need to set current PsiClass to the parent
                if (parent != null) {
                    parent.addChild(psiClass);
                }

                mocks.add(psiClass);
                return psiClass;
            } else {
                // actual class
                PsiElementMockWrapper<PsiFile> psiFile = new PsiElementMockWrapper<>(PsiFile.class, javaPath, true);
                psiElementRoot = psiFile;
                mocks.add(psiFile);

                PsiElementMockWrapper<PsiClass> psiClass = new PsiElementMockWrapper<>(PsiClass.class, path);
                mocks.add(psiClass);
                psiFile.addChild(psiClass);

                return psiClass;
            }
        }
        // not a class
        //        nullVirtualFiles.add(new File(path.toString() + ".java").toPath());
        return null;
    }

    /**
     * Check if fileName starts with an uppercase
     */
    private boolean isClass(Path fileName) {
        String first = Character.toString(fileName.getFileName().toString().charAt(0));
        return first.toUpperCase().equals(first);
    }
}
