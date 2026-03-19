package io.jenkins.stapler.idea.jelly;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

public class JellyJavaReferenceProvider extends PsiReferenceProvider {

    @Override
    public PsiReference @NotNull [] getReferencesByElement(
            @NotNull PsiElement element, @NotNull ProcessingContext context) {

        if (!(element instanceof XmlAttributeValue attributeValue)) {
            return PsiReference.EMPTY_ARRAY;
        }
        if (attributeValue.getParent() instanceof final XmlAttribute attribute) {
            if (attribute.getParent().getLocalName().equals("entry")
                    && attribute.getLocalName().equals("field")) {
                return new PsiReference[] {new JellyToJavaClassReference(attributeValue)};
            }
        }

        return PsiReference.EMPTY_ARRAY;
    }
}
