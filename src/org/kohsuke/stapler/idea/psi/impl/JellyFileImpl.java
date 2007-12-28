package org.kohsuke.stapler.idea.psi.impl;

import com.intellij.extapi.psi.LightPsiFileBase;
import com.intellij.lang.StdLanguages;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLock;
import com.intellij.psi.impl.source.LightPsiFileImpl;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlFile;
import org.jetbrains.annotations.NotNull;
import org.kohsuke.stapler.idea.language.JellyFileType;
import org.kohsuke.stapler.idea.language.JellyLanguage;
import org.kohsuke.stapler.idea.psi.JellyFile;
import org.kohsuke.stapler.idea.psi.JellyPsi;

import java.util.Iterator;

/**
 * @author Kohsuke Kawaguchi
 */
public class JellyFileImpl extends LightPsiFileBase implements JellyFile {
    private JellyPsiImpl[] children;

    public JellyFileImpl(FileViewProvider viewProvider) {
        super(viewProvider, JellyLanguage.INSTANCE);
    }

    public JellyFile getContainingFile() {
        return this;
    }

    @NotNull
    public FileType getFileType() {
        return JellyFileType.INSTANCE;
    }

    public XmlFile getSourceElement() {
        return (XmlFile)getViewProvider().getPsi(StdLanguages.XML);
    }

    public JellyPsi getJellyParent() {
        return null;
    }

    public void clearCaches() {
        synchronized (PsiLock.LOCK) {
            children = null;
            incModificationCount();
        }
    }

    // this method isn't really invoked during the edit, either.
    public void subtreeChanged() {
        super.subtreeChanged();
    }

    @NotNull
    public JellyPsiImpl[] getChildren() {
        synchronized (PsiLock.LOCK) {
            if(children==null)
                children = JellyPsiImpl.wrap(this,getSourceElement().getDocument().getChildren());
            return children;
        }
    }

    public JellyPsiImpl getFirstChild() {
        return (JellyPsiImpl)super.getFirstChild();
    }

    public JellyPsiImpl getLastChild() {
        return (JellyPsiImpl)super.getLastChild();
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

    public LightPsiFileImpl copyLight(FileViewProvider fileviewprovider) {
        return new JellyFileImpl(fileviewprovider);
    }

    public synchronized void incModificationCount() {
        myModificationCount++;
    }

    public synchronized long getModificationCount() {
        return myModificationCount;
    }

    private long myModificationCount=0;
}
