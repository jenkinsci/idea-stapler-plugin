package org.kohsuke.stapler.idea;

import com.intellij.lang.properties.IProperty;
import com.intellij.lang.properties.psi.PropertiesElementFactory;
import com.intellij.lang.properties.psi.PropertiesFile;
import com.intellij.lang.properties.psi.Property;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.PsiBinaryExpression;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.PsiType;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Internationalize the current selected text.
 *
 * @author Kohsuke Kawaguchi
 */
public class I18nRefactorAction extends EditorAction {
    public I18nRefactorAction() {
        super(new EditorActionHandler() {
            public void execute(final Editor editor, DataContext dataContext) {
                if (editor == null) // be defensive
                    return;

                SelectionModel selectionModel = editor.getSelectionModel();

//                String selectedText = selectionModel.getSelectedText();
//                Messages.showInfoMessage(selectedText, "selection");

                final Project project = editor.getProject();
                if(project==null)
                    return;

                PsiDocumentManager psiManager = PsiDocumentManager.getInstance(project);
                PsiFile psiFile = psiManager.getPsiFile(editor.getDocument());
                if (!(psiFile instanceof PsiJavaFile)) {
                    return; // not a Java source file
                }

                // look for Messages.properties
                PsiJavaFile javaFile = (PsiJavaFile) psiFile;
                PsiFile props = findMessagesDotProperties(project, javaFile);
                if(props==null) {
                    Messages.showErrorDialog("Can't find Messages.properties","stapler i18n");
                    return;
                }
                if(!(props instanceof PropertiesFile)) {
                    Messages.showErrorDialog("Messages.properties is not a property file","stapler i18n");
                    return;
                }
                final PropertiesFile propsFile = (PropertiesFile) props;

                // find the expression currently selected
                PsiElement e = findSelectedPsiElement(selectionModel, javaFile);
                while(e!=null) {
                    if(e instanceof PsiExpression)
                        break;
                    e = e.getParent();
                }
                if(e==null) {
                    Messages.showErrorDialog("An expression needs to be selected","stapler i18n");
                    return;
                }

                final PsiExpression exp = (PsiExpression) e;
                final PsiClassType stringType = PsiType.getJavaLangString(PsiManager.getInstance(project), e.getResolveScope());
                if(exp.getType()==null || !exp.getType().equals(stringType)) {
                    Messages.showErrorDialog("A string expression needs to be selected","stapler i18n");
                    return;
                }

                final String key = getResourceName(javaFile);
                if(key==null)   return; // cancelled

                // property value
                final StringBuilder propertyValue = new StringBuilder();
                // expression to refer to string value
                final StringBuilder expression = new StringBuilder();
                new Runnable() {
                    int numArgs = 0;

                    public void run() {
                        process(exp);
                    }

                    private void process(PsiElement exp) {
                        if (exp instanceof PsiLiteralExpression) {
                            PsiLiteralExpression lit = (PsiLiteralExpression) exp;
                            if(lit.getType().equals(stringType)) {
                                escapeAndAppend(lit.getValue().toString());
                                return;
                            }
                        }

                        if (exp instanceof PsiBinaryExpression) {
                            PsiBinaryExpression binExp = (PsiBinaryExpression) exp;
                            if(binExp.getOperationTokenType()== JavaTokenType.PLUS) {
                                process(binExp.getLOperand());
                                process(binExp.getROperand());
                                return;
                            }
                        }

                        propertyValue.append('{').append(numArgs++).append('}');
                        if(expression.length()>0)
                            expression.append(',');
                        expression.append(exp.getText());
                    }

                    /**
                     * Takes the literal string value and appends that to <tt>propertyValue</tt>
                     * with proper escaping.
                     */
                    private void escapeAndAppend(String value) {
                        for (char ch : value.toCharArray()) {
                            switch (ch) {
                            case '\'':
                                propertyValue.append("''");
                                break;
                            case '{':
                            case '}':
                                propertyValue.append('\'').append(ch).append('\'');
                                break;
                            case ' ':
                                if(propertyValue.length()==0)
                                    propertyValue.append('\\');
                                // fall through
                            default:
                                propertyValue.append(ch);
                            }
                        }
                    }
                }.run();

                // IDEA bombed out saying I need to wrap the addProperty into this.
                ApplicationManager.getApplication().runWriteAction(new Runnable() {
                    public void run() {
                        try {
                            propsFile.addPropertyAfter(
                                (Property)PropertiesElementFactory.createProperty(project,key,propertyValue.toString()),
                                (Property)findAnchor(propsFile,key));
                        } catch (IncorrectOperationException x) {
                            Messages.showErrorDialog(x.getMessage(),"Unable to add property");
                            return;
                        }

                        // wrap the arguments into "Messages.KEY(...)"
                        expression.insert(0,"Messages."+toJavaIdentifier(key)+"(");
                        expression.append(")");

                        TextRange tr = exp.getTextRange();
                        editor.getDocument().deleteString(tr.getStartOffset(),tr.getEndOffset());
                        EditorModificationUtil.insertStringAtCaret(editor,expression.toString());
                    }

                    private IProperty findAnchor(PropertiesFile propsFile, String key) {
                        List<IProperty> list = propsFile.getProperties();
                        for(int i=0; i<list.size()-1; i++) {
                            IProperty prev = list.get(i);
                            IProperty next = list.get(i+1);
                            if(prev.getKey().compareTo(key)<0 && key.compareTo(next.getKey())<0)
                                return prev;
                        }
                        // pick up the last
                        if(list.isEmpty())  return null;
                        return list.get(list.size()-1);
                    }
                });
            }

            private String getResourceName(PsiJavaFile javaFile) {
                String key = Messages.showInputDialog("Message resource name?", "stapler 18n", null);
                if(key==null || key.length()==0)    return null; // cancelled
                return getMainClassName(javaFile.getName())+'.'+key;
            }

            private String getMainClassName(String name) {
                if(name.endsWith(".java"))
                    return name.substring(0,name.length()-5);
                return name;
            }

            /**
             * Locates <tt>Messages.properties</tt> in the same package
             */
            private PsiFile findMessagesDotProperties(Project project, PsiJavaFile javaFile) {
                PsiPackage pkg = JavaPsiFacade.getInstance(project).findPackage(javaFile.getPackageName());
                for(PsiDirectory dir : pkg.getDirectories()) {
                    PsiFile props = dir.findFile("Messages.properties");
                    if(props!=null) return props;
                }
                return null;
            }

            /**
             * Finds the smallest {@link PsiElement} that encompasses the current selection.
             */
            @Nullable
            private PsiElement findSelectedPsiElement(SelectionModel selectionModel, PsiJavaFile javaFile) {
                PsiElement e = javaFile.findElementAt(selectionModel.getSelectionStart());
                if(e==null) return null;

                while(true) {
                    TextRange tr = e.getTextRange();
                    if(selectionModel.getSelectionEnd()<=tr.getEndOffset())
                        return e;
                    e = e.getParent();
                }
            }

            /**
             * Copied from the localizer code.
             * Converts a property name to a method name.
             */
            protected String toJavaIdentifier(String key) {
                // TODO: this is fairly dumb implementation
                return key.replace('.','_');
            }
        });
    }
}
