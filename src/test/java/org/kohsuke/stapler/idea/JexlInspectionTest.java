package org.kohsuke.stapler.idea;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.facet.FacetManager;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.intellij.testFramework.fixtures.DefaultLightProjectDescriptor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class JexlInspectionTest extends BasePlatformTestCase {
    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

    public void testSmokeJexlInspection() {
        myFixture.configureByFile(getTestName(true) + ".jelly");
        myFixture.enableInspections(new JexlInspection());
        List<HighlightInfo> highlightInfos = myFixture.doHighlighting(HighlightSeverity.WEAK_WARNING);
        assertEmpty(highlightInfos);
    }

    public void testParserFailureJexlInspection() {
        myFixture.configureByFile(getTestName(true) + ".jelly");
        myFixture.enableInspections(new JexlInspection());
        List<HighlightInfo> highlightInfos = myFixture.doHighlighting(HighlightSeverity.WEAK_WARNING);
        assertNotEmpty(highlightInfos);
        assertEquals("Two invalid JEXL expressions are used", 2, highlightInfos.size());
    }
}