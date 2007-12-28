package org.kohsuke.stapler.idea.language;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lang.StdLanguages;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.kohsuke.stapler.idea.psi.impl.JellyFileImpl;

/**
 * Mostly just delegate to {@link ParserDefinition} of XML.
 *
 * @author Kohsuke Kawaguchi
 */
final class JellyParserDefinition implements ParserDefinition {
    public static final JellyParserDefinition INSTANCE = new JellyParserDefinition();

    private final ParserDefinition xml = StdLanguages.XML.getParserDefinition();

    private JellyParserDefinition() {
    }

    @NotNull
    public Lexer createLexer(Project project) {
        return xml.createLexer(project);
    }

    public PsiParser createParser(Project project) {
        return xml.createParser(project);
    }

    public IFileElementType getFileNodeType() {
        return xml.getFileNodeType();
    }

    @NotNull
    public TokenSet getWhitespaceTokens() {
        return xml.getWhitespaceTokens();
    }

    @NotNull
    public TokenSet getCommentTokens() {
        return xml.getCommentTokens();
    }

    @NotNull
    public PsiElement createElement(ASTNode node) {
        return xml.createElement(node);
    }

    public PsiFile createFile(FileViewProvider viewProvider) {
        return new JellyFileImpl(viewProvider);
    }

    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY;
    }
}
