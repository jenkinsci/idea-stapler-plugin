package org.kohsuke.stapler.idea;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;

public class AddNamespaceQuickFix implements IntentionAction {

    private final String prefix;
    private final String namespace;

    public AddNamespaceQuickFix(String prefix, String namespace) {
        this.prefix = prefix;
        this.namespace = namespace;
    }

    @NotNull
    @Override
    public String getText() {
        return String.format("Add namespace: xmlns:%s=\"%s\"", prefix, namespace);
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return "Add XML namespace";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        if (!(file instanceof XmlFile)) return false;

        XmlTag rootTag = ((XmlFile) file).getRootTag();
        return rootTag != null && rootTag.getAttribute("xmlns:" + prefix) == null;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) {
        if (!(file instanceof XmlFile)) return;

        XmlTag rootTag = ((XmlFile) file).getRootTag();
        if (rootTag != null) {
            rootTag.setAttribute("xmlns:" + prefix, namespace);
        }
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}