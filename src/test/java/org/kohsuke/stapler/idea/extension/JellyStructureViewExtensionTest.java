package org.kohsuke.stapler.idea.extension;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;

import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.junit.Before;
import org.junit.Test;
import org.kohsuke.stapler.idea.mock.PsiElementMockFactory;
import org.kohsuke.stapler.idea.mock.PsiElementMockWrapper;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Test of JellyStructureViewExtension
 *
 * @author Julien Greffe
 */
public class JellyStructureViewExtensionTest {

    @Mock
    PsiManager psiManager;

    JellyStructureViewExtension jellyStructureViewExtension = new JellyStructureViewExtension();

    @Before
    public void before() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void test_none() {
        String path = "/this/is/a/test/leading/nowhere";
        PsiElementMockFactory factory = new PsiElementMockFactory(path);

        try (MockedStatic<VfsUtil> vfsUtil = Mockito.mockStatic(VfsUtil.class)) {
            for (PsiElementMockWrapper<? extends PsiElement> mock : factory.getMocks()) {
                vfsUtil.when(() ->
                                 VfsUtil.findFile(mock.getPath(), false)).thenReturn(mock.getVirtualFile());
                if (mock.getVirtualFile() != null && mock.getType() == PsiFile.class) {
                    when(psiManager.findFile(mock.getVirtualFile())).thenReturn((PsiFile) mock.getPsiElement());
                }
            }

            Optional<PsiClass> result = jellyStructureViewExtension
                .findParentMatchingJavaFile(psiManager,
                                            Paths.get(path),
                                            new ArrayList<>());
            assertFalse(result.isPresent());
        }
    }

    @Test
    public void test_simpleClass() {
        String path = "this/is/a/test/SomeClass";

        PsiElementMockFactory factory = new PsiElementMockFactory(path);

        try (MockedStatic<VfsUtil> vfsUtil = Mockito.mockStatic(VfsUtil.class)) {
            for (PsiElementMockWrapper<? extends PsiElement> mock : factory.getMocks()) {
                vfsUtil.when(() ->
                                 VfsUtil.findFile(mock.getPath(), false)).thenReturn(mock.getVirtualFile());
                if (mock.getVirtualFile() != null && mock.getType() == PsiFile.class) {
                    when(psiManager.findFile(mock.getVirtualFile())).thenReturn((PsiFile) mock.getPsiElement());
                }
            }

            Optional<PsiClass> result = jellyStructureViewExtension
                .findParentMatchingJavaFile(psiManager,
                                            Paths.get(path),
                                            new ArrayList<>());
            assertTrue(result.isPresent());
            assertEquals("MockPsiClass:SomeClass", result.get().toString());
        }
    }

    @Test
    public void test_innerClass() {
        String path = "this/is/a/test/With/Complete/Nested/InnerClass";

        PsiElementMockFactory factory = new PsiElementMockFactory(path);

        try (MockedStatic<VfsUtil> vfsUtil = Mockito.mockStatic(VfsUtil.class)) {
            for (PsiElementMockWrapper<? extends PsiElement> mock : factory.getMocks()) {
                vfsUtil.when(() ->
                                 VfsUtil.findFile(mock.getPath(), false)).thenReturn(mock.getVirtualFile());
                if (mock.getVirtualFile() != null && mock.getType() == PsiFile.class) {
                    when(psiManager.findFile(mock.getVirtualFile())).thenReturn((PsiFile) mock.getPsiElement());
                }
            }

            Optional<PsiClass> result = jellyStructureViewExtension
                .findParentMatchingJavaFile(psiManager,
                                            Paths.get(path),
                                            new ArrayList<>());
            assertTrue(result.isPresent());
            assertEquals("MockPsiClass:InnerClass", result.get().toString());
        }
    }
}
