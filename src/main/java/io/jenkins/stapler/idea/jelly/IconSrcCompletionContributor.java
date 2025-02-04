package io.jenkins.stapler.idea.jelly;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.xml.XmlAttributeImpl;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ProcessingContext;
import io.jenkins.stapler.idea.jelly.symbols.SymbolFinder;
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
                            SymbolFinder.getInstance(project)
                                    .getAvailableSymbols()
                                    .forEach(symbol -> result.addElement(LookupElementBuilder.create(symbol.name())
                                            .withPresentableText(symbol.displayText())
                                            .withTypeText(symbol.group())
                                            .withInsertHandler((insertionContext, item) -> {
                                                XmlAttribute attribute = PsiTreeUtil.getParentOfType(
                                                        insertionContext
                                                                .getFile()
                                                                .findElementAt(insertionContext.getStartOffset()),
                                                        XmlAttribute.class);
                                                if (attribute != null) {
                                                    WriteCommandAction.runWriteCommandAction(
                                                            project, () -> attribute.setValue(symbol.name()));
                                                }
                                            })));
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
            } else return "icon".equals(attribute.getName());
        }
        return false;
    }
}
