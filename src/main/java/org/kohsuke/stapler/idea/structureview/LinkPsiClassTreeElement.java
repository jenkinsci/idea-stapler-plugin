package org.kohsuke.stapler.idea.structureview;

import java.util.Collection;
import java.util.Collections;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.java.JavaClassTreeElement;
import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.NotNull;

/**
 * Simple tree element to display file without children
 *
 * @author Julien Greffe
 */
public class LinkPsiClassTreeElement extends JavaClassTreeElement {

    public LinkPsiClassTreeElement(PsiClass psiClass) {
        super(psiClass, false);
    }

    @Override
    public @NotNull Collection<StructureViewTreeElement> getChildrenBase() {
        return Collections.emptyList();
    }
}
