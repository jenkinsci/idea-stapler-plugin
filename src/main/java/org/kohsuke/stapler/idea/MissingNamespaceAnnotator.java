package org.kohsuke.stapler.idea;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class MissingNamespaceAnnotator implements Annotator {

    Map<String, String> mappedNamespaces = Map.of("l", "/lib/layout");

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof XmlTag tag)) return;

        if ("l".equals(tag.getNamespacePrefix()) && !isNamespaceDeclared(tag)) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Missing namespace for prefix 'l'")
                .withFix(new AddNamespaceQuickFix())
                .create();
        }
    }

    private boolean isNamespaceDeclared(XmlTag tag) {
        return "/lib/layout".equals(tag.getNamespace());
    }
}
