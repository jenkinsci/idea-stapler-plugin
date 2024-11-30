package org.kohsuke.stapler.idea.descriptor;

import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlElement;
import com.intellij.xml.XmlAttributeDescriptor;
import org.jetbrains.annotations.NotNull;
import org.kohsuke.stapler.idea.dom.model.AttributeTag;

/**
 * {@link XmlAttributeDescriptor} for a tag file.
 *
 * @author Kohsuke Kawaguchi
 */
public class StaplerCustomJellyTagfileXmlAttributeDescriptor implements XmlAttributeDescriptor {
    /** Which element do we belong? */
    private StaplerCustomJellyTagfileXmlElementDescriptor element;
    /** Definition of this attribute. */
    private final AttributeTag def;

    StaplerCustomJellyTagfileXmlAttributeDescriptor(
            StaplerCustomJellyTagfileXmlElementDescriptor element, AttributeTag def) {
        this.element = element;
        this.def = def;
    }

    public AttributeTag getModel() {
        return def;
    }

    /**
     * This only seems to be used when a new tag is inserted, to automatically complete all required attributes.
     *
     * <p>But not having a required attribute missing in XML doesn't automatically flag an error annotation.
     */
    @Override
    public boolean isRequired() {
        return def.isRequired();
    }

    @Override
    public boolean isFixed() {
        return false;
    }

    @Override
    public boolean hasIdType() {
        return false;
    }

    @Override
    public boolean hasIdRefType() {
        return false;
    }

    @Override
    public String getDefaultValue() {
        return null;
    }

    @Override
    public boolean isEnumerated() {
        return false;
    }

    @Override
    public String[] getEnumeratedValues() {
        return new String[0];
    }

    /** Validates the attribute value and returns an error message, if there's an error. */
    @Override
    public String validateValue(XmlElement context, String value) {
        return null;
    }

    @Override
    public PsiElement getDeclaration() {
        return def.tag;
    }

    @Override
    public String getName(PsiElement context) {
        return getName();
    }

    @Override
    public String getName() {
        return def.getName();
    }

    @Override
    public void init(PsiElement element) {}

    @Override
    public Object @NotNull [] getDependencies() {
        return new Object[] {def.tag};
    }
}
