package org.kohsuke.stapler.idea.descriptor;

import com.intellij.xml.XmlElementDescriptor;
import com.intellij.xml.XmlAttributeDescriptor;
import com.intellij.xml.XmlElementsGroup;
import com.intellij.xml.XmlNSDescriptor;
import com.intellij.xml.impl.schema.AnyXmlAttributeDescriptor;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NonNls;

/**
 * @author Kohsuke Kawaguchi
 */
public class AnyElementDescriptorImpl implements XmlElementDescriptor {
    private final XmlTag tag;

    public AnyElementDescriptorImpl(XmlTag tag) {
        this.tag = tag;
    }

    public String getQualifiedName() {
        return tag.getName();
    }

    public String getDefaultName() {
        return tag.getName();
    }

    public XmlElementDescriptor[] getElementsDescriptors(XmlTag context) {
        return XmlElementDescriptor.EMPTY_ARRAY;
    }

    public XmlElementDescriptor getElementDescriptor(XmlTag childTag, XmlTag contextTag) {
        XmlNSDescriptorImpl ns = XmlNSDescriptorImpl.get(childTag);
        if(ns!=null)
            return ns.getElementDescriptor(childTag);
        return new AnyElementDescriptorImpl(childTag);
    }

    public XmlAttributeDescriptor[] getAttributesDescriptors(@Nullable XmlTag context) {
        return XmlAttributeDescriptor.EMPTY;
    }

    public XmlAttributeDescriptor getAttributeDescriptor(@NonNls String attributeName, @Nullable XmlTag context) {
        return new AnyXmlAttributeDescriptor(attributeName);
    }

    public XmlAttributeDescriptor getAttributeDescriptor(XmlAttribute attribute) {
        return new AnyXmlAttributeDescriptor(attribute.getName());
    }

    public XmlNSDescriptor getNSDescriptor() {
        
        // TODO
        throw new UnsupportedOperationException();
    }

    public int getContentType() {
        // TODO
        throw new UnsupportedOperationException();
    }

    public PsiElement getDeclaration() {
        // TODO
        throw new UnsupportedOperationException();
    }

    public String getName(PsiElement context) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public String getName() {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void init(PsiElement element) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public Object[] getDependences() {
        // TODO
        throw new UnsupportedOperationException();
    }

    public XmlElementsGroup getTopGroup() {
        // TODO
        throw new UnsupportedOperationException();
    }

    public String getDefaultValue() {
        return null;
    }
}
