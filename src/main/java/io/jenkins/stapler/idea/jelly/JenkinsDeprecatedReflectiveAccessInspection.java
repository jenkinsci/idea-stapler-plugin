package io.jenkins.stapler.idea.jelly;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiModifier;
import org.jetbrains.annotations.NotNull;

/**
 * Deprecated reflective access
 *
 * <p>Before Jenkins 2.272 Stapler would access all fields ignoring visibility restrictions.
 *
 * <p>Stapler now tries to access a field first and only if it gets access denied then will it force access via
 * setAccessible(true).
 *
 * <p>This behavior is not recommended anymore, and will trigger IllegalReflectiveAccess warnings when running Jenkins
 * on Java 11 or newer.
 *
 * <p>If you see a warning message about this then you should report an issue to the respective plugin.
 *
 * <p>
 *
 * <pre>
 * Dec 15, 2020 9:22:33 PM org.kohsuke.stapler.lang.FieldRef$1 get
 * WARNING: java.lang.IllegalAccessException: Processing this request relies on deprecated behavior that will be disallowed in future releases of Java. See <a href="https://jenkins.io/redirect/stapler-reflective-access/">Deprecated reflective access</a> for more information. Details: class org.kohsuke.stapler.lang.FieldRef$1 cannot access a member of class org.kohsuke.stapler.AncestorImplTest$Foo with modifiers "public"
 * </pre>
 *
 * <p><a href="https://www.jenkins.io/doc/developer/handling-requests/deprecated-reflective-access/">Deprecated
 * reflective access</a>
 */
public class JenkinsDeprecatedReflectiveAccessInspection extends AbstractBaseJavaLocalInspectionTool {

    private static final String DATA_BOUND_SETTER_ANNOTATION = "org.kohsuke.stapler.DataBoundSetter";

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {

            @Override
            public void visitField(@NotNull PsiField field) {
                super.visitField(field);
                if (!field.hasModifierProperty(PsiModifier.PUBLIC)) {
                    final var annotation = field.getAnnotation(DATA_BOUND_SETTER_ANNOTATION);
                    if (annotation != null) {
                        holder.registerProblem(
                                annotation,
                                String.format(
                                        "Annotating field '%s' with @DataBoundSetter relies on deprecated behavior that will be disallowed in future releases of Java.",
                                        field.getName()));
                    }
                }
            }
        };
    }
}
