package org.kohsuke.stapler.idea;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.XmlElementVisitor;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlText;
import org.jetbrains.annotations.NotNull;

/**
 * {@link LocalInspectionTool} with enhancements to handle XML files.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class LocalXmlInspectionTool extends LocalInspectionTool {

    @Override
    @NotNull
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
        return new XmlElementVisitor() {
            @Override
            public void visitXmlText(XmlText text) {
                addDescriptors(checkXmlText(text, holder.getManager(), isOnTheFly));
            }

            @Override
            public void visitXmlAttributeValue(XmlAttributeValue value) {
                addDescriptors(checkXmlAttributeValue(value, holder.getManager(), isOnTheFly));
            }

            private void addDescriptors(final ProblemDescriptor[] descriptors) {
                for (ProblemDescriptor descriptor : descriptors) {
                    holder.registerProblem(descriptor);
                }
            }
        };
    }

    protected ProblemDescriptor[] checkXmlText(XmlText text, InspectionManager manager, boolean onTheFly) {
        return EMPTY_ARRAY;
    }

    protected ProblemDescriptor[] checkXmlAttributeValue(
            XmlAttributeValue text, InspectionManager manager, boolean onTheFly) {
        return EMPTY_ARRAY;
    }

    protected static final ProblemDescriptor[] EMPTY_ARRAY = new ProblemDescriptor[0];
}
