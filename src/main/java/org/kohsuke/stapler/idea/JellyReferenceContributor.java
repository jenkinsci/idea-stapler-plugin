package org.kohsuke.stapler.idea;

import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.psi.xml.XmlAttributeValue;

/** @author Kohsuke Kawaguchi */
public class JellyReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(PsiReferenceRegistrar registrar) {
        JellyTagLibReferenceProvider p = new JellyTagLibReferenceProvider();
        registrar.registerReferenceProvider(StandardPatterns.instanceOf(XmlAttributeValue.class), p);
    }
}
