package org.kohsuke.stapler.idea;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.xml.XmlText;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.InspectionManager;

/**
 * {@link LocalInspectionTool} with enhancements to handle XML files.
 * 
 * @author Kohsuke Kawaguchi
 */
public abstract class LocalXmlInspectionTool extends LocalInspectionTool {
    @NonNls
    @NotNull
    public String getShortName() {
        return getClass().getSimpleName();
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }

    @NotNull
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
        return new PsiElementVisitor() {
            public void visitXmlText(XmlText text) {
                addDescriptors(checkXmlText(text, holder.getManager(), isOnTheFly));
            }

            public void visitReferenceExpression(PsiReferenceExpression expression) {
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


    protected static final ProblemDescriptor[] EMPTY_ARRAY = new ProblemDescriptor[0];
}
