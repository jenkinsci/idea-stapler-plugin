package io.jenkins.stapler.idea.jelly;

import com.intellij.patterns.XmlPatterns;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;

public class JellyReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(XmlPatterns.xmlAttributeValue(), new JellyJavaReferenceProvider());
    }
}
