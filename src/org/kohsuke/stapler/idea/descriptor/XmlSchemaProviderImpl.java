package org.kohsuke.stapler.idea.descriptor;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.xml.XmlFile;
import com.intellij.xml.XmlNSDescriptor;
import com.intellij.xml.XmlSchemaProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kohsuke.stapler.idea.StaplerApplicationComponent;

import java.util.Set;

/**
 *
 * <p>
 * This is not to be confused with W3C XML Schema. Rather,
 * it's a mechanism for locating a structure definition for a given URI
 * as {@link XmlFile}, such that its root metadata is {@link XmlNSDescriptor}. 
 *
 * @author Kohsuke Kawaguchi
 */
public class XmlSchemaProviderImpl extends XmlSchemaProvider {
    public XmlFile getSchema(@NotNull String url, @Nullable Module module, @NotNull PsiFile baseFile) {
        XmlNSDescriptorImpl d = XmlNSDescriptorImpl.get(url, module);
        if(d==null)     return null;

        XmlFile pseudoSchema = (XmlFile) PsiFileFactory.getInstance(module.getProject()).createFileFromText(
                url + ".xml", "<schema uri=\"" + url + "\" xmlns='" + StaplerApplicationComponent.DUMMY_SCHEMA_URL + "'>");
        pseudoSchema.putUserData(MODULE,d.getDir());
        return pseudoSchema;
    }

    /**
     * Before the {@link #getSchema(String, Module, PsiFile)}  method is called,
     * this method is called, presumably to see if this provider is applicable
     * to the file.
     *
     * <p>
     * In this case we are only interetsed in references from inside Jelly files,
     * so check that.
     */
    @Override
    public boolean isAvailable(@NotNull XmlFile file) {
        return file.getName().endsWith(".jelly");
    }

    /*package*/ static final Key<PsiDirectory> MODULE = Key.create(PsiDirectory.class.getName());
}
