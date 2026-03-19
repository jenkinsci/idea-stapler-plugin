package io.jenkins.stapler.idea.jelly;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

public class JellyReferenceContributorTest extends BasePlatformTestCase {

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

    public void testReferenceAtCaret() {
        myFixture.configureByText(
                "Foo.java",
                """
                public class Foo {
                    public String getBar() {
                        return "";
                    }
                }
                """);

        myFixture.configureByFiles("Foo/config.jelly");

        PsiReference reference = myFixture.getReferenceAtCaretPosition();
        assertNotNull(reference);

        PsiElement resolved = reference.resolve();
        assertNotNull(resolved);
        if (resolved instanceof PsiMethod method) {
            assertEquals("getBar", method.getName());
        } else {
            fail("not a method");
        }
    }
}
