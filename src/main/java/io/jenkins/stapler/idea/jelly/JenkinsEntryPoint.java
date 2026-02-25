package io.jenkins.stapler.idea.jelly;

import com.intellij.codeInspection.reference.EntryPoint;
import com.intellij.codeInspection.reference.RefElement;
import com.intellij.configurationStore.XmlSerializer;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Stream;
import org.jdom.Element;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class JenkinsEntryPoint extends EntryPoint {

    private static final String EXTENSION_POINT_INTERFACE = "hudson.ExtensionPoint";
    private static final String EXTENSION_ANNOTATION = "hudson.Extension";
    private static final String INITIALIZER_ANNOTATION = "hudson.init.Initializer";
    private static final String TERMINATOR_ANNOTATION = "hudson.init.Terminator";
    private static final String DATA_BOUND_CONSTRUCTOR = "org.kohsuke.stapler.DataBoundConstructor";
    private static final String DATA_BOUND_SETTER_ANNOTATION = "org.kohsuke.stapler.DataBoundSetter";
    private static final String EXPORTED_BEAN_ANNOTATION = "org.kohsuke.stapler.export.ExportedBean";
    private static final String EXPORTED_ANNOTATION = "org.kohsuke.stapler.export.Exported";

    private boolean selected = true;

    @Override
    public @NotNull @Nls String getDisplayName() {
        return "Jenkins";
    }

    @Override
    public boolean isEntryPoint(@NotNull RefElement refElement, @NotNull PsiElement psiElement) {
        return isEntryPoint(psiElement);
    }

    @Override
    public boolean isEntryPoint(@NotNull PsiElement psiElement) {
        if (selected) {
            Stream<Visitor> visitorStream = Stream.of(
                    new ExtensionPointVisitor(),
                    new ExtensionVisitor(),
                    new InitializerVisitor(),
                    new TerminatorVisitor(),
                    new ExportedBeanVisitor(),
                    new ExportedVisitor(),
                    new DataBoundSetterVisitor(),
                    new DataBoundConstructorVisitor());
            return visitorStream.anyMatch(visitor -> visitor.visit(psiElement));
        }
        return false;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public void readExternal(Element element) {
        XmlSerializer.deserializeInto(element, this);
    }

    @Override
    public void writeExternal(Element element) {
        XmlSerializer.serializeObjectInto(this, element);
    }

    interface Visitor {
        default boolean visit(PsiElement element) {
            if (element instanceof PsiClass psiClass) {
                return visit(psiClass);
            } else if (element instanceof PsiMethod psiMethod) {
                return visit(psiMethod);
            } else if (element instanceof PsiField psiField) {
                return visit(psiField);
            }
            return false;
        }

        default boolean visit(PsiClass aClass) {
            return false;
        }

        default boolean visit(PsiMethod method) {
            return false;
        }

        default boolean visit(PsiField field) {
            return false;
        }
    }

    static class ExtensionPointVisitor implements Visitor {
        @Override
        public boolean visit(PsiClass aClass) {
            return collectAllImplementsTypes(aClass).contains(EXTENSION_POINT_INTERFACE);
        }

        private Set<String> collectAllImplementsTypes(PsiClass psiClass) {
            Set<String> result = new LinkedHashSet<>();

            PsiClass current = psiClass;
            while (current != null) {
                for (var anInterface : current.getInterfaces()) {
                    result.add(anInterface.getQualifiedName());
                }
                current = current.getSuperClass();
            }

            return result;
        }
    }

    static class ExtensionVisitor implements Visitor {
        @Override
        public boolean visit(PsiClass aClass) {
            return aClass.hasAnnotation(EXTENSION_ANNOTATION);
        }

        @Override
        public boolean visit(PsiMethod method) {
            return method.hasAnnotation(EXTENSION_ANNOTATION);
        }

        @Override
        public boolean visit(PsiField field) {
            return field.hasAnnotation(EXTENSION_ANNOTATION);
        }
    }

    static class InitializerVisitor implements Visitor {
        @Override
        public boolean visit(PsiMethod method) {
            return method.hasAnnotation(INITIALIZER_ANNOTATION);
        }
    }

    static class TerminatorVisitor implements Visitor {
        @Override
        public boolean visit(PsiMethod method) {
            return method.hasAnnotation(TERMINATOR_ANNOTATION);
        }
    }

    static class ExportedBeanVisitor implements Visitor {
        @Override
        public boolean visit(PsiClass aClass) {
            return aClass.hasAnnotation(EXPORTED_BEAN_ANNOTATION);
        }
    }

    static class ExportedVisitor implements Visitor {
        @Override
        public boolean visit(PsiField field) {
            return field.hasAnnotation(EXPORTED_ANNOTATION);
        }

        @Override
        public boolean visit(PsiMethod method) {
            return method.hasAnnotation(EXPORTED_ANNOTATION);
        }
    }

    static class DataBoundSetterVisitor implements Visitor {
        @Override
        public boolean visit(PsiField field) {
            return field.hasAnnotation(DATA_BOUND_SETTER_ANNOTATION);
        }

        @Override
        public boolean visit(PsiMethod method) {
            return method.hasAnnotation(DATA_BOUND_SETTER_ANNOTATION);
        }
    }

    static class DataBoundConstructorVisitor implements Visitor {
        @Override
        public boolean visit(PsiMethod method) {
            if (method.isConstructor()) {
                return method.hasAnnotation(DATA_BOUND_CONSTRUCTOR);
            }
            return false;
        }
    }
}
