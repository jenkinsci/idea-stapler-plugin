package org.kohsuke.stapler.idea;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.PsiReferenceProvider;
import com.intellij.psi.impl.source.resolve.reference.ReferenceType;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;
import org.kohsuke.stapler.idea.psi.ref.TagReference;

/**
 * @author Kohsuke Kawaguchi
 */
public class JellyTagLibReferenceProvider implements PsiReferenceProvider {
    /*
        The basic idea of ReferenceProvider is to create a reference speculatively,
        then the reference object will later try to find the target.
     */
    @NotNull
    public PsiReference[] getReferencesByElement(PsiElement e) {
        if (e instanceof XmlTag) {
            XmlTag t = (XmlTag)e;
            if(TagReference.isApplicable(t))
                return new PsiReference[] {new TagReference(t)};
        }
        return PsiReference.EMPTY_ARRAY;
    }

    @NotNull
    public PsiReference[] getReferencesByElement(PsiElement psielement, ReferenceType referencetype) {
        // what is this?
        throw new UnsupportedOperationException();
    }

    @NotNull
    public PsiReference[] getReferencesByString(String s, PsiElement psielement, ReferenceType referencetype, int i) {
        // what is this?
        throw new UnsupportedOperationException();
    }

    public void handleEmptyContext(PsiScopeProcessor psiscopeprocessor, PsiElement psielement) {
        // I have no idea what this method is supposed to do
    }
}
