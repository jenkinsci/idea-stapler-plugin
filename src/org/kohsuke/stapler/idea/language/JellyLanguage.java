package org.kohsuke.stapler.idea.language;

import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.ide.structureView.TreeBasedStructureViewBuilder;
import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.impl.xml.XmlStructureViewTreeModel;
import com.intellij.lang.Commenter;
import com.intellij.lang.Language;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.StdLanguages;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.refactoring.NamesValidator;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlElement;
import org.jetbrains.annotations.NotNull;
import org.kohsuke.stapler.idea.psi.JellyFile;

/**
 * @author Kohsuke Kawaguchi
 */
public final class JellyLanguage extends Language {
    public static final JellyLanguage INSTANCE = new JellyLanguage();

    private final NamesValidator myNamesValidator = new NamesValidator() {
        public boolean isKeyword(String name, Project project) {
            return false;
        }

        public boolean isIdentifier(String name, Project project) {
            return true;
        }
    };

    private JellyLanguage() {
        super("Jelly");
    }

    public ParserDefinition getParserDefinition() {
        return JellyParserDefinition.INSTANCE;
    }

    public Annotator getAnnotator() {
        return JellyAnnotator.INSTANCE;
    }

    @NotNull
    public NamesValidator getNamesValidator() {
        return myNamesValidator;
    }

    public Commenter getCommenter() {
        return StdLanguages.XML.getCommenter();
    }

    // TODO
//    @NotNull
//    public FindUsagesProvider getFindUsagesProvider() {
//        ...
//    }

    public static JellyFile getJellyFile(PsiFile psiFile) {
        if (psiFile instanceof JellyFile)
            return (JellyFile) psiFile;
        else
            return (JellyFile) psiFile.getViewProvider().getPsi(JellyLanguage.INSTANCE);
    }

    /**
     * Gets {@link JellyFile} that contains the given {@link PsiElement}
     */
    public static JellyFile getJellyFile(PsiElement e) {
        return JellyLanguage.getJellyFile(e.getContainingFile());
    }

    public StructureViewBuilder getStructureViewBuilder(PsiFile psiFile) {
        final JellyFile j = getJellyFile(psiFile);
        if (j == null)
            return null;
        return new TreeBasedStructureViewBuilder() {
            @NotNull
            public StructureViewModel createStructureViewModel() {
                return new XmlStructureViewTreeModel(j.getSourceElement());
            }
        };
    }

    // TODO
//    protected DocumentationProvider createDocumentationProvider() {
//        ...
//    }
}
