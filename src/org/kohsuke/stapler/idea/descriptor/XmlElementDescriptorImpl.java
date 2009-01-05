package org.kohsuke.stapler.idea.descriptor;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import com.intellij.xml.XmlAttributeDescriptor;
import com.intellij.xml.XmlElementDescriptor;
import com.intellij.xml.XmlNSDescriptor;
import com.intellij.xml.impl.dtd.BaseXmlElementDescriptorImpl;
import org.kohsuke.stapler.idea.dom.model.AttributeTag;
import org.kohsuke.stapler.idea.dom.model.JellyTag;

import java.util.HashMap;
import java.util.List;

/**
 * @author Kohsuke Kawaguchi
 */
public class XmlElementDescriptorImpl extends BaseXmlElementDescriptorImpl {
    private final XmlNSDescriptorImpl nsDescriptor;
    private final XmlFile tagFile;

    public XmlElementDescriptorImpl(XmlNSDescriptorImpl nsDescriptor, XmlFile tagFile) {
        this.nsDescriptor = nsDescriptor;
        this.tagFile = tagFile;
    }

    @Override
    public XmlElementDescriptor getElementDescriptor(XmlTag child, XmlTag context) {
        XmlNSDescriptorImpl ns = XmlNSDescriptorImpl.get(child);
        if(ns==null) {
            {// here I'm trying to return a descriptor that allows anything/
                /*
                This didn't work --- it works on elements on non-empty namespaces,
                but empty namespaces are marked as errors.

                return NullElementDescriptor.getInstance();
                */

                /*
                This didn't work either.
                XmlNSDescriptor nsd = tagFile.getDocument().getDefaultNSDescriptor(child.getNamespace(), false);
                XmlElementDescriptor d = nsd.getElementDescriptor(child);
                return d;
                */
                
                PsiFile f = child.getContainingFile();
                XmlNSDescriptor nsd = ((XmlFile)f).getDocument().getDefaultNSDescriptor(child.getNamespace(), false);
                XmlElementDescriptor d = nsd.getElementDescriptor(child);
                return d;
            }

        } else
            return ns.getElementDescriptor(child);
    }

    protected XmlElementDescriptor[] doCollectXmlDescriptors(XmlTag xmlTag) {
        return new XmlElementDescriptor[0];
    }

    protected XmlAttributeDescriptor[] collectAttributeDescriptors(XmlTag xmlTag) {
        DomFileElement<JellyTag> root = DomManager.getDomManager(tagFile.getProject()).getFileElement(tagFile, JellyTag.class);
        if(root==null)
            // huh?
            throw new AssertionError(tagFile);
        JellyTag tag = root.getRootElement();
        List<AttributeTag> atts = tag.getDocumentation().getAttributes();
        XmlAttributeDescriptor[] descriptors = new XmlAttributeDescriptor[atts.size()];
        int i=0;
        for (AttributeTag a : atts) {
            descriptors[i++] = new XmlAttributeDescriptorImpl(this,a);
        }
        return descriptors;
    }

    protected HashMap<String, XmlAttributeDescriptor> collectAttributeDescriptorsMap(XmlTag xmlTag) {
        HashMap<String, XmlAttributeDescriptor> r = new HashMap<String, XmlAttributeDescriptor>();
        for (XmlAttributeDescriptor a : getAttributesDescriptors(xmlTag))
            r.put(a.getName(xmlTag),a);
        return r;
    }

    protected HashMap<String, XmlElementDescriptor> collectElementDescriptorsMap(XmlTag xmlTag) {
        HashMap<String, XmlElementDescriptor> r = new HashMap<String, XmlElementDescriptor>();
        for (XmlElementDescriptor e : getElementsDescriptors(xmlTag))
            r.put(e.getName(xmlTag),e);
        return r;
    }

    public String getQualifiedName() {
        // TODO: how am I supposed to figure out the prefix?
        return getName();
    }

    public String getDefaultName() {
        return getQualifiedName();
    }

    public XmlNSDescriptor getNSDescriptor() {
        return nsDescriptor;
    }

    public int getContentType() {
        // TODO
        return CONTENT_TYPE_ANY;
    }

    public PsiElement getDeclaration() {
        return tagFile;
    }

    public String getName(PsiElement context) {
        String n = getName();
        if (context instanceof XmlElement) {
            XmlTag xmltag = PsiTreeUtil.getParentOfType(context, XmlTag.class, false);
            if (xmltag != null) {
                String prefix = xmltag.getPrefixByNamespace(nsDescriptor.getName());
                if (prefix != null && prefix.length() > 0)
                    return prefix + ':' + n;
            }
        }
        return n;
    }

    public String getName() {
        String fileName = tagFile.getName();
        return fileName.substring(0,fileName.length()-6); // cut off extension
    }

    public void init(PsiElement element) {
    }

    public Object[] getDependences() {
        return new Object[] {nsDescriptor,tagFile};
    }

}
