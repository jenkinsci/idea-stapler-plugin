package org.kohsuke.stapler.idea;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;

public class JellyCompletionContributorTest extends BasePlatformTestCase {

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

    public void testDefaultTagLibrary() {
        assertDefaultTagLibrary("""
            <?xml version="1.0" encoding="UTF-8"?>
            <?jelly escape-by-default='true'?>
            <j:jelly xmlns:j="jelly:core">
                <l:b<caret>
            </j:jelly>
        """, """
            <?xml version="1.0" encoding="UTF-8"?>
            <?jelly escape-by-default='true'?>
            <j:jelly xmlns:j="jelly:core" xmlns:l="/lib/layout">
                <l:basic />
            </j:jelly>
        """);
    }

    public void testRequiredAttributes() {
        assertDefaultTagLibrary("""
            <?xml version="1.0" encoding="UTF-8"?>
            <?jelly escape-by-default='true'?>
            <j:jelly xmlns:j="jelly:core">
                <l:req<caret>
            </j:jelly>
        """, """
            <?xml version="1.0" encoding="UTF-8"?>
            <?jelly escape-by-default='true'?>
            <j:jelly xmlns:j="jelly:core" xmlns:l="/lib/layout">
                <l:required title="" />
            </j:jelly>
        """);
    }

    public void testInvokeBody() {
        assertDefaultTagLibrary("""
            <?xml version="1.0" encoding="UTF-8"?>
            <?jelly escape-by-default='true'?>
            <j:jelly xmlns:j="jelly:core">
                <l:child<caret>
            </j:jelly>
        """, """
            <?xml version="1.0" encoding="UTF-8"?>
            <?jelly escape-by-default='true'?>
            <j:jelly xmlns:j="jelly:core" xmlns:l="/lib/layout">
                <l:children>
                 \s
                </l:children>
            </j:jelly>
        """);
    }

    private void assertDefaultTagLibrary(String body, String expected) {
        // Simulate the default tag libraries in core
        myFixture.copyDirectoryToProject("lib", "lib");
        myFixture.configureByText("basic.jelly", body);

        myFixture.completeBasic();

        myFixture.checkResult(expected);
    }
}