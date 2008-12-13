package org.kohsuke.stapler.idea.language;

import com.intellij.lang.LanguageExtension;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;

/**
 * This defines when {@link JellyLanguage a custom vocabulary for XML} should kick in.
 *
 * @author Kohsuke Kawaguchi
 */
public final class JellyLanguageExtension extends LanguageExtension {
    public static final JellyLanguageExtension INSTANCE = new JellyLanguageExtension();
    
    private JellyLanguageExtension() {
        super(JellyLanguageExtension.class.getName());
    }

    public boolean isRelevantForFile(PsiFile psi) {
        if (psi instanceof XmlFile) {
            XmlFile xf = (XmlFile) psi;
            return xf.getName().endsWith(".jelly");
        }

        return false;
    }

    public JellyLanguage getLanguage() {
        return JellyLanguage.INSTANCE;
    }
}
