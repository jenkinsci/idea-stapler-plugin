package io.jenkins.stapler.idea.jelly;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import java.util.List;

public class JenkinsDeprecatedReflectiveAccessInspectionTest extends BasePlatformTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.enableInspections(new JenkinsDeprecatedReflectiveAccessInspection());
    }

    public void testReflectiveAccessWarning() {
        myFixture.configureByText(
                "ExtensionPoint.java",
                """
                package org.kohsuke.stapler;

                public @interface DataBoundSetter {}
                """);

        myFixture.configureByText(
                "Foo.java",
                """
                public class Foo {
                    @org.kohsuke.stapler.DataBoundSetter
                    private String foo1;
                    @org.kohsuke.stapler.DataBoundSetter
                    protected String foo2;
                    @org.kohsuke.stapler.DataBoundSetter
                    public String foo3;
                }
                """);
        List<HighlightInfo> highlightInfos = myFixture.doHighlighting(HighlightSeverity.WARNING);
        assertEquals(5, highlightInfos.size());
        assertEquals(
                "Annotating field 'foo1' with @DataBoundSetter relies on deprecated behavior that will be disallowed in future releases of Java.",
                highlightInfos.get(0).getDescription());
        assertEquals(
                "Annotating field 'foo2' with @DataBoundSetter relies on deprecated behavior that will be disallowed in future releases of Java.",
                highlightInfos.get(2).getDescription());
    }
}
