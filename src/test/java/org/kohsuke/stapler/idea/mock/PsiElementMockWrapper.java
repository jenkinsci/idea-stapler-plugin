package org.kohsuke.stapler.idea.mock;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Julien Greffe
 */
public class PsiElementMockWrapper<T extends PsiElement> {
    private Class<T> type;

    private final Path path;

    private final T psiElement;

    private VirtualFile virtualFile;

    List<PsiElementMockWrapper<? extends PsiElement>> children = new ArrayList<>();

    public PsiElementMockWrapper(Class<T> type) {
        this(type, Paths.get("unknown"), false);
    }

    public PsiElementMockWrapper(Class<T> type, Path path) {
        this(type, path, false);
    }

    public PsiElementMockWrapper(Class<T> type, Path path, boolean withVirtualFile) {
        this.type = type;
        this.path = path;
        psiElement = mock(type);
        if (withVirtualFile) {
            virtualFile = mock(VirtualFile.class);
        }
    }

    public void addChild(PsiElement child) {
        children.add(new PsiElementMockWrapper<>(child.getClass()));
    }

    public void addChild(PsiElementMockWrapper<? extends PsiElement> child) {
        children.add(child);
    }

    public Class<T> getType() {
        return type;
    }

    public Path getPath() {
        return path;
    }

    public T getPsiElement() {
        return psiElement;
    }

    public List<PsiElementMockWrapper<? extends PsiElement>> getChildren() {
        return children;
    }

    public VirtualFile getVirtualFile() {
        return virtualFile;
    }

    public void stub() {
        if (type == PsiClass.class) {
            when(((PsiClass) psiElement).getName()).thenReturn(path.getFileName().toString());
        }
        when(psiElement.toString()).thenReturn("Mock" + type.getSimpleName() + ":" + path.getFileName());
        if (!children.isEmpty()) {
            when(psiElement.getFirstChild()).thenReturn(children.get(0).getPsiElement());
            for (int i = 0; i < children.size(); i++) {
                PsiElementMockWrapper<? extends PsiElement> wrapper = children.get(i);
                PsiElement current = wrapper.getPsiElement();
                PsiElement next = (i < children.size() - 1) ? children.get(i + 1).getPsiElement() : null;
                when(current.getNextSibling()).thenReturn(next);
                wrapper.stub();
            }
        }
    }

}
