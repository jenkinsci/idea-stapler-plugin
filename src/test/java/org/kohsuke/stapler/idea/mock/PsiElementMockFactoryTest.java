package org.kohsuke.stapler.idea.mock;

import java.nio.file.Paths;
import java.util.List;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Julien Greffe
 */
public class PsiElementMockFactoryTest {

    @Test
    public void smokes_PsiClassMockFactory_noClass() {
        PsiElementMockFactory factory = new PsiElementMockFactory("/this/is/a/test");
        assertEquals(0, factory.getMocks().size());
    }

    @Test
    public void smokes_PsiClassMockFactory_simpleClass() {
        PsiElementMockFactory factory = new PsiElementMockFactory("/this/is/a/test/MyClass");
        List<PsiElementMockWrapper<? extends PsiElement>> mocks = factory.getMocks();
        assertEquals(2, factory.getMocks().size());
        assertEquals(Paths.get("/this/is/a/test/MyClass.java"), factory.getPsiElementRoot().getPath());
        assertEquals(PsiFile.class, factory.getPsiElementRoot().getType());
        assertEquals(1, factory.getPsiElementRoot().getChildren().size());
        assertEquals(PsiClass.class, factory.getPsiElementRoot().getChildren().get(0).getType());
    }

    @Test
    public void smokes_PsiClassMockFactory_innerClass() {
        PsiElementMockFactory factory = new PsiElementMockFactory("/this/is/a/test/MyClass/With/Inner");
        List<PsiElementMockWrapper<? extends PsiElement>> mocks = factory.getMocks();
        assertEquals(4, factory.getMocks().size());
        assertEquals(Paths.get("/this/is/a/test/MyClass.java"), factory.getPsiElementRoot().getPath());
        assertEquals(PsiFile.class, factory.getPsiElementRoot().getType());
        assertNotNull(factory.getPsiElementRoot().getVirtualFile());

        // check structure
        assertEquals(1, factory.getPsiElementRoot().getChildren().size());
        PsiElementMockWrapper<? extends PsiElement> child1 = factory.getPsiElementRoot().getChildren().get(0);
        assertEquals(Paths.get("/this/is/a/test/MyClass"), child1.getPath());
        assertEquals(PsiClass.class, child1.getType());

        assertEquals(1, child1.getChildren().size());
        PsiElementMockWrapper<? extends PsiElement> child2 = child1.getChildren().get(0);
        assertEquals(Paths.get("/this/is/a/test/MyClass/With"), child2.getPath());
        assertEquals(PsiClass.class, child2.getType());

        assertEquals(1, child2.getChildren().size());
        PsiElementMockWrapper<? extends PsiElement> child3 = child2.getChildren().get(0);
        assertEquals(Paths.get("/this/is/a/test/MyClass/With/Inner"), child3.getPath());
        assertEquals(PsiClass.class, child3.getType());

        assertEquals(0, child3.getChildren().size());
    }
}
