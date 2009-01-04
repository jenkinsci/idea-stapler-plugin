package org.kohsuke.stapler.idea.descriptor;

import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlElement;
import com.intellij.xml.XmlAttributeDescriptor;
import org.kohsuke.stapler.idea.dom.model.AttributeTag;

/**
 * {@link XmlAttributeDescriptor} for a tag file.
 *
 * @author Kohsuke Kawaguchi
*/
class XmlAttributeDescriptorImpl implements XmlAttributeDescriptor {
    /**
     * Which element do we belong?
     */
    private XmlElementDescriptorImpl element;
    /**
     * Definition of this attribute.
     */
    private final AttributeTag def;

    XmlAttributeDescriptorImpl(XmlElementDescriptorImpl element, AttributeTag def) {
        this.element = element;
        this.def = def;
    }

    /**
     * This only seems to be used when a new tag is inserted,
     * to automatically complete all required attributes.
     *
     * But not having a required attribute missing in XML doesn't automatically flag an error annotation.
     */
    public boolean isRequired() {
        return def.isRequired();
    }

    public boolean isFixed() {
        return false;
    }

    public boolean hasIdType() {
        return false;
    }

    public boolean hasIdRefType() {
        return false;
    }

    public String getDefaultValue() {
        return null;
    }

    public boolean isEnumerated() {
        return false;
    }

    public String[] getEnumeratedValues() {
        return new String[0];
    }

    /**
     * Validates the attribute value and returns an error message, if there's an error.
     */
    public String validateValue(XmlElement context, String value) {
        return null;
    }

    public PsiElement getDeclaration() {
        return def.getXmlTag();
    }

    public String getName(PsiElement context) {
        return getName();
    }

    public String getName() {
        return def.getSafeName();
    }

    public void init(PsiElement element) {
    }

    public Object[] getDependences() {
        return new Object[]{def.getXmlTag()};
    }
}
