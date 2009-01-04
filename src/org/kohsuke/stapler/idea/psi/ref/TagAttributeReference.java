package org.kohsuke.stapler.idea.psi.ref;

import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomManager;
import org.kohsuke.stapler.idea.dom.model.JellyTag;

/**
 * Reference to a declaration of an attribute in a Jelly tag.
 *
 * @author Kohsuke Kawaguchi
 */
public class TagAttributeReference extends PsiReferenceBase<XmlAttribute> {
    private final TagReference parent;

    public TagAttributeReference(TagReference parent, XmlAttribute ref) {
        super(ref,ref.getTextRange());
        this.parent = parent;
    }

    /**
     * Resolves to the &lt;st:attribute> element.
     */
    public XmlTag resolve() {
        XmlFile file = parent.resolve();
        if(file==null)      return null;

        JellyTag tag = DomManager.getDomManager(file.getProject()).getFileElement(file, JellyTag.class).getRootElement();
        return tag.getDocumentation().getAttributeByName(myElement.getLocalName()).getXmlTag();
    }

    public Object[] getVariants() {
        return new Object[0];
    }
}

