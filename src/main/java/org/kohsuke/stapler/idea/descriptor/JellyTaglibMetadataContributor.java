package org.kohsuke.stapler.idea.descriptor;

import com.intellij.psi.filters.AndFilter;
import com.intellij.psi.filters.ClassFilter;
import com.intellij.psi.filters.position.NamespaceFilter;
import com.intellij.psi.meta.MetaDataContributor;
import com.intellij.psi.meta.MetaDataRegistrar;
import com.intellij.psi.xml.XmlDocument;

public class JellyTaglibMetadataContributor implements MetaDataContributor {
    public static final String DUMMY_SCHEMA_URL = "dummy-schema-url";

    @Override
    public void contributeMetaData(MetaDataRegistrar registrar) {
        // this is so that we can create an XmlFile whose getRootElement().getMetaData()
        // returns XmlNSDescriptorImpl. This is necessary to load schemas on the fly
        registrar.registerMetaData(
                new AndFilter(
                        new ClassFilter(XmlDocument.class),
                        new NamespaceFilter(DUMMY_SCHEMA_URL)),
                XmlNSDescriptorImpl.class
        );
    }
}
