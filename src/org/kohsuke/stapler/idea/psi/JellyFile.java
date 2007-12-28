package org.kohsuke.stapler.idea.psi;

import com.intellij.openapi.util.ModificationTracker;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.xml.XmlFile;
import org.kohsuke.stapler.idea.language.JellyLanguage;

/**
 * @author Kohsuke Kawaguchi
 * @see JellyLanguage#getJellyFile(PsiFile)
 */
public interface JellyFile extends PsiFile, JellyPsi, PsiNamedElement, ModificationTracker
{
    XmlFile getSourceElement();

    void clearCaches();
}
