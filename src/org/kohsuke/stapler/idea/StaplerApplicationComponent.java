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
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.filters.AndFilter;
import com.intellij.psi.filters.ClassFilter;
import com.intellij.psi.filters.position.NamespaceFilter;
import com.intellij.psi.meta.MetaDataRegistrar;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.util.io.URLUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kohsuke.stapler.idea.descriptor.XmlNSDescriptorImpl;

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

                            // the intention was to call URLUtil directly, but there appears to be some build
                            // incompatibility that is simply easier to work around by copying the method source into
                            // this class
                            Pair<String, String> pair = splitJarUrl(extForm);
                            if (pair != null) {

                                // This procedure is necessary for complete support for this resource in the
                                // {@link MapExternalResourceDialog}, but is not necessary for Jelly namespace resolution
                                // in the editor
                                extForm = pair.first + URLUtil.JAR_SEPARATOR + pair.second;
                            } else {
                                // the scheme separator appears to be minimally necessary for the Jelly URLResolver to
                                // identify this as a proper file: url.
                                extForm = extForm.replaceAll("file:", "file://");
                            }

                            erm.addResource(
                                    "jelly:"+s,  // namespace URI
                                    URLUtil.unescapePercentSequences(extForm)); // xmlns checking fails without this call
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

    /**
     * Splits .jar URL along a separator and strips "jar" and "file" prefixes if any.
     * Returns a pair of path to a .jar file and entry name inside a .jar, or null if the URL does not contain a separator.
     *
     * Copied verbatim from {@link URLUtil#splitJarUrl(String)} from source, because when called from URLUtil, it
     * always returns null, even if the JAR_SEPARATOR is clearly present in the URL. I imagine this is due to the source
     * being more recent than the built library.
     *
     * E.g. "jar:file:///path/to/jar.jar!/resource.xml" is converted into ["/path/to/jar.jar", "resource.xml"].
     */
    @Nullable
    public static Pair<String, String> splitJarUrl(@NotNull String url) {
        int pivot = url.indexOf(URLUtil.JAR_SEPARATOR);
        if (pivot < 0) return null;

        String resourcePath = url.substring(pivot + 2);
        String jarPath = url.substring(0, pivot);

        if (StringUtil.startsWithConcatenation(jarPath, URLUtil.JAR_PROTOCOL, ":")) {
            jarPath = jarPath.substring(URLUtil.JAR_PROTOCOL.length() + 1);
        }

        if (jarPath.startsWith(URLUtil.FILE_PROTOCOL)) {
            jarPath = jarPath.substring(URLUtil.FILE_PROTOCOL.length());
            if (jarPath.startsWith(URLUtil.SCHEME_SEPARATOR)) {
                jarPath = jarPath.substring(URLUtil.SCHEME_SEPARATOR.length());
            }
            else if (StringUtil.startsWithChar(jarPath, ':')) {
                jarPath = jarPath.substring(1);
            }
        }

        return Pair.create(jarPath, resourcePath);
    }
}
