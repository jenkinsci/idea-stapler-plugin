package org.kohsuke.stapler.idea.psi;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;

/**
 * Definition of a Jelly tag.
 * @author Kohsuke Kawaguchi
 */
public interface TagDefinition {
    /**
     * Either {@link PsiClass} representing the tag class, or a
     * {@link JellyFile} representing a Jelly tag file defining this tag. 
     */
    PsiElement resolveDefinition();
}
