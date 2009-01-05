package org.kohsuke.stapler.idea;

import com.intellij.lang.Language;
import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.XmlElementVisitor;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlText;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * Injects CSS and JavaScript to suitable places
 *
 * @author Kohsuke Kawaguchi
 */
public class JellyLanguageInjector implements MultiHostInjector {
    public void getLanguagesToInject(@NotNull final MultiHostRegistrar registrar, @NotNull PsiElement context) {
        if(!context.getContainingFile().getName().endsWith(".jelly"))
            return; // not a jelly file

        // inject CSS to @style
        if (context instanceof XmlAttributeValue) {
            final XmlAttributeValue value = (XmlAttributeValue)context;

            XmlAttribute a = (XmlAttribute) value.getParent();
            if(!a.getName().equals("style"))
                return; // not a style attribute

            Language language = findLanguage("CSS");
            if (language == null) return;

            registrar.startInjecting(language);
            registrar.addPlace("dummy_selector {","}",
                    (PsiLanguageInjectionHost)value,
                    TextRange.from(1, value.getTextLength() - 2));
            registrar.doneInjecting();
        }
        
        // inject JavaScript to <script>
        if (context instanceof XmlTag) {
            XmlTag t = (XmlTag) context;
            if(!t.getName().equals("script"))
                return; // not a script element

            Language language = findLanguage("JavaScript");
            if (language == null) return;

            registrar.startInjecting(language);
            t.acceptChildren(new XmlElementVisitor() {
                @Override
                public void visitXmlText(XmlText text) {
                    int len = text.getTextLength();
                    if(len ==0) return;
                    registrar.addPlace(null,null,
                            (PsiLanguageInjectionHost)text,
                            TextRange.from(0, len));
                }
            });
            registrar.doneInjecting();
        }
    }

    private Language findLanguage(String id) {
        for( Language l : Language.getRegisteredLanguages())
            if(l.getID().equals(id))
                return l;
        return null;
    }

    @NotNull
    public List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
        return INJECTION_TARGET;
    }

    private static final List<Class<? extends XmlElement>> INJECTION_TARGET = Arrays.asList(XmlAttributeValue.class, XmlTag.class);
}
