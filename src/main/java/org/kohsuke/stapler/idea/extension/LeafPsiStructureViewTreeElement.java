package org.kohsuke.stapler.idea.extension;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.Iconable;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Generic Structure View tree element which renders a named PSI element without any children.
 */
public class LeafPsiStructureViewTreeElement implements StructureViewTreeElement, ItemPresentation {
    private final SmartPsiElementPointer<PsiNamedElement> value;

    public LeafPsiStructureViewTreeElement(PsiNamedElement value) {
        this.value = SmartPointerManager.createPointer(value);
    }

    @Override
    public Object getValue() {
        return value.getElement();
    }

    @Override
    public @NotNull ItemPresentation getPresentation() {
        return this;
    }

    @Override
    public TreeElement @NotNull [] getChildren() {
        return TreeElement.EMPTY_ARRAY;
    }

    @Override
    public void navigate(boolean requestFocus) {
        PsiElement element = value.getElement();
        if (element instanceof Navigatable) {
            ((Navigatable) element).navigate(requestFocus);
        }
    }

    @Override
    public boolean canNavigate() {
        PsiElement element = value.getElement();
        return element instanceof Navigatable && ((Navigatable) element).canNavigate();
    }

    @Override
    public boolean canNavigateToSource() {
        PsiElement element = value.getElement();
        return element instanceof Navigatable && ((Navigatable) element).canNavigateToSource();
    }

    @Override
    public @Nullable String getPresentableText() {
        PsiNamedElement element = value.getElement();
        return element != null ? element.getName() : null;
    }

    @Override
    public @Nullable String getLocationString() {
        return null;
    }

    @Override
    public @Nullable Icon getIcon(boolean unused) {
        PsiElement view = value.getElement();
        return view != null ? view.getIcon(Iconable.ICON_FLAG_READ_STATUS) : null;
    }
}
