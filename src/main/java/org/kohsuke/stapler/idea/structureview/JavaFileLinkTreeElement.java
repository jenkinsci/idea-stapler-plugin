package org.kohsuke.stapler.idea.structureview;

import java.util.Collection;
import java.util.Collections;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.java.JavaFileTreeElement;
import com.intellij.psi.PsiClassOwner;
import org.jetbrains.annotations.NotNull;

/**
 * Simple tree element to display file without children
 *
 * @author Julien Greffe
 */
public class JavaFileLinkTreeElement extends JavaFileTreeElement {

    public JavaFileLinkTreeElement(PsiClassOwner file) {
        super(file);
    }

    @Override
    public @NotNull Collection<StructureViewTreeElement> getChildrenBase() {
        return Collections.emptyList();
    }
}
