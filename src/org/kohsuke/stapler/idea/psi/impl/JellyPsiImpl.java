package org.kohsuke.stapler.idea.psi.impl;

import com.intellij.extapi.psi.MetadataPsiElementBase;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLock;
import com.intellij.psi.PsiManager;
import com.intellij.psi.xml.XmlComment;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlTag;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kohsuke.stapler.idea.language.JellyLanguage;
import org.kohsuke.stapler.idea.psi.JellyFile;
import org.kohsuke.stapler.idea.psi.JellyPsi;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kohsuke Kawaguchi
 */
abstract class JellyPsiImpl extends MetadataPsiElementBase implements JellyPsi {
    // semi-final
    private PsiElement prev,next;
    private JellyPsi[] children;
    private final JellyPsi parent;

    protected JellyPsiImpl(JellyPsi parent, XmlElement sourceElement) {
        super(sourceElement);
        this.parent = parent;
    }

    public XmlElement getSourceElement() {
        return (XmlElement)super.getSourceElement();
    }

    public JellyFile getContainingFile() {
        return (JellyFile)super.getContainingFile();
    }

    public PsiElement getPrevSibling() {
        return prev;
    }

    public PsiElement getNextSibling() {
        return next;
    }

    @Nullable
    public JellyPsi getFirstChild() {
        JellyPsi[] children = getChildren();
        return children.length==0 ? null : children[0];
    }

    @Nullable
    public JellyPsi getLastChild() {
        JellyPsi[] children = getChildren();
        return children.length==0 ? null : children[children.length-1];
    }

    @NotNull
    public final JellyLanguage getLanguage() {
        return JellyLanguage.INSTANCE;
    }

    public JellyPsi getParent() {
        return parent;
    }

    public JellyPsi getJellyParent() {
        return parent;
    }

    @Nullable
    public ASTNode getNode() {
        return getSourceElement().getNode();
    }

    @NotNull
    public JellyPsi[] getChildren() {
        synchronized (PsiLock.LOCK) {
            if(children==null) {
                children = wrap(this,getSourceElement().getChildren());
            }
            return children;
        }
    }

    public JellyPsi findElementAt(int offset) {
        if (!isValid())
            return null;
        TextRange ownRange = getTextRange();
        int offsetInFile = offset + ownRange.getStartOffset();

        for (JellyPsi c : getChildren()) {
            TextRange textRange = c.getTextRange();
            if (textRange.contains(offsetInFile))
                return c.findElementAt(offsetInFile - textRange.getStartOffset());
        }

        return ownRange.contains(offsetInFile) ? this : null;
    }

    public void clearCaches() {
        synchronized (PsiLock.LOCK) {
            children = null;
            prev = null;
            next = null;
        }
    }

    public boolean isPhysical() {
        return getSourceElement().isPhysical();
    }

    public boolean isValid() {
        return getSourceElement().isValid();
    }

    public PsiManager getManager() {
        return getSourceElement().getManager();
    }

    

    static JellyPsiImpl[] wrap(JellyPsi parent, PsiElement[] children) {
        List<JellyPsiImpl> wrapped = new ArrayList<JellyPsiImpl>(children.length);
        for (PsiElement e : children) {
            JellyPsiImpl child = wrap(parent, e);
            if(child!=null)
                wrapped.add(child);
        }

        JellyPsiImpl[] a = wrapped.toArray(new JellyPsiImpl[wrapped.size()]);

        // set up sibling relationship
        JellyPsiImpl prev=null;
        for (JellyPsiImpl c : a) {
            c.prev = prev;
            if(prev!=null)
                prev.next = c;
            prev = c;
        }
        return a;
    }

    // clone isn't really invoked during edit
    protected Object clone() {
        return super.clone();
    }

    static JellyPsiImpl wrap(JellyPsi parent, PsiElement e) {
        if(e instanceof XmlComment)
            return new JellyCommentImpl(parent, (XmlComment)e);
        if(!(e instanceof XmlTag))
            return null;
        XmlTag tag = (XmlTag)e;

        return new JellyTagImpl(parent,tag);
    }
}
