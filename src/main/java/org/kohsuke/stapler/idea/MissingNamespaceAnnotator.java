package org.kohsuke.stapler.idea;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class MissingNamespaceAnnotator implements Annotator {

    public static final Map<String, String> EXPECTED_NAMESPACES = Map.of("j", "jelly:core",
        "f", "/lib/form", "l", "/lib/layout");

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof XmlTag tag)) return;

        // Check if the prefix is one of the expected ones
        String prefix = tag.getNamespacePrefix();

        if (EXPECTED_NAMESPACES.containsKey(prefix)) {
            String expectedNamespace = EXPECTED_NAMESPACES.get(prefix);

            // Check if the namespace is missing or incorrect
            if (!expectedNamespace.equals(tag.getNamespace())) {
                holder.newAnnotation(HighlightSeverity.ERROR,
                        String.format("Missing or incorrect namespace for prefix '%s'. Expected: '%s'", prefix, expectedNamespace))
                    .withFix(new AddNamespaceQuickFix(prefix, expectedNamespace))
                    .create();
            }
        }
    }
}
