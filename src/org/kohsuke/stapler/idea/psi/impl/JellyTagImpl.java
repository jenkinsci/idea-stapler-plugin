package org.kohsuke.stapler.idea.psi.impl;

import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.util.TextRange;
import org.kohsuke.stapler.idea.psi.JellyPsi;
import org.kohsuke.stapler.idea.psi.JellyTag;
import org.kohsuke.stapler.idea.psi.TagDefinition;

/**
 * @author Kohsuke Kawaguchi
 */
final class JellyTagImpl extends JellyPsiImpl implements JellyTag {
    JellyTagImpl(JellyPsi parent, XmlTag sourceElement) {
        super(parent, sourceElement);
    }

    public XmlTag getSourceElement() {
        return (XmlTag)super.getSourceElement();
    }

    public TagDefinition getDefinition() {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public PsiReference getReference() {
        // reference is only for the element name.
        // text range is relative to this element
        TextRange tr = getSourceElement().getFirstChild().getNextSibling().getTextRange();
        tr = tr.shiftRight(-getSourceElement().getTextRange().getStartOffset());

        return new PsiReferenceBase<XmlTag>(getSourceElement(),tr) {
            public PsiElement resolve() {
                String localName = myElement.getLocalName();
                String nsUri = myElement.getNamespace();

                Module m = ModuleUtil.findModuleForPsiElement(myElement);
                if(m!=null) {// just trying to be defensive
                    PsiManager psiManager = PsiManager.getInstance(myElement.getProject());

                    String pkgName = nsUri.substring(1).replace('/', '.');
                    // this invocation below successfully finds packages that includes
                    // invalid characters like 'a-b-c'
                    PsiPackage pkg = psiManager.findPackage(pkgName);
                    PsiDirectory[] dirs = pkg.getDirectories(GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(m, false));

                    for (PsiDirectory dir : dirs) {
                        PsiFile tagFile = dir.findFile(localName + ".jelly");
                        if(tagFile!=null)   return tagFile;
                    }

                    // TODO: this is just a test
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
        };
    }
}
