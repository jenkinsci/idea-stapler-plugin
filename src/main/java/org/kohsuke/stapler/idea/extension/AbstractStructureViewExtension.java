package org.kohsuke.stapler.idea.extension;

import com.intellij.ide.structureView.StructureViewExtension;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.roots.TestSourcesFilter;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.GlobalSearchScopesCore;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract class for jelly/java structure views
 *
 * @author Julien Greffe
 */
public abstract class AbstractStructureViewExtension implements StructureViewExtension {

    /**
     * Return current scope: test or production
     */
    protected GlobalSearchScope getCurrentScope(PsiElement element) {
        if (TestSourcesFilter.isTestSources(element.getContainingFile().getVirtualFile(),
                                            element.getProject())) {
            return GlobalSearchScopesCore.projectTestScope(element.getProject());
        }
        return GlobalSearchScopesCore.projectProductionScope(element.getProject());
    }

    @Override
    public @Nullable Object getCurrentEditorElement(Editor editor, PsiElement parent) {
        return null;
    }
}
