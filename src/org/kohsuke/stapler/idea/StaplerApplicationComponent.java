package org.kohsuke.stapler.idea;

import com.intellij.codeInspection.InspectionToolProvider;
import com.intellij.facet.FacetTypeRegistry;
import com.intellij.javaee.ExternalResourceManager;
import com.intellij.lang.LanguageAnnotators;
import com.intellij.lang.LanguageDocumentation;
import com.intellij.lang.xml.XMLLanguage;
import com.intellij.openapi.application.ApplicationManager;
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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

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


        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            public void run() {
                {// register the .jelly extension as XML
                    FileTypeManager fm = FileTypeManager.getInstance();
                    fm.associateExtension(fm.getFileTypeByExtension("xml"),"jelly");
                }

                {// register schemas for Jelly
                    ExternalResourceManager erm = ExternalResourceManager.getInstance();
                    String[] schemas = {"ant","antlr","bean","beanshell","betwixt","bsf","core","define","dynabean","email","fmt","html","http","interaction","jaxme","jetty","jface","jms","jmx","jsl","junit","log","memory","ojb","quartz","regexp","soap","sql","swing","swt","threads","util","validate","velocity","xml","xmlunit","stapler"};
                    for( String s: schemas ) {
                        String name = "/org/kohsuke/stapler/idea/resources/schemas/" + s + ".xsd";
                        URL res = getClass().getClassLoader().getResource(name);
                        if (res==null)
                            throw new AssertionError("Failed to find schema resource: "+name);

                        { // Mangle Resource URL to match what IntelliJ expects for the location argument
                            String extForm = res.toExternalForm();
                            String jarSuffix = "";

                            { // Strip jar: prefix and !-suffix before fixing embedded file: url
                                if (extForm.startsWith("jar:")) {
                                    int bangIndex = extForm.indexOf('!');
                                    if (bangIndex >= 0) {
                                        jarSuffix = extForm.substring(bangIndex);
                                        extForm = extForm.substring("jar:".length(), bangIndex);
                                    } else {
                                        extForm = extForm.substring("jar:".length());
                                    }
                                }
                            }

                            { // convert file: URL to path, per
                              // https://weblogs.java.net/blog/kohsuke/archive/2007/04/how_to_convert.html
                                if (extForm.startsWith("file:")) {
                                    try {
                                        extForm = new File(new URI(extForm)).getPath();
                                    } catch (URISyntaxException e) {
                                        try {
                                            extForm = new File(new URL(extForm).getPath()).getPath();
                                        } catch (MalformedURLException l) {
                                            extForm = extForm.substring("file:".length());
                                        }
                                    }

                                }
                            }
                            
                            erm.addResource(
                                    "jelly:"+s,  // namespace URI
                                    extForm + jarSuffix); // re-append !-suffix
                        }
                    }
                }
            }
        });

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
