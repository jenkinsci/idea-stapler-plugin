package org.kohsuke.stapler.idea;

import com.intellij.codeInspection.InspectionToolProvider;
import com.intellij.facet.FacetTypeRegistry;
import com.intellij.javaee.ExternalResourceManager;
import com.intellij.lang.LanguageAnnotators;
import com.intellij.lang.LanguageDocumentation;
import com.intellij.lang.xml.XMLLanguage;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.psi.filters.AndFilter;
import com.intellij.psi.filters.ClassFilter;
import com.intellij.psi.filters.position.NamespaceFilter;
import com.intellij.psi.meta.MetaDataRegistrar;
import com.intellij.psi.xml.XmlDocument;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.kohsuke.stapler.idea.descriptor.XmlNSDescriptorImpl;

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
            String[] schemas = {"ant","antlr","bean","beanshell","betwixt","bsf","core","define","dynabean","email","fmt","html","http","interaction","jaxme","jetty","jface","jms","jmx","jsl","junit","log","memory","ojb","quartz","regexp","soap","sql","swing","swt","threads","util","validate","velocity","xml","xmlunit","stapler"};
            for( String s: schemas )
                erm.addStdResource(
                        "jelly:"+s,  // namespace URI
                        "/org/kohsuke/stapler/idea/resources/schemas/"+s+".xsd",
                        getClass());
        }

        // this is so that we can create an XmlFile whose getRootElement().getMetaData()
        // returns XmlNSDescriptorImpl. This is necessary to load schemas on the fly
        MetaDataRegistrar.getInstance().registerMetaData(
            new AndFilter(
                new ClassFilter(XmlDocument.class),
                new NamespaceFilter(DUMMY_SCHEMA_URL)),
            XmlNSDescriptorImpl.class
        );

        LanguageAnnotators.INSTANCE.addExplicitExtension(
                XMLLanguage.INSTANCE, new JellyAnnotator());

        LanguageDocumentation.INSTANCE.addExplicitExtension(
                XMLLanguage.INSTANCE, new JellyDocumentationProvider()
        );

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

    public static final String DUMMY_SCHEMA_URL = "dummy-schema-url";

}
