package org.kohsuke.stapler.idea;

import com.intellij.ide.actions.GotoActionBase;
import com.intellij.ide.util.DefaultPsiElementCellRenderer;
import com.intellij.ide.util.gotoByName.ChooseByNameModel;
import com.intellij.ide.util.gotoByName.ChooseByNamePopup;
import com.intellij.ide.util.gotoByName.ChooseByNamePopupComponent.Callback;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilBase;
import org.apache.commons.lang.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Select stapler views from a Java class.
 *
 * @author Kohsuke Kawaguchi
 */
public class GotoViewAction extends GotoActionBase {

    public void gotoActionPerformed(AnActionEvent anactionevent) {
        PsiElement context = getPsiContext(anactionevent);

        final Project project = anactionevent.getData(PlatformDataKeys.PROJECT);
        Editor editor = anactionevent.getData(PlatformDataKeys.EDITOR);
        if(editor == null || project == null)   return;

        PsiFile file = PsiUtilBase.getPsiFileInEditor(editor, project);
        if(!(file instanceof PsiJavaFile))  return; // not a Java file

        JavaPsiFacade facade = JavaPsiFacade.getInstance(project);

        // from which class are we invoked?
        PsiElement e = PsiUtilBase.getElementAtOffset(file, editor.getCaretModel().getOffset());
        PsiClass clazz = PsiTreeUtil.getParentOfType(e, PsiClass.class);

        // if we are invoked from inside anonymous class, go up the tree
        // until we find a named class.
        while(clazz!=null && clazz.getQualifiedName()==null)
            clazz = PsiTreeUtil.getParentOfType(clazz,PsiClass.class);

        // build up packages that contain jelly views, in the order of preference
        // through inheritance hierarchy of the class
        final List<PsiPackage> viewPackages = new ArrayList<PsiPackage>();
        while(clazz!=null) {
            PsiPackage pkg = facade.findPackage(clazz.getQualifiedName());
            if(pkg!=null)   viewPackages.add(pkg);
            clazz = clazz.getSuperClass();
        }

        if(viewPackages.isEmpty())  return; // no views

        ChooseByNameModel model = new ChooseByNameModel() {

            public String getPromptText() {
                return "Enter view name:";
            }

            public String getNotInMessage() {
                return "getNotInMessage";
            }

            public String getNotFoundMessage() {
                return "getNotFoundMessage";
            }

            public String getCheckBoxName() {
                return null;    // no check box
            }

            public char getCheckBoxMnemonic() {
                return '\0';
            }

            public boolean loadInitialCheckBoxState() {
                return false;
            }

            public void saveInitialCheckBoxState(boolean state) {
                // noop
            }

            public ListCellRenderer getListCellRenderer() {
                return new DefaultPsiElementCellRenderer();
            }

            public String[] getNames(boolean checkBoxState) {
                List<String> r = new ArrayList<String>();
                for (PsiPackage pkg : viewPackages) {
                    for (PsiDirectory dir : pkg.getDirectories()) {
                        for (PsiFile file : dir.getFiles()) {
                            String name = file.getName();
                            if(name.endsWith(".jelly") || name.endsWith(".groovy"))
                                r.add(name.substring(0,name.lastIndexOf('.')));
                        }
                    }
                }
                return r.toArray(new String[r.size()]);
            }

            public Object[] getElementsByName(String name, boolean checkBoxState, String pattern) {
                for (PsiPackage pkg : viewPackages) {
                    for (PsiDirectory dir : pkg.getDirectories()) {
                        for (PsiFile file : dir.getFiles()) {
                            if(file.getName().equals(name+".jelly") || file.getName().equals(name+".groovy"))
                                return new Object[]{file};
                        }
                    }
                }
                return EMPTY_ARRAY;
            }

            @NotNull
            public String[] getSeparators() {
                return ArrayUtils.EMPTY_STRING_ARRAY;
            }

            public String getElementName(Object obj) {
                if (obj instanceof PsiFile) {
                    return ((PsiFile) obj).getName();
                } else {
                    return null;
                }
            }

            public String getFullName(Object obj) {
                if (obj instanceof PsiFile) {
                    VirtualFile virtualfile = ((PsiFile) obj).getVirtualFile();
                    return virtualfile == null ? null : virtualfile.getPath();
                } else {
                    return getElementName(obj);
                }
            }

            public String getHelpId() {
                return null;
            }

            public boolean willOpenEditor() {
                return true;
            }
        };

        PsiDocumentManager.getInstance(project).commitAllDocuments();
        ChooseByNamePopup choosebynamepopup = ChooseByNamePopup.createPopup(project,
                model, context);
        choosebynamepopup.invoke(new Callback() {
            public void onClose() {
                if (GotoActionBase.myInAction==GotoViewAction.class)
                    GotoActionBase.myInAction = null;
            }

            public void elementChosen(Object obj) {
                ((NavigationItem) obj).navigate(true);
            }
        }, ModalityState.current(), true);
    }
}

