package org.kohsuke.stapler.idea.descriptor;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.impl.source.xml.XmlElementDescriptorProvider;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlTag;
import com.intellij.xml.XmlElementDescriptor;

/**
 * Contributes {@link XmlElementDescriptorProvider} for Jelly tags.
 *
 * @author Kohsuke Kawaguchi
 */
public class XmlElementDescriptorProviderImpl implements XmlElementDescriptorProvider {
    public XmlElementDescriptor getDescriptor(XmlTag xmlTag) {
        if(!xmlTag.getContainingFile().getName().endsWith(".jelly"))
            return null;    // not a jelly script

        String nsUri = xmlTag.getNamespace();
        if(nsUri.length()==0)   return null;

        Module m = ModuleUtil.findModuleForPsiElement(xmlTag);
        if(m==null) return null; // just trying to be defensive

        JavaPsiFacade javaPsi = JavaPsiFacade.getInstance(xmlTag.getProject());

        String pkgName = nsUri.substring(1).replace('/', '.');
        // this invocation below successfully finds packages that includes
        // invalid characters like 'a-b-c'
        PsiPackage pkg = javaPsi.findPackage(pkgName);
        if(pkg==null)   return null;

        PsiDirectory[] dirs = pkg.getDirectories(GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(m, false));

        for (PsiDirectory dir : dirs) {
            if(dir.findFile("taglib")!=null) {
                // this is a tag library
                return new XmlNSDescriptorImpl(nsUri,dir).getElementDescriptor(xmlTag);
            }
        }

        return null;
    }
}
