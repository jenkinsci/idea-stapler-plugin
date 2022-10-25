package org.kohsuke.stapler.idea.language;

import com.intellij.lang.xml.XMLLanguage;

/**
 * Language definition for jelly
 *
 * @author Julien Greffe
 */
public class JellyLanguage extends XMLLanguage {

    public static final JellyLanguage INSTANCE = new JellyLanguage();

    private JellyLanguage() {
        super(XMLLanguage.INSTANCE, "Jelly");
    }
}
