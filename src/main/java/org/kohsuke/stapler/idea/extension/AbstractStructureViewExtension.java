package org.kohsuke.stapler.idea.extension;

import static com.intellij.openapi.roots.TestSourcesFilter.isTestSources;
import static com.intellij.psi.search.GlobalSearchScopesCore.projectProductionScope;
import static com.intellij.psi.search.GlobalSearchScopesCore.projectTestScope;

import com.intellij.ide.structureView.StructureViewExtension;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract class for jelly/java structure views
 *
 * @author Julien Greffe
 */
public abstract class AbstractStructureViewExtension implements StructureViewExtension {

    /** Return current scope: test or production */
    protected GlobalSearchScope getCurrentScope(PsiElement element) {
        if (element.getContainingFile() != null
                && element.getContainingFile().getVirtualFile() != null
                && isTestSources(element.getContainingFile().getVirtualFile(), element.getProject())) {
            return projectTestScope(element.getProject());
        }
        return projectProductionScope(element.getProject());
    }

    @Override
    public @Nullable Object getCurrentEditorElement(Editor editor, PsiElement parent) {
        return null;
    }
}
