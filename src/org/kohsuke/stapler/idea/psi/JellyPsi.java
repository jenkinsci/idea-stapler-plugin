package org.kohsuke.stapler.idea.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiInvalidElementAccessException;
import com.intellij.psi.xml.XmlElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kohsuke.stapler.idea.language.JellyLanguage;

/**
 * Base-type for Jelly PSI interfaces.
 *
 * @author Kohsuke Kawaguchi
 */
public interface JellyPsi extends PsiElement {
    /**
     * Gets the XML PSI element that backs this up.
     *
     * A Jelly PSI always has a corresponding XML PSI,
     * although the other way around is not always true.
     *
     * @see JellyLanguage#getJellyFile(PsiElement) 
     */
    @NotNull XmlElement getSourceElement();

    /**
     * Works like {@link #getParent()} but only returns
     * a value if the parent is a sub-type of {@link JellyPsi}.
     */
    @Nullable JellyPsi getJellyParent();

    JellyPsi findElementAt(int offset);

    JellyPsi getFirstChild();
    
    JellyPsi getLastChild();

    JellyFile getContainingFile() throws PsiInvalidElementAccessException;

    /**
     * Detaches all the child PSIs. Used internally whenever a change
     * is made to the underlying file to invalidate a part of the PSI. 
     */
    void clearCaches();

    public static final JellyPsi EMPTY_ARRAY[] = new JellyPsi[0];
}
