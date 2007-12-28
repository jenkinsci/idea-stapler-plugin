package org.kohsuke.stapler.idea.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiInvalidElementAccessException;
import com.intellij.psi.xml.XmlElement;

/**
 * @author Kohsuke Kawaguchi
 */
public interface JellyPsi extends PsiElement {
    XmlElement getSourceElement();

    JellyPsi getJellyParent();

    JellyPsi findElementAt(int offset);

    JellyPsi getFirstChild();
    
    JellyPsi getLastChild();

    JellyFile getContainingFile() throws PsiInvalidElementAccessException;

    public static final JellyPsi EMPTY_ARRAY[] = new JellyPsi[0];

    void clearCaches();
}
