package org.kohsuke.stapler.idea;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

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

    public void testI18nLookups() {
        myFixture.configureByFile(getTestName(true) + ".jelly");
        myFixture.enableInspections(new JexlInspection());
        List<HighlightInfo> highlightInfos = myFixture.doHighlighting(HighlightSeverity.WEAK_WARNING);
        assertNotEmpty(highlightInfos);
        assertEquals("Expected error", 10, highlightInfos.size());
        assertEquals("Missing '}' character at the end of expression", highlightInfos.get(0).getDescription());
        assertTrue("Should match error", highlightInfos.get(1).getDescription().contains("Expecting \"(\" ..."));
        for (int i = 2; i < 10; i++) {
            assertEquals("Missing '}' character at the end of expression", highlightInfos.get(i).getDescription());
        }
    }

    public void testTokenize() {
        Map<String, String> expectations = new HashMap<>() {
            {
                put("${}", null);
                put("{}", null);
                put("()", null);
                put("[]", null);
                put(",", "");
                put("{,}", null);
                put("(,)", null);
                put("[,]", null);
                put("test)", "test");
                put("test,", "test");
                put("(test,)", null);
                put("\"", null);
                put("'", null);
                put("''", null);
                put("'test'", null);
                put("\"test\"", null);
                put("'test", null);
                put("\"test", null);
                put("test'", null);
                put("test\"", null);
            }
        };

        JexlInspection jexlInspection = new JexlInspection();

        for (Map.Entry<String, String> expectation : expectations.entrySet()) {
            assertEquals("Issue with text: " + expectation.getKey(), expectation.getValue(),
                         jexlInspection.tokenize(expectation.getKey()));
        }
    }
}