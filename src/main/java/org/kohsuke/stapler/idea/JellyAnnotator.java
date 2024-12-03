package org.kohsuke.stapler.idea;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlTag;
import com.intellij.xml.XmlElementDescriptor;
import com.intellij.xml.XmlNSDescriptor;
import com.intellij.xml.impl.schema.AnyXmlElementDescriptor;
import org.jetbrains.annotations.NotNull;

/**
 * Additional Jelly-specific {@link Annotator}.
 *
 * <p>Schemas generated from Jelly tag libraries use &lt;xs:any />, so any nested element is deemed legal by default
 * annotation.
 *
 * <p>This {@link Annotator} ensures that such elements are marked as errors.
 *
 * @author Kohsuke Kawaguchi
 */
public class JellyAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement psi, @NotNull AnnotationHolder holder) {
        if (psi instanceof XmlTag tag) {
            // for elements
            XmlNSDescriptor ns = tag.getDescriptor().getNSDescriptor();
            XmlElementDescriptor e = ns.getElementDescriptor(tag);
            if (e instanceof AnyXmlElementDescriptor) {
                PsiElement startTagName = tag.getFirstChild().getNextSibling();
                holder.newAnnotation(HighlightSeverity.ERROR, "Undefined jelly element")
                        .range(startTagName)
                        .create();
            }
        }
    }
}
