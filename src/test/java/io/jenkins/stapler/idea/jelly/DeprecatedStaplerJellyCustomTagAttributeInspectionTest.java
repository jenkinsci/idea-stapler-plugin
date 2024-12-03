package io.jenkins.stapler.idea.jelly;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import java.util.List;

public class DeprecatedStaplerJellyCustomTagAttributeInspectionTest extends BasePlatformTestCase {
    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.enableInspections(new DeprecatedStaplerJellyCustomTagAttributeInspection());
    }

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

    public void testDeprecateTagAttribute() {
        myFixture.copyDirectoryToProject("testlib", "testlib");
        myFixture.configureByFile(getTestName(true) + ".jelly");

        List<HighlightInfo> highlightInfos = myFixture.doHighlighting(HighlightSeverity.WARNING);
        assertNotEmpty(highlightInfos);
        assertEquals(
                "Attribute 'title' is deprecated. Use \"Go to declaration\" to find the recommended solution.",
                highlightInfos.get(0).getDescription());
    }
}
