package io.jenkins.stapler.idea.jelly;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;

public class IconSrcCompletionContributorTest extends BasePlatformTestCase {

    public void testDefaultTagLibrary_srcAttribute() {
        assertDefaultTagLibrary(
                """
            <?xml version="1.0" encoding="UTF-8"?>
            <?jelly escape-by-default='true'?>
            <j:jelly xmlns:j="jelly:core" xmlns:l="/lib/layout">
                <l:icon src="symbol-<caret>
            </j:jelly>
            """,
                111);
    }

    public void testDefaultTagLibrary_iconAttribute() {
        assertDefaultTagLibrary(
                """
            <?xml version="1.0" encoding="UTF-8"?>
            <?jelly escape-by-default='true'?>
            <j:jelly xmlns:j="jelly:core" xmlns:l="/lib/layout">
                <l:notice icon="symbol-<caret>
            </j:jelly>
            """,
                111);
    }

    private void assertDefaultTagLibrary(String body, int amount) {
        myFixture.configureByText("basic.jelly", body);

        myFixture.completeBasic();

        assertEquals(amount, myFixture.getLookupElementStrings().size());
    }
}
