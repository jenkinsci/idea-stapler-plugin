package io.jenkins.stapler.idea.jelly;

import static io.jenkins.stapler.idea.jelly.symbols.SymbolFinder.getAvailableSymbols;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.XmlElementVisitor;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import io.jenkins.stapler.idea.jelly.symbols.Symbol;
import java.util.Set;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

public class InvalidIconSrcInspection extends LocalInspectionTool {

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new XmlElementVisitor() {
            @Override
            public void visitXmlAttribute(@NotNull XmlAttribute attribute) {
                if (validAttributeToScan(attribute)) {
                    Set<String> symbols = getAvailableSymbols(attribute.getProject()).stream()
                            .map(Symbol::name)
                            .collect(Collectors.toSet());
                    if (!symbols.contains(attribute.getValue())) {
                        holder.registerProblem(
                                attribute.getValueElement(),
                                String.format("'%s' is an invalid symbol", attribute.getValue()));
                    }
                }
            }
        };
    }

    private boolean validAttributeToScan(@NotNull XmlAttribute attribute) {
        if ("src".equals(attribute.getName())) {
            PsiElement parent = attribute.getParent();
            if (parent instanceof XmlTag xmlTag) {
                return "icon".equals(xmlTag.getLocalName())
                        && "l".equals(xmlTag.getNamespacePrefix())
                        && attribute.getValue().startsWith("symbol-");
            }
        }
        return false;
    }
}
