package org.kohsuke.stapler.idea;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.Lookup;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

import java.util.Arrays;
import java.util.List;

public class JellyCompletionContributorTest extends BasePlatformTestCase {

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

    public void testDefaultTagLibrary() {
        // Simulate the default tag libraries in core
        myFixture.copyDirectoryToProject("lib", "lib");
        myFixture.configureByText("basic.jelly", """
            <?xml version="1.0" encoding="UTF-8"?>
            <?jelly escape-by-default='true'?>
            <j:jelly xmlns:j="jelly:core">
                <l:b<caret>
            </j:jelly>
        """);

        myFixture.completeBasic();

        myFixture.checkResult("""
            <?xml version="1.0" encoding="UTF-8"?>
            <?jelly escape-by-default='true'?>
            <j:jelly xmlns:j="jelly:core" xmlns:l="/lib/layout">
                <l:basic  />
            </j:jelly>
        """);
    }

    public void testRequiredAttributes() {
        // Simulate the default tag libraries in core
        myFixture.copyDirectoryToProject("lib", "lib");
        myFixture.configureByText("basic.jelly", """
            <?xml version="1.0" encoding="UTF-8"?>
            <?jelly escape-by-default='true'?>
            <j:jelly xmlns:j="jelly:core">
                <l:req<caret>
            </j:jelly>
        """);

        var response = myFixture.completeBasic();

        myFixture.checkResult("""
            <?xml version="1.0" encoding="UTF-8"?>
            <?jelly escape-by-default='true'?>
            <j:jelly xmlns:j="jelly:core" xmlns:l="/lib/layout">
                <l:required title="" />
            </j:jelly>
        """);
    }

    public void testInvokeBody() {
        // Simulate the default tag libraries in core
        myFixture.copyDirectoryToProject("lib", "lib");
        myFixture.configureByText("basic.jelly", """
            <?xml version="1.0" encoding="UTF-8"?>
            <?jelly escape-by-default='true'?>
            <j:jelly xmlns:j="jelly:core">
                <l:child<caret>
            </j:jelly>
        """);

        myFixture.completeBasic();

        myFixture.checkResult("""
            <?xml version="1.0" encoding="UTF-8"?>
            <?jelly escape-by-default='true'?>
            <j:jelly xmlns:j="jelly:core" xmlns:l="/lib/layout">
                <l:children>
                 \s
                </l:children>
            </j:jelly>
        """);
    }

    public void testCustomTagLibrary() {
        myFixture.copyDirectoryToProject("testlib", "testlib");
        myFixture.configureByText("basic.jelly", """
            <?xml version="1.0" encoding="UTF-8"?>
                          <?jelly escape-by-default='true'?>
                          <j:jelly xmlns:j="jelly:core" xmlns:t="/testlib">
                              <t:<caret>
                          </j:jelly>
        """);

        myFixture.completeBasic();

        assertEquals(List.of("test"), myFixture.getLookupElementStrings());
    }
}