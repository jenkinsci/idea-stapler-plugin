package org.kohsuke.stapler.idea.descriptor;

import com.intellij.psi.impl.source.xml.XmlElementDescriptorProvider;
import com.intellij.psi.xml.XmlTag;
import com.intellij.xml.XmlElementDescriptor;

/**
 * Contributes {@link XmlElementDescriptorProvider} for Jelly tags.
 *
 * @author Kohsuke Kawaguchi
 */
public class XmlElementDescriptorProviderImpl implements XmlElementDescriptorProvider {
    @Override
    public XmlElementDescriptor getDescriptor(XmlTag tag) {
        StaplerCustomJellyTagLibraryXmlNSDescriptor ns = StaplerCustomJellyTagLibraryXmlNSDescriptor.get(tag);
        if (ns != null) return ns.getElementDescriptor(tag);
        return null;
    }
}
