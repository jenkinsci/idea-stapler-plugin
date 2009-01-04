package org.kohsuke.stapler.idea;

import com.intellij.codeInspection.InspectionToolProvider;
import com.intellij.facet.FacetTypeRegistry;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.lang.CompositeLanguage;
import com.intellij.lang.StdLanguages;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.kohsuke.stapler.idea.language.JellyLanguageExtension;

/**
 * @author Kohsuke Kawaguchi
 */
public class StaplerApplicationComponent implements ApplicationComponent, InspectionToolProvider {
    @NonNls @NotNull
    public String getComponentName() {
        return getClass().getSimpleName();
    }

    public void initComponent() {
        FacetTypeRegistry.getInstance().registerFacetType(StaplerFacetType.INSTANCE);

        {// register the .jelly extension as XML
            FileTypeManager fm = FileTypeManager.getInstance();
            fm.associateExtension(fm.getFileTypeByExtension("xml"),"jelly");
        }
        // register a custom vocabulary for Jelly
//        ((CompositeLanguage) StdLanguages.XML).registerLanguageExtension(
//                JellyLanguageExtension.INSTANCE);
    }

    public void disposeComponent() {
        // noop
    }

    public Class[] getInspectionClasses() {
        return new Class[]{JexlInspection.class};
    }
}
