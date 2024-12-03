package io.jenkins.stapler.idea.jelly;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.XmlElementVisitor;
import com.intellij.psi.xml.XmlAttribute;
import org.jetbrains.annotations.NotNull;
import org.kohsuke.stapler.idea.descriptor.StaplerCustomJellyTagfileXmlAttributeDescriptor;

public class DeprecatedStaplerJellyCustomTagAttributeInspection extends LocalInspectionTool {
    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new XmlElementVisitor() {
            @Override
            public void visitXmlAttribute(@NotNull XmlAttribute attribute) {
                if (attribute.getDescriptor() instanceof StaplerCustomJellyTagfileXmlAttributeDescriptor descriptor
                        && descriptor.getModel().isDeprecated()) {
                    holder.registerProblem(
                            attribute,
                            String.format(
                                    "Attribute '%s' is deprecated. Use \"Go to declaration\" to find the recommended solution.",
                                    attribute.getName()));
                }
            }
        };
    }
}
