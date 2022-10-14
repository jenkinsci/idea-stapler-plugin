package org.kohsuke.stapler.idea.mock;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

/**
 * @author Julien Greffe
 */
public class PsiElementMockWrapperTest {

    /**
     * Check PsiTreeUtil mock behavior
     */
    @Test
    public void smokes_PsiTreeUtil() {
        PsiElementMockWrapper<PsiFile> wrapper = new PsiElementMockWrapper<>(PsiFile.class);
        wrapper.addChild(mock(PsiFile.class));
        wrapper.addChild(mock(PsiClass.class));
        wrapper.addChild(mock(PsiClass.class));
        wrapper.addChild(mock(PsiElement.class));
        wrapper.addChild(mock(PsiJavaFile.class));
        wrapper.addChild(mock(PsiClass.class));
        wrapper.stub();
        PsiClass child = PsiTreeUtil.getChildOfType(wrapper.getPsiElement(), PsiClass.class);
        assertNotNull(child);
        assertEquals(6, PsiTreeUtil.getChildrenOfAnyType(wrapper.getPsiElement(), PsiElement.class).size());
        assertEquals(3, PsiTreeUtil.getChildrenOfAnyType(wrapper.getPsiElement(), PsiClass.class).size());
        assertEquals(2, PsiTreeUtil.getChildrenOfAnyType(wrapper.getPsiElement(), PsiFile.class).size());
        assertEquals(1, PsiTreeUtil.getChildrenOfAnyType(wrapper.getPsiElement(), PsiJavaFile.class).size());
    }
}
