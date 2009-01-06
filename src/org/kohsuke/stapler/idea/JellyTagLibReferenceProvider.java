package org.kohsuke.stapler.idea;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.kohsuke.stapler.idea.psi.ref.TagReference;

/**
 * Let IDEA know that the use of Jelly tags are referencing their definitions.
 *
 * <p>
 * This data drives Ctrl+Click. 
 *
 * @author Kohsuke Kawaguchi
 */
public class JellyTagLibReferenceProvider extends PsiReferenceProvider {
    /*
        The basic idea of ReferenceProvider is to create a reference speculatively,
        then the reference object will later try to find the target.
     */
    @NotNull
    public PsiReference[] getReferencesByElement(@NotNull PsiElement e, @NotNull ProcessingContext processingContext) {
        if (e instanceof XmlTag) {
            XmlTag t = (XmlTag)e;
            if(TagReference.isApplicable(t))
                return array(new TagReference(t));
        }

        // this doesn't work, because XmlAttributeImpl doesn't call reference providers.
        // instead, XmlAttribute can only reference XmlAttributeDescriptor.getDeclaration()
//        if (e instanceof XmlAttribute) {
//            XmlAttribute a = (XmlAttribute) e;
//            PsiReference tagRef = a.getParent().getReference();
//            if(tagRef instanceof TagReference) {
//                return array(new TagAttributeReference((TagReference)tagRef,a));
//            }
//        }

        return PsiReference.EMPTY_ARRAY;
    }

    private PsiReference[] array(PsiReference ref) {
        return new PsiReference[] {ref};
    }
}
