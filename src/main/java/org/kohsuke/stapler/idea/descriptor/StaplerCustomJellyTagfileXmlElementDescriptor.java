package org.kohsuke.stapler.idea.descriptor;

import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.SimpleFieldCache;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.xml.XmlAttributeDescriptor;
import com.intellij.xml.XmlElementDescriptor;
import com.intellij.xml.XmlNSDescriptor;
import com.intellij.xml.impl.dtd.BaseXmlElementDescriptorImpl;
import com.intellij.xml.impl.schema.AnyXmlElementDescriptor;
import com.intellij.xml.util.XmlUtil;
import java.util.HashMap;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kohsuke.stapler.idea.dom.model.AttributeTag;
import org.kohsuke.stapler.idea.dom.model.DocumentationTag;

/** @author Kohsuke Kawaguchi */
public class StaplerCustomJellyTagfileXmlElementDescriptor extends BaseXmlElementDescriptorImpl {
    private final StaplerCustomJellyTagLibraryXmlNSDescriptor nsDescriptor;
    private final XmlFile tagFile;
    private volatile Boolean hasInvokeBody;
    private static final SimpleFieldCache<Boolean, StaplerCustomJellyTagfileXmlElementDescriptor> hasInvokeBodyCache =
            new SimpleFieldCache<>() {
                @Override
                protected Boolean compute(StaplerCustomJellyTagfileXmlElementDescriptor xmlElementDescriptor) {
                    return xmlElementDescriptor.lookForInvokeBody();
                }

                @Override
                protected Boolean getValue(StaplerCustomJellyTagfileXmlElementDescriptor xmlElementDescriptor) {
                    return xmlElementDescriptor.hasInvokeBody;
                }

                @Override
                protected void putValue(
                        Boolean hasInvokeBody, StaplerCustomJellyTagfileXmlElementDescriptor xmlElementDescriptor) {
                    xmlElementDescriptor.hasInvokeBody = hasInvokeBody;
                }
            };

    public StaplerCustomJellyTagfileXmlElementDescriptor(
            StaplerCustomJellyTagLibraryXmlNSDescriptor nsDescriptor, XmlFile tagFile) {
        this.nsDescriptor = nsDescriptor;
        this.tagFile = tagFile;
    }

    @Override
    public XmlElementDescriptor getElementDescriptor(XmlTag child, XmlTag context) {
        if (!invokesBody()) {
            return null;
        } else {
            StaplerCustomJellyTagLibraryXmlNSDescriptor ns = StaplerCustomJellyTagLibraryXmlNSDescriptor.get(child);
            if (ns != null) {
                return ns.getElementDescriptor(child);
            } else {
                // Reuse XSD implementation of Any element descriptor to allow every possible tag in the body.
                return new AnyXmlElementDescriptor(this, getNSDescriptor());
            }
        }
    }

    @Override
    protected XmlElementDescriptor[] doCollectXmlDescriptors(XmlTag xmlTag) {
        // Must be overridden because this class extends from base DTD class but not used since `getElementDescriptor`
        // is implemented
        return new XmlElementDescriptor[0];
    }

    @Override
    protected XmlAttributeDescriptor[] collectAttributeDescriptors(XmlTag xmlTag) {
        DocumentationTag tag = getModel();
        if (tag == null) return XmlAttributeDescriptor.EMPTY;
        List<AttributeTag> atts = tag.getAttributes();
        XmlAttributeDescriptor[] descriptors = new XmlAttributeDescriptor[atts.size()];
        int i = 0;
        for (AttributeTag a : atts) {
            descriptors[i++] = new StaplerCustomJellyTagfileXmlAttributeDescriptor(this, a);
        }
        return descriptors;
    }

    @Nullable
    public DocumentationTag getModel() {
        assert tagFile != null;
        XmlDocument doc = tagFile.getDocument();
        if (doc == null) return null;
        XmlTag root = doc.getRootTag();
        if (root == null) return null;
        XmlTag[] docs = root.findSubTags("documentation", "jelly:stapler");
        if (docs.length == 0) return null;

        return new DocumentationTag(docs[0]);
    }

    @Override
    protected HashMap<String, XmlAttributeDescriptor> collectAttributeDescriptorsMap(XmlTag xmlTag) {
        HashMap<String, XmlAttributeDescriptor> r = new HashMap<>();
        for (XmlAttributeDescriptor a : getAttributesDescriptors(xmlTag)) r.put(a.getName(xmlTag), a);
        return r;
    }

    @Override
    protected HashMap<String, XmlElementDescriptor> collectElementDescriptorsMap(XmlTag xmlTag) {
        HashMap<String, XmlElementDescriptor> r = new HashMap<>();
        for (XmlElementDescriptor e : getElementsDescriptors(xmlTag)) r.put(e.getName(xmlTag), e);
        return r;
    }

    @Override
    public String getQualifiedName() {
        // DTD based descriptor just does the same.
        // It's XSD and RNG who provide implementation of this.
        // Maybe this should provide one as well based on the package name of the tagFile. What features does it affect?
        return getName();
    }

    @Override
    public String getDefaultName() {
        return getQualifiedName();
    }

    @Override
    public XmlNSDescriptor getNSDescriptor() {
        return nsDescriptor;
    }

    @Override
    public int getContentType() {
        if (invokesBody()) {
            return CONTENT_TYPE_ANY;
        } else {
            return CONTENT_TYPE_EMPTY;
        }
    }

    @Override
    public PsiElement getDeclaration() {
        return tagFile;
    }

    @Override
    public String getName(PsiElement context) {
        String n = getName();
        if (context instanceof XmlElement) {
            XmlTag xmltag = PsiTreeUtil.getParentOfType(context, XmlTag.class, false);
            if (xmltag != null) {
                String prefix = xmltag.getPrefixByNamespace(nsDescriptor.getName());
                if (prefix != null && prefix.length() > 0) return prefix + ':' + n;
            }
        }
        return n;
    }

    @Override
    public String getName() {
        String fileName = tagFile.getName();
        return fileName.substring(0, fileName.length() - 6); // cut off extension
    }

    @Override
    public void init(PsiElement element) {
        // This class is not registered by metadata contributor, so this method is probably never called hence empty.
    }

    @Override
    public Object @NotNull [] getDependencies() {
        return new Object[] {nsDescriptor, tagFile};
    }

    private boolean invokesBody() {
        Boolean value = hasInvokeBodyCache.get(this);
        return value != null && value;
    }

    private boolean lookForInvokeBody() {
        final Ref<XmlTag> result = new Ref<>();
        XmlUtil.processXmlElements(
                tagFile,
                element -> {
                    if (element instanceof XmlTag && isJellyDefineInvokeBodyTag((XmlTag) element)) {
                        result.set((XmlTag) element);
                        return false;
                    }
                    return true;
                },
                true);
        return !result.isNull();
    }

    private static boolean isJellyDefineInvokeBodyTag(XmlTag tag) {
        return tag.getNamespace().equals("jelly:define") && tag.getLocalName().equals("invokeBody");
    }
}
