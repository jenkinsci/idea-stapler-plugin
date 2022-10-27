package org.kohsuke.stapler.idea.language;

import com.intellij.lang.xml.XMLParserDefinition;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import org.kohsuke.stapler.idea.psi.JellyFileImpl;

/**
 * Jelly Parser Definition to associate proper types
 *
 * @author Julien Greffe
 */
public class JellyParserDefinition extends XMLParserDefinition {

    public static final IFileElementType JELLY_FILE = new IFileElementType(JellyLanguage.INSTANCE);

    @Override
    public IFileElementType getFileNodeType() {
        return JELLY_FILE;
    }

    @Override
    public PsiFile createFile(FileViewProvider viewProvider) {
        return new JellyFileImpl(viewProvider, JELLY_FILE);
    }
}
