package org.kohsuke.stapler.idea;

import com.intellij.codeInsight.intention.IntentionAction;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;

public class AddNamespaceQuickFix implements IntentionAction {

    @NotNull
    @Override
    public String getText() {
        return "Import namespace /lib/layout";
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return "Add XML namespace";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        if (!(file instanceof XmlFile)) return false;

        // Get the current XML tag under the cursor
        XmlTag tag = getTagAtCaret(editor, file);
        return tag != null && "l".equals(tag.getNamespacePrefix()) && !isNamespaceDeclared(tag);
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) {
        if (!(file instanceof XmlFile)) return;

        // Add the namespace to the root element
        XmlTag rootTag = ((XmlFile) file).getRootTag();
        if (rootTag != null) {
            rootTag.setAttribute("xmlns:l", "/lib/layout");
        }
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }

    private XmlTag getTagAtCaret(Editor editor, PsiFile file) {
        int offset = editor.getCaretModel().getOffset();
        PsiElement elementAtCaret = file.findElementAt(offset);

        // Traverse up the PSI tree to find the enclosing XmlTag
        while (elementAtCaret != null && !(elementAtCaret instanceof XmlTag)) {
            elementAtCaret = elementAtCaret.getParent();
        }
        return (XmlTag) elementAtCaret;
    }

    private boolean isNamespaceDeclared(XmlTag tag) {
        return "/lib/layout".equals(tag.getNamespace());
    }
}
