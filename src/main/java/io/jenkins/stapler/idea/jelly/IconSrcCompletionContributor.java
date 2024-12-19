package io.jenkins.stapler.idea.jelly;

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
import io.jenkins.stapler.idea.jelly.symbols.IoniconsApiSymbolFinder;
import io.jenkins.stapler.idea.jelly.symbols.JenkinsSymbolFinder;
import io.jenkins.stapler.idea.jelly.symbols.LocalSymbolFinder;
import io.jenkins.stapler.idea.jelly.symbols.Symbol;
import io.jenkins.stapler.idea.jelly.symbols.SymbolFinder;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

public class IconSrcCompletionContributor extends CompletionContributor {

    private static final ConcurrentMap<Project, Set<Symbol>> ICONS_CACHE = new ConcurrentHashMap<>();

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
                        PsiElement parent = position.getParent().getParent();

                        if (isInsideLIconSrcAttribute(parent)) {
                            Project project = position.getProject();
                            Set<Symbol> icons = ICONS_CACHE.computeIfAbsent(project, e -> computeSymbols(project));
                            icons.forEach(file -> result.addElement(LookupElementBuilder.create(file.name())
                                    .withPresentableText(file.displayText())
                                    .withTypeText(file.group())));
                        }
                    }
                });
    }

    private Set<Symbol> computeSymbols(Project project) {
        return SYMBOL_FINDERS.stream()
                .flatMap(finder -> finder.getSymbols(project).stream())
                .collect(Collectors.toSet());
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
