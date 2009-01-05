package org.kohsuke.stapler.idea.descriptor;

import com.intellij.xml.XmlNSDescriptor;
import com.intellij.xml.XmlElementDescriptor;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Kohsuke Kawaguchi
 */
public class XmlNSDescriptorImpl implements XmlNSDescriptor {
    /**
     * Namespace URI of a tag library.
     */
    private String uri;

    /**
     * Directory full of tag files that defines this namespace.
     */
    private PsiDirectory dir;

    public XmlNSDescriptorImpl(String uri, PsiDirectory dir) {
        this.uri = uri;
        this.dir = dir;
    }

    /**
     * @deprecated
     *      Should be only invoked by IDEA.
     *      {@link #init(PsiElement)} call follows immediately.
     */
    public XmlNSDescriptorImpl() {
    }

    public PsiDirectory getDir() {
        return dir;
    }

    public XmlElementDescriptor getElementDescriptor(@NotNull XmlTag tag) {
        PsiFile f = dir.findFile(tag.getLocalName() + ".jelly");
        if (f instanceof XmlFile)
            return new XmlElementDescriptorImpl(this, (XmlFile)f);
        return null;
    }

    /**
     * Returns all the possible root elements.
     *
     * <p>
     * This appears to be used for code completion. When I returned
     * an empty array, the code completion didn't show me anything. 
     */
    @NotNull
    public XmlElementDescriptor[] getRootElementsDescriptors(@Nullable XmlDocument document) {
        List<XmlElementDescriptor> r = new ArrayList<XmlElementDescriptor>();
        for(PsiFile f : dir.getFiles()) {
            if(!f.getName().endsWith(".jelly"))     continue;
            if (f instanceof XmlFile)
                r.add(new XmlElementDescriptorImpl(this, (XmlFile)f));
        }
        return r.toArray(new XmlElementDescriptor[r.size()]);
    }

    public XmlFile getDescriptorFile() {
        return null;
    }

    public boolean isHierarhyEnabled() {
        return true; // ???
    }

    public PsiElement getDeclaration() {
        return dir.findFile("taglib");
    }

    public String getName(PsiElement context) {
        return uri;
    }

    public String getName() {
        return uri;
    }

    /**
     * This method is called when this object is instanciated by IDEA as metadata
     * to existing object.
     * <p>
     * This special pseudo document has to be &lt;schema uri="..." xmlns="dummy-schema-url"/> 
     */
    public void init(PsiElement element) {
        XmlDocument doc = (XmlDocument) element;
        dir = doc.getContainingFile().getUserData(XmlSchemaProviderImpl.MODULE);
        uri = doc.getRootTag().getAttribute("uri","").getValue();
    }

    public Object[] getDependences() {
        return new Object[]{dir};
    }

    public static XmlNSDescriptorImpl get(XmlTag tag) {
        if(!tag.getContainingFile().getName().endsWith(".jelly"))
            return null;    // this tag is not in a jelly script

        String nsUri = tag.getNamespace();
        return get(nsUri, ModuleUtil.findModuleForPsiElement(tag));
    }

    public static XmlNSDescriptorImpl get(String nsUri, Module module) {
        // just trying to be defensive
        if(module==null) return null;
        if(nsUri.length()==0)   return null;

        JavaPsiFacade javaPsi = JavaPsiFacade.getInstance(module.getProject());

        String pkgName = nsUri.substring(1).replace('/', '.');
        // this invocation below successfully finds packages that includes
        // invalid characters like 'a-b-c'
        PsiPackage pkg = javaPsi.findPackage(pkgName);
        if(pkg==null)   return null;

        PsiDirectory[] dirs = pkg.getDirectories(GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module, false));

        for (PsiDirectory dir : dirs)
            if(dir.findFile("taglib")!=null)
                // this is a tag library
                return new XmlNSDescriptorImpl(nsUri,dir);

        return null;
    }
}
