package org.kohsuke.stapler.idea.descriptor;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.xml.XmlElementDescriptor;
import com.intellij.xml.XmlNSDescriptorEx;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kohsuke.stapler.idea.psi.JellyFile;

/** @author Kohsuke Kawaguchi */
public class StaplerCustomJellyTagLibraryXmlNSDescriptor implements XmlNSDescriptorEx {
    /**
     * Extension introduced by Stapler Framework to differentiate Jelly views from Tag Libraries that are made up of
     * script files. For example: <a
     * href="https://github.com/jenkinsci/stapler/blob/1709.ve4c10835694b_/jelly/src/main/java/org/kohsuke/stapler/jelly/CustomTagLibrary.java#L128">CustomTagLibrary.java#L128</a>
     * <a
     * href="https://github.com/jenkinsci/stapler/blob/1709.ve4c10835694b_/jelly/src/main/java/org/kohsuke/stapler/jelly/ThisTagLibrary.java#L77">ThisTagLibrary.java#L77</a>
     */
    private static final String STAPLER_JELLY_TAG_EXTENSION = "jellytag";
    /** Default extension of the Apache Commons Jelly */
    private static final String JELLY_EXTENSION = "jelly";
    /*
     * Stapler's `CustomTagLibrary`[1] introduces its own tag extension in addition to standard Jelly Extension.
     * It is also extensible using `JellyTagFileLoader`-s, the only instance of which is `GroovyTagFileLoader`[2]
     *
     * 1: https://github.com/jenkinsci/stapler/blob/1709.ve4c10835694b_/jelly/src/main/java/org/kohsuke/stapler/jelly/CustomTagLibrary.java#L128-L130
     * 2: https://github.com/jenkinsci/stapler/blob/2a13b906bf3af42bc610e4592d56eb8b511fa1be/groovy/src/main/java/org/kohsuke/stapler/jelly/groovy/GroovyTagFileLoader.java
     */
    private static final List<String> DOT_TAGFILE_EXTENSIONS =
            List.of("." + STAPLER_JELLY_TAG_EXTENSION, "." + JELLY_EXTENSION, ".groovy");
    /** Namespace URI of a tag library. */
    private String uri;

    /** Directory full of tag files that defines this namespace. */
    private PsiDirectory dir;

    public StaplerCustomJellyTagLibraryXmlNSDescriptor(String uri, PsiDirectory dir) {
        this.uri = uri;
        this.dir = dir;
    }

    /** @deprecated Should be only invoked by IDEA. {@link #init(PsiElement)} call follows immediately. */
    public StaplerCustomJellyTagLibraryXmlNSDescriptor() {}

    public PsiDirectory getDir() {
        return dir;
    }

    @Override
    public XmlElementDescriptor getElementDescriptor(@NotNull XmlTag tag) {
        return getElementDescriptor(tag.getLocalName());
    }

    @Override
    public XmlElementDescriptor getElementDescriptor(String localName, String namespace) {
        return getElementDescriptor(localName);
    }

    private XmlElementDescriptor getElementDescriptor(String localName) {
        for (String ext : DOT_TAGFILE_EXTENSIONS) {
            PsiFile f = dir.findFile(localName + ext);
            if (f instanceof XmlFile) {
                return new StaplerCustomJellyTagfileXmlElementDescriptor(this, (XmlFile) f);
            }
            // TODO: Handle groovy file tags
        }
        return null;
    }

    /** Returns an empty array to disable IntelliJ's autocomplete */
    @Override
    @NotNull
    public XmlElementDescriptor @NotNull [] getRootElementsDescriptors(@Nullable XmlDocument document) {
        return new XmlElementDescriptor[0];
    }

    /**
     * Returns all possible tags for the library.
     *
     * <p>This is used for generating autocomplete suggestions.
     */
    @NotNull
    public StaplerCustomJellyTagfileXmlElementDescriptor @NotNull [] getTagDescriptors() {
        List<StaplerCustomJellyTagfileXmlElementDescriptor> r = new ArrayList<>();
        for (PsiFile f : dir.getFiles()) {
            if (!isTagFile(f)) continue;
            if (f instanceof XmlFile) r.add(new StaplerCustomJellyTagfileXmlElementDescriptor(this, (XmlFile) f));
        }
        return r.toArray(new StaplerCustomJellyTagfileXmlElementDescriptor[0]);
    }

    private boolean isTagFile(PsiFile file) {
        for (String ext : DOT_TAGFILE_EXTENSIONS) {
            if (file.getName().endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    @Override
    @Nullable
    public XmlFile getDescriptorFile() {
        return null;
    }

    @Override
    public PsiElement getDeclaration() {
        return dir.findFile("taglib");
    }

    @Override
    public String getName(PsiElement context) {
        return uri;
    }

    @Override
    public String getName() {
        return uri;
    }

    /**
     * This method is called when this object is instantiated by IDEA as metadata to existing object.
     *
     * <p>This special pseudo document has to be &lt;schema uri="..." xmlns="dummy-schema-url"/>
     */
    @Override
    public void init(PsiElement element) {
        XmlDocument doc = (XmlDocument) element;
        dir = doc.getContainingFile().getUserData(StaplerCustomJellyTagLibraryXmlSchemaProvider.MODULE);
        uri = doc.getRootTag().getAttribute("uri", "").getValue();
    }

    @Override
    public Object @NotNull [] getDependencies() {
        return new Object[] {dir};
    }

    public static StaplerCustomJellyTagLibraryXmlNSDescriptor get(XmlTag tag) {
        if (!(tag.getContainingFile() instanceof JellyFile)) return null; // this tag is not in a jelly script

        String nsUri = tag.getNamespace();
        return get(nsUri, ModuleUtil.findModuleForPsiElement(tag));
    }

    public static StaplerCustomJellyTagLibraryXmlNSDescriptor get(String nsUri, Module module) {
        // just trying to be defensive
        if (module == null) return null;
        if (nsUri.length() == 0) return null;

        JavaPsiFacade javaPsi = JavaPsiFacade.getInstance(module.getProject());

        String pkgName = nsUri.substring(1).replace('/', '.');
        // this invocation below successfully finds packages that include invalid characters like 'a-b-c'
        PsiPackage pkg = javaPsi.findPackage(pkgName);
        if (pkg == null) return null;

        PsiDirectory[] dirs =
                pkg.getDirectories(GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module, false));

        for (PsiDirectory dir : dirs)
            if (dir.findFile("taglib") != null)
                // this is a tag library
                return new StaplerCustomJellyTagLibraryXmlNSDescriptor(nsUri, dir);

        return null;
    }
}
