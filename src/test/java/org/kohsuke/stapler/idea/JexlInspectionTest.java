package org.kohsuke.stapler.idea;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.facet.FacetManager;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.DefaultLightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class JexlInspectionTest extends LightPlatformCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return new DefaultLightProjectDescriptor() {
            @Override
            public void configureModule(@NotNull Module module, @NotNull ModifiableRootModel model, @NotNull ContentEntry contentEntry) {
                FacetManager.getInstance(module).addFacet(StaplerFacetType.INSTANCE, StaplerFacetType.INSTANCE.getDefaultFacetName(), null);
            }
        };
    }

    public void testSmokeJexlInspection() {
        myFixture.configureByFile(getTestName(true) + ".jelly");
        myFixture.enableInspections(new JexlInspection());
        List<HighlightInfo> highlightInfos = myFixture.doHighlighting(HighlightSeverity.WEAK_WARNING);
        assertEmpty(highlightInfos);
    }
}