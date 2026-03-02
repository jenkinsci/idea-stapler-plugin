package io.jenkins.stapler.idea.jelly;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.util.PropertyUtilBase;
import com.intellij.psi.xml.XmlAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JellyToJavaClassReference extends PsiReferenceBase<XmlAttributeValue> {

    public JellyToJavaClassReference(@NotNull XmlAttributeValue element) {
        super(element, true);
    }

    @Override
    public @Nullable PsiElement resolve() {

        final var containingFile = getElement().getContainingFile();
        final var containingDirectory = containingFile.getContainingDirectory();

        String className = containingDirectory.getName();
        String methodName = getElement().getValue();
        Project project = getElement().getProject();

        PsiShortNamesCache cache = PsiShortNamesCache.getInstance(project);
        GlobalSearchScope scope = GlobalSearchScope.projectScope(project);

        String[] getterNames = PropertyUtilBase.suggestGetterNames(methodName);

        PsiClass[] psiClass = cache.getClassesByName(className, scope);
        for (var aClass : psiClass) {
            for (var getterName : getterNames) {
                for (PsiMethod method : aClass.findMethodsByName(getterName, true)) {
                    return method;
                }
            }
        }
        return null;
    }

    @Override
    public boolean isReferenceTo(@NotNull PsiElement element) {
        return super.isReferenceTo(element);
    }

    @Override
    public Object @NotNull [] getVariants() {
        return EMPTY_ARRAY;
    }
}
