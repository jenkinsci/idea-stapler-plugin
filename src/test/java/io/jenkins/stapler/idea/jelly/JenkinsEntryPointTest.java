package io.jenkins.stapler.idea.jelly;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInspection.deadCode.UnusedDeclarationInspection;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import java.util.List;

public class JenkinsEntryPointTest extends BasePlatformTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.enableInspections(new UnusedDeclarationInspection(true));
    }

    public void testExtensionPointUnused() {
        myFixture.configureByText("Foo.java", """
                public class Foo {}
                """);
        List<HighlightInfo> highlightInfos = myFixture.doHighlighting(HighlightSeverity.WARNING);
        assertSize(1, highlightInfos);
        assertEquals("Class 'Foo' is never used", highlightInfos.get(0).getDescription());
    }

    public void testExtensionPointUsed() {
        myFixture.configureByText(
                "ExtensionPoint.java",
                """
                package hudson;
                public interface ExtensionPoint {}
                """);
        myFixture.configureByText(
                "Foo.java",
                """
                public class Foo implements hudson.ExtensionPoint {}
                """);
        List<HighlightInfo> highlightInfos = myFixture.doHighlighting(HighlightSeverity.WARNING);
        assertEmpty(highlightInfos);
    }

    public void testExtensionUnused() {
        myFixture.configureByText("Foo.java", """
                public class Foo {}
                """);
        List<HighlightInfo> highlightInfos = myFixture.doHighlighting(HighlightSeverity.WARNING);
        assertSize(1, highlightInfos);
        assertEquals("Class 'Foo' is never used", highlightInfos.get(0).getDescription());
    }

    public void testExtensionUsed() {
        myFixture.configureByText(
                "ExtensionPoint.java",
                """
                package hudson;

                public @interface Extension {}
                """);
        myFixture.configureByText(
                "Foo.java",
                """
                @hudson.Extension
                public class Foo {}
                """);
        List<HighlightInfo> highlightInfos = myFixture.doHighlighting(HighlightSeverity.WARNING);
        assertEmpty(highlightInfos);
    }

    public void testInitializerUnused() {
        myFixture.configureByText(
                "Foo.java",
                """
                public class Foo {
                    public static void init() {
                    }
                }
                """);
        List<HighlightInfo> highlightInfos = myFixture.doHighlighting(HighlightSeverity.WARNING);
        assertSize(2, highlightInfos);
        assertEquals("Method 'init()' is never used", highlightInfos.get(1).getDescription());
    }

    public void testInitializerUsed() {
        myFixture.configureByText(
                "ExtensionPoint.java",
                """
                package hudson.init;

                public @interface Initializer {}
                """);
        myFixture.configureByText(
                "Foo.java",
                """
                public class Foo {
                    @hudson.init.Initializer
                    public static void init() {
                    }
                }
                """);
        List<HighlightInfo> highlightInfos = myFixture.doHighlighting(HighlightSeverity.WARNING);
        assertEmpty(highlightInfos);
    }

    public void testTerminatorUnused() {
        myFixture.configureByText(
                "Foo.java",
                """
                public class Foo {
                    public static void shutdown() {
                    }
                }
                """);
        List<HighlightInfo> highlightInfos = myFixture.doHighlighting(HighlightSeverity.WARNING);
        assertSize(2, highlightInfos);
        assertEquals("Method 'shutdown()' is never used", highlightInfos.get(1).getDescription());
    }

    public void testTerminatorUsed() {
        myFixture.configureByText(
                "ExtensionPoint.java",
                """
                package hudson.init;

                public @interface Terminator {}
                """);
        myFixture.configureByText(
                "Foo.java",
                """
                public class Foo {
                    @hudson.init.Terminator
                    public static void shutdown() {
                    }
                }
                """);
        List<HighlightInfo> highlightInfos = myFixture.doHighlighting(HighlightSeverity.WARNING);
        assertEmpty(highlightInfos);
    }

    public void testExportedBeanUnused() {
        myFixture.configureByText("Foo.java", """
                public class Foo {}
                """);
        List<HighlightInfo> highlightInfos = myFixture.doHighlighting(HighlightSeverity.WARNING);
        assertSize(1, highlightInfos);
        assertEquals("Class 'Foo' is never used", highlightInfos.get(0).getDescription());
    }

    public void testExportedBeanUsed() {
        myFixture.configureByText(
                "ExtensionPoint.java",
                """
                package org.kohsuke.stapler.export;

                public @interface ExportedBean {}
                """);
        myFixture.configureByText(
                "Foo.java",
                """
                @org.kohsuke.stapler.export.ExportedBean
                public class Foo {}
                """);
        List<HighlightInfo> highlightInfos = myFixture.doHighlighting(HighlightSeverity.WARNING);
        assertEmpty(highlightInfos);
    }

    public void testExportedUnused() {
        myFixture.configureByText(
                "Foo.java",
                """
                public class Foo {
                    public int bar() {
                        return 42;
                    }
                }
                """);
        List<HighlightInfo> highlightInfos = myFixture.doHighlighting(HighlightSeverity.WARNING);
        assertSize(2, highlightInfos);
        assertEquals("Method 'bar()' is never used", highlightInfos.get(1).getDescription());
    }

    public void testExportedUsed() {
        myFixture.configureByText(
                "ExtensionPoint.java",
                """
                package org.kohsuke.stapler.export;

                public @interface Exported {}
                """);
        myFixture.configureByText(
                "Foo.java",
                """
                public class Foo {
                    @org.kohsuke.stapler.export.Exported
                    public int bar() {
                        return 42;
                    }
                }
                """);
        List<HighlightInfo> highlightInfos = myFixture.doHighlighting(HighlightSeverity.WARNING);
        assertEmpty(highlightInfos);
    }

    public void testDataBoundSetterUnused() {
        myFixture.configureByText(
                "Foo.java",
                """
                public class Foo {
                    public void setBar(int bar) {}
                }
                """);
        List<HighlightInfo> highlightInfos = myFixture.doHighlighting(HighlightSeverity.WARNING);
        assertSize(3, highlightInfos);
        assertEquals("Method 'setBar(int)' is never used", highlightInfos.get(1).getDescription());
    }

    public void testDataBoundSetterUsed() {
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
                    public void setBar(int bar) {}
                }
                """);
        List<HighlightInfo> highlightInfos = myFixture.doHighlighting(HighlightSeverity.WARNING);
        assertSize(1, highlightInfos);
    }

    public void testDataBoundConstructorUnused() {
        myFixture.configureByText(
                "Foo.java",
                """
                public class Foo {
                    public Foo(){}
                }
                """);
        List<HighlightInfo> highlightInfos = myFixture.doHighlighting(HighlightSeverity.WARNING);
        assertSize(2, highlightInfos);
        assertEquals("Constructor 'Foo()' is never used", highlightInfos.get(1).getDescription());
    }

    public void testDataBoundConstructorUsed() {
        myFixture.configureByText(
                "ExtensionPoint.java",
                """
                package org.kohsuke.stapler;

                public @interface DataBoundConstructor {}
                """);
        myFixture.configureByText(
                "Foo.java",
                """
                public class Foo {
                    @org.kohsuke.stapler.DataBoundConstructor
                    public Foo(){}
                }
                """);
        List<HighlightInfo> highlightInfos = myFixture.doHighlighting(HighlightSeverity.WARNING);
        assertEmpty(highlightInfos);
    }
}
