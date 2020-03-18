package org.kohsuke.stapler.idea;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.xml.XmlText;

/**
 * @author Kohsuke Kawaguchi
 */
public class I18nInspection extends LocalXmlInspectionTool {
    @Override
    protected ProblemDescriptor[] checkXmlText(XmlText text, InspectionManager manager, boolean onTheFly) {
        if(text.getText().equals("foo")) {
            return new ProblemDescriptor[] {
                manager.createProblemDescriptor(text,"Can't be foo", LocalQuickFix.EMPTY_ARRAY,
                ProblemHighlightType.GENERIC_ERROR_OR_WARNING )
            };
        }
        return EMPTY_ARRAY;
    }
}
