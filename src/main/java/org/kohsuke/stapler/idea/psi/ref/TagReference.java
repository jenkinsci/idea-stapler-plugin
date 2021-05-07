package org.kohsuke.stapler.idea.psi.ref;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;

/**
 * {@link PsiReference} to a definition of a Jelly tag.
 *
 * @author Kohsuke Kawaguchi
 */
public final class TagReference extends PsiReferenceBase<XmlTag> {
    public TagReference(XmlTag ref) {
        super(ref,calcTagNameRange(ref));
    }

    /**
     * Returns true if the given XML tag is a reference to a Jelly tag.
     */
    public static boolean isApplicable(XmlTag ref) {
        return ref.getNamespace().startsWith("/");
    }

    /**
     * Calculate the text range withtin {@link XmlTag} that represents
     * the tag name.
     */
    private static TextRange calcTagNameRange(XmlTag t) {
        // reference is only for the element name.
        // text range is relative to this element
        TextRange tr = t.getFirstChild().getNextSibling().getTextRange();
        return tr.shiftRight(-t.getTextRange().getStartOffset());

    }

    @Override
    public XmlFile resolve() {
        String localName = myElement.getLocalName();
        String nsUri = myElement.getNamespace();
        if(nsUri.length()==0)   return null;

        Module m = ModuleUtil.findModuleForPsiElement(myElement);
        if(m==null) return null; // just trying to be defensive

        JavaPsiFacade javaPsi = JavaPsiFacade.getInstance(myElement.getProject());

        String pkgName = nsUri.substring(1).replace('/', '.');
        // this invocation below successfully finds packages that includes
        // invalid characters like 'a-b-c'
        PsiPackage pkg = javaPsi.findPackage(pkgName);
        if(pkg==null)   return null;

        PsiDirectory[] dirs = pkg.getDirectories(GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(m, false));

        for (PsiDirectory dir : dirs) {
            PsiFile tagFile = dir.findFile(localName + ".jelly");
            if (tagFile instanceof XmlFile)
                return (XmlFile) tagFile;
        }

//        // TODO: this is just a test
//        PsiManager psiManager = PsiManager.getInstance(myElement.getProject());
//        VirtualFile module = m.getModuleFile().getParent();
//        VirtualFile child = module.findChild(localName + ".txt");
//        if(child!=null)
//            return psiManager.findFile(child);

        return null;
    }

    @Override
    public Object[] getVariants() {
//        // not sure how to use this
//        // -> this is used apparently as a quick completion.
//        // try typing "<a" then hit Ctrl+SPACE.
//        return new String[]{"abc","def","ghi"};
        return new Object[0];
    }
}
