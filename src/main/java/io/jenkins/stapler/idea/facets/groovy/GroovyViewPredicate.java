package io.jenkins.stapler.idea.facets.groovy;

import com.intellij.psi.PsiFile;
import org.jetbrains.plugins.groovy.GroovyFileType;
import org.kohsuke.stapler.idea.extension.JavaStructureViewExtension;

public class GroovyViewPredicate implements JavaStructureViewExtension.StaplerViewPredicate {
    @Override
    public boolean test(PsiFile psiFile) {
        return psiFile.getFileType() == GroovyFileType.GROOVY_FILE_TYPE;
    }
}
