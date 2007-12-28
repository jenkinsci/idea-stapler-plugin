package org.kohsuke.stapler.idea;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.impl.source.resolve.reference.PsiReferenceProvider;
import com.intellij.psi.impl.source.resolve.reference.ReferenceType;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlToken;
import org.jetbrains.annotations.NotNull;

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


            // test
            return new PsiReference[] {
                new PsiReferenceBase<XmlTag>(t, new TextRange(2,3)) {
                    public PsiElement resolve() {
                        String localName = myElement.getLocalName();

                        Module m = ModuleUtil.findModuleForPsiElement(myElement);
                        if(m!=null) {// just trying to be defensive
                            PsiManager psiManager = PsiManager.getInstance(myElement.getProject());
                            VirtualFile module = m.getModuleFile().getParent();
                            VirtualFile child = module.findChild(localName + ".txt");
                            if(child!=null)
                                return psiManager.findFile(child);
                        }

                        return null;
                    }

                    public Object[] getVariants() {
                        // not sure how to use this
                        return new String[]{"abc","def","ghi"};
                    }
                }
            };
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
