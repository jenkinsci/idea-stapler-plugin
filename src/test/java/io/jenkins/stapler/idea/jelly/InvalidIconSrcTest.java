package io.jenkins.stapler.idea.jelly;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import java.util.List;

public class InvalidIconSrcTest extends BasePlatformTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.enableInspections(new InvalidIconSrcInspection());
    }

    public void testInvalidIconAttribute_srcAttribute() {
        createFile(
                """
            <?xml version="1.0" encoding="UTF-8"?>
            <?jelly escape-by-default='true'?>
            <j:jelly xmlns:j="jelly:core" xmlns:l="/lib/layout">
                <l:icon src="symbol-invalid" />
            </j:jelly>
            """);

        List<HighlightInfo> highlightInfos = myFixture.doHighlighting(HighlightSeverity.WARNING);
        assertEquals(
                "'symbol-invalid' isn't a valid symbol", highlightInfos.get(1).getDescription());
    }

    public void testInvalidIconAttribute_iconAttribute() {
        createFile(
                """
            <?xml version="1.0" encoding="UTF-8"?>
            <?jelly escape-by-default='true'?>
            <j:jelly xmlns:j="jelly:core" xmlns:l="/lib/layout">
                <l:notice icon="symbol-invalid" />
            </j:jelly>
            """);

        List<HighlightInfo> highlightInfos = myFixture.doHighlighting(HighlightSeverity.WARNING);
        assertEquals(
                "'symbol-invalid' isn't a valid symbol", highlightInfos.get(1).getDescription());
    }

    private void createFile(String body) {
        myFixture.configureByText("basic.jelly", body);
    }
}
