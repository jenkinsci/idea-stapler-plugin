package org.kohsuke.stapler.idea;

import com.intellij.codeInspection.InspectionToolProvider;
import com.intellij.facet.FacetTypeRegistry;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.lang.CompositeLanguage;
import com.intellij.lang.StdLanguages;
import com.intellij.javaee.ExternalResourceManager;
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

        {// register schemas for Jelly
            ExternalResourceManager erm = ExternalResourceManager.getInstance();
            String[] schemas = {"ant","antlr","bean","beanshell","betwixt","bsf","core","define","dynabean","email","fmt","html","http","interaction","jaxme","jetty","jface","jms","jmx","jsl","junit","log","memory","ojb","quartz","regexp","soap","sql","swing","swt","threads","util","validate","velocity","xml","xmlunit"};
            for( String s: schemas )
                erm.addStdResource(
                        "jelly:"+s,  // namespace URI
                        "/org/kohsuke/stapler/idea/resources/schemas/"+s+".xsd",
                        getClass());
        }

        // register a custom vocabulary for Jelly
        // still experimenting.
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
