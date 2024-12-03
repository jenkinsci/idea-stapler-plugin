package org.kohsuke.stapler.idea;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.xml.XmlAttributeImpl;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ProcessingContext;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.kohsuke.stapler.idea.symbols.IoniconsApiSymbolFinder;
import org.kohsuke.stapler.idea.symbols.JenkinsSymbolFinder;
import org.kohsuke.stapler.idea.symbols.LocalSymbolFinder;
import org.kohsuke.stapler.idea.symbols.Symbol;
import org.kohsuke.stapler.idea.symbols.SymbolFinder;

public class IconSrcCompletionContributor extends CompletionContributor {

    private static final List<SymbolFinder> SYMBOL_FINDERS =
            List.of(new LocalSymbolFinder(), new JenkinsSymbolFinder(), new IoniconsApiSymbolFinder());

    public IconSrcCompletionContributor() {
        extend(
                CompletionType.BASIC,
                PlatformPatterns.psiElement().inside(XmlAttributeImpl.class),
                new CompletionProvider<>() {
                    @Override
                    protected void addCompletions(
                            @NotNull CompletionParameters parameters,
                            @NotNull ProcessingContext context,
                            @NotNull CompletionResultSet result) {
                        PsiElement position = parameters.getPosition();
                        Project project = position.getProject();

                        Set<Symbol> icons = SYMBOL_FINDERS.stream()
                                .flatMap(e -> e.getSymbols(project).stream())
                                .collect(Collectors.toSet());

                        PsiElement parent = position.getParent().getParent();
                        if (isInsideLIconSrcAttribute(parent)) {
                            icons.forEach(file -> result.addElement(LookupElementBuilder.create(file.getName())
                                    .withIcon(file.getIcon())
                                    .withPresentableText(file.getDisplayText())
                                    .withTypeText(file.getGroup())));
                        }
                    }
                });
    }

    private boolean isInsideLIconSrcAttribute(PsiElement element) {
        if (element instanceof XmlAttributeImpl attribute) {
            if ("src".equals(attribute.getName())) {
                PsiElement parent = attribute.getParent();
                if (parent instanceof XmlTag xmlTag) {
                    return "icon".equals(xmlTag.getLocalName()) && "l".equals(xmlTag.getNamespacePrefix());
                }
            }
        }
        return false;
    }
}
