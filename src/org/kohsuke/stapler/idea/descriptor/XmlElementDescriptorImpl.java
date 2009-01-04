package org.kohsuke.stapler.idea.descriptor;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.xml.XmlAttributeDescriptor;
import com.intellij.xml.XmlElementDescriptor;
import com.intellij.xml.XmlNSDescriptor;
import com.intellij.xml.impl.dtd.BaseXmlElementDescriptorImpl;

import java.util.HashMap;

import org.apache.commons.lang.ArrayUtils;

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

    protected XmlElementDescriptor[] doCollectXmlDescriptors(XmlTag xmlTag) {
        return new XmlElementDescriptor[0];
    }

    protected XmlAttributeDescriptor[] collectAttributeDescriptors(XmlTag xmlTag) {
        return new XmlAttributeDescriptor[] {
              new XmlAttributeDescriptor() {
                  public boolean isRequired() {
                      return true;
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

                  public String validateValue(XmlElement context, String value) {
                      if(value.length()%2==1)
                          return "Even length";
                      return null;
                  }

                  public PsiElement getDeclaration() {
                      // TODO
                      return tagFile;
                  }

                  public String getName(PsiElement context) {
                      return getName();
                  }

                  public String getName() {
                      return "att1";
                  }

                  public void init(PsiElement element) {
                  }

                  public Object[] getDependences() {
                      return ArrayUtils.EMPTY_OBJECT_ARRAY;
                  }
              }
        };
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
        return CONTENT_TYPE_MIXED;
    }

    public PsiElement getDeclaration() {
        return tagFile;
    }

    public String getName(PsiElement context) {
        String n = getName();
        if (context instanceof XmlElement) {
            XmlTag xmltag = PsiTreeUtil.getParentOfType(context, XmlTag.class, false);
            if (xmltag != null) {
                String prefix = xmltag.getPrefixByNamespace(nsDescriptor.uri);
                if (prefix != null && prefix.length() > 0)
                    return prefix + ':' + n;
            }
        }
        return n;
    }

    public String getName() {
        return tagFile.getName();
    }

    public void init(PsiElement element) {
    }

    public Object[] getDependences() {
        return new Object[] {nsDescriptor,tagFile};
    }
}
