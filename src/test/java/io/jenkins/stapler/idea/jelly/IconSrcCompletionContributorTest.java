package io.jenkins.stapler.idea.jelly;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;

public class IconSrcCompletionContributorTest extends BasePlatformTestCase {

    public void testDefaultTagLibrary() {
        assertDefaultTagLibrary("""
                <l:icon src="symbol-<caret>
                """, 111);
    }

    private void assertDefaultTagLibrary(String body, int amount) {
        myFixture.configureByText("basic.jelly", body);

        myFixture.completeBasic();

        assertEquals(amount, myFixture.getLookupElementStrings().size());
    }
}
