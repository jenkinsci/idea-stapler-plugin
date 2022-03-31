package org.kohsuke.stapler.idea.descriptor;

import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import com.intellij.xml.XmlAttributeDescriptor;
import com.intellij.xml.XmlElementDescriptor;
import com.intellij.xml.XmlElementsGroup;
import com.intellij.xml.XmlNSDescriptor;
import com.intellij.xml.impl.schema.AnyXmlAttributeDescriptor;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Kohsuke Kawaguchi
 */
public class AnyElementDescriptorImpl implements XmlElementDescriptor {
    private final XmlTag tag;

    public AnyElementDescriptorImpl(XmlTag tag) {
        this.tag = tag;
    }

    @Override
    public String getQualifiedName() {
        return tag.getName();
    }

    @Override
    public String getDefaultName() {
        return tag.getName();
    }

    @Override
    public XmlElementDescriptor[] getElementsDescriptors(XmlTag context) {
        return XmlElementDescriptor.EMPTY_ARRAY;
    }

    @Override
    public XmlElementDescriptor getElementDescriptor(XmlTag childTag, XmlTag contextTag) {
        XmlNSDescriptorImpl ns = XmlNSDescriptorImpl.get(childTag);
        if(ns!=null)
            return ns.getElementDescriptor(childTag);
        return new AnyElementDescriptorImpl(childTag);
    }

    @Override
    public XmlAttributeDescriptor[] getAttributesDescriptors(@Nullable XmlTag context) {
        return XmlAttributeDescriptor.EMPTY;
    }

    @Override
    public XmlAttributeDescriptor getAttributeDescriptor(@NonNls String attributeName, @Nullable XmlTag context) {
        return new AnyXmlAttributeDescriptor(attributeName);
    }

    @Override
    public XmlAttributeDescriptor getAttributeDescriptor(XmlAttribute attribute) {
        return new AnyXmlAttributeDescriptor(attribute.getName());
    }

    @Override
    public XmlNSDescriptor getNSDescriptor() {
        
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public int getContentType() {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public PsiElement getDeclaration() {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName(PsiElement context) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public void init(PsiElement element) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Object @NotNull [] getDependencies() {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public XmlElementsGroup getTopGroup() {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public String getDefaultValue() {
        return null;
    }
}
