package io.jenkins.stapler.idea.jelly;

import static io.jenkins.stapler.idea.jelly.symbols.SymbolFinder.getAvailableSymbols;

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
import org.jetbrains.annotations.NotNull;

public class IconSrcCompletionContributor extends CompletionContributor {

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
                            getAvailableSymbols(project)
                                    .forEach(file -> result.addElement(LookupElementBuilder.create(file.name())
                                            .withPresentableText(file.displayText())
                                            .withTypeText(file.group())));
                        }
                    }
                });
    }

    private boolean isInsideLIconSrcAttribute(PsiElement element) {
        if (element instanceof XmlAttributeImpl attribute) {
            if ("src".equals(attribute.getName())) {
                PsiElement parent = attribute.getParent();
                if (parent instanceof XmlTag xmlTag) {
                    return "icon".equals(xmlTag.getLocalName()) && "/lib/layout".equals(xmlTag.getNamespace());
                }
            }
        }
        return false;
    }
}
