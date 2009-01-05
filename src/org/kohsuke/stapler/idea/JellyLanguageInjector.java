package org.kohsuke.stapler.idea;

import com.intellij.lang.Language;
import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * Injects CSS and JavaScript to suitable places
 *
 * @author Kohsuke Kawaguchi
 */
public class JellyLanguageInjector implements MultiHostInjector {
    public void getLanguagesToInject(@NotNull MultiHostRegistrar registrar, @NotNull PsiElement context) {
        final XmlAttributeValue value = (XmlAttributeValue)context;

        if(!value.getContainingFile().getName().endsWith(".jelly"))
            return; // not a jelly file

        XmlAttribute a = (XmlAttribute) value.getParent();
        if(!a.getName().equals("style"))
            return; // not a style attribute

        final Language language = findLanguage("CSS");
        if (language == null) return;

        registrar.startInjecting(language);
        registrar.addPlace("dummy_selector {","}",
                (PsiLanguageInjectionHost)value,
                TextRange.from(1, value.getTextLength() - 2));
        registrar.doneInjecting();

    }

    private Language findLanguage(String id) {
        for( Language l : Language.getRegisteredLanguages())
            if(l.getID().equals(id))
                return l;
        return null;
    }

    @NotNull
    public List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
        return Collections.singletonList(XmlAttributeValue.class);
    }
}
