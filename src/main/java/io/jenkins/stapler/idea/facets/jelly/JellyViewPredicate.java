package io.jenkins.stapler.idea.facets.jelly;

import com.intellij.psi.PsiFile;
import org.kohsuke.stapler.idea.extension.JavaStructureViewExtension;
import org.kohsuke.stapler.idea.language.JellyFileType;

public class JellyViewPredicate implements JavaStructureViewExtension.StaplerViewPredicate {
    @Override
    public boolean test(PsiFile psiFile) {
        return psiFile.getFileType() == JellyFileType.INSTANCE;
    }
}
