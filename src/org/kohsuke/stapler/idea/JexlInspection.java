package org.kohsuke.stapler.idea;

import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.xml.XmlText;
import org.apache.commons.jexl.ExpressionFactory;
import org.apache.commons.jexl.parser.ParseException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kohsuke Kawaguchi
 */
public class JexlInspection extends LocalXmlInspectionTool {
    @Nls @NotNull
    public String getGroupDisplayName() {
        return GroupNames.BUGS_GROUP_NAME;
    }

    @Nls @NotNull
    public String getDisplayName() {
        return "Checks syntax of JEXL expressions";
    }

    @NotNull
    public HighlightDisplayLevel getDefaultLevel() {
        return HighlightDisplayLevel.ERROR;
    }

    /*
     * Copyright 2002,2004 The Apache Software Foundation.
     *
     * Licensed under the Apache License, Version 2.0 (the "License");
     * you may not use this file except in compliance with the License.
     * You may obtain a copy of the License at
     *
     *      http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS,
     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     * See the License for the specific language governing permissions and
     * limitations under the License.
     */
    protected ProblemDescriptor[] checkXmlText(XmlText xmlText, InspectionManager manager, boolean onTheFly) {
        if(!xmlText.getContainingFile().getName().endsWith(".jelly"))
            return EMPTY_ARRAY; // not a jelly script

        String text = xmlText.getText();

        int len = text.length();

        int startIndex = text.indexOf( "${" );

        if ( startIndex < 0) {
            return EMPTY_ARRAY;
        }

        int endIndex = text.indexOf( "}", startIndex+2 );

        if ( endIndex < 0 )
            return new ProblemDescriptor[] {
                manager.createProblemDescriptor(xmlText,
                        createSubRange(startIndex,len),
                        "Missing '}' character at the end of expression: ",
                        ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                        LocalQuickFix.EMPTY_ARRAY)
            };

        if ( startIndex == 0 && endIndex == len - 1 )
            return parseJexl(manager,xmlText,createSubRange(0,len),text.substring(2, endIndex));

        int cur = 0;
        char c;

        StringBuilder expr  = new StringBuilder();

        MAIN:
        while ( cur < len ) {
            c = text.charAt( cur );

            switch (c) {
            case '$':
                if (cur + 1 < len) {
                    ++cur;
                    c = text.charAt(cur);

                    switch (c) {
                    case '$':
                        break;
                    case '{':
                        if (cur + 1 < len) {
                            ++cur;

                            while (cur < len) {
                                c = text.charAt(cur);
                                switch (c) {
                                case '"':
                                    expr.append(c);
                                    ++cur;

                                    DOUBLE_QUOTE:
                                    while (cur < len) {
                                        c = text.charAt(cur);

                                        switch (c) {
                                        case ('\\'):
                                            ++cur;
                                            expr.append(c);
                                            break;
                                        case ('"'):
                                            ++cur;
                                            expr.append(c);
                                            break DOUBLE_QUOTE;
                                        default:
                                            ++cur;
                                            expr.append(c);
                                        } // switch
                                    } // while
                                    break;
                                case '\'':
                                    expr.append(c);
                                    ++cur;

                                    SINGLE_QUOTE:
                                    while (cur < len) {
                                        c = text.charAt(cur);

                                        switch (c) {
                                        case ('\\'):
                                            ++cur;
                                            expr.append(c);
                                            break;
                                        case ('\''):
                                            ++cur;
                                            expr.append(c);
                                            break SINGLE_QUOTE;
                                        default:
                                            ++cur;
                                            expr.append(c);
                                        } // switch
                                    } // while
                                    break;
                                case '}':
                                    ProblemDescriptor[] r = parseJexl(manager,xmlText,
                                            createSubRange(cur-expr.length()-2, cur+1),
                                            expr.toString());
                                    // for now let's abort if we find one issue
                                    if(r.length!=0) return r;

                                    expr.setLength(0);
                                    ++cur;
                                    continue MAIN;
                                default:
                                    expr.append(c);
                                    ++cur;
                                }
                            }

                            return new ProblemDescriptor[] {
                                manager.createProblemDescriptor(xmlText,
                                        createSubRange(cur-expr.length()-2, cur+1),
                                        "Missing '}' character at the end of expression: ",
                                        ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                                        LocalQuickFix.EMPTY_ARRAY)
                            };
                        }
                        break;
                    }
                }
                break;
            }
            ++cur;
        }

        return EMPTY_ARRAY;
    }

    /**
     * Parses the expression to JEXL and report back any error.
     */
    private ProblemDescriptor[] parseJexl(InspectionManager manager, XmlText xmlText, TextRange range, String expr) {
        try {
            ExpressionFactory.createExpression(expr);
            return EMPTY_ARRAY;
        } catch (ParseException e) {
            range = new TextRange( // +2 to skip "${"
                    range.getStartOffset()+2+e.currentToken.next.beginColumn-1, // column is 1 origin
                    range.getStartOffset()+2+e.currentToken.next.endColumn);   // end origin is inclusive

            StringBuffer expected = new StringBuffer();
            int maxSize = 0;
            for (int i = 0; i < e.expectedTokenSequences.length; i++) {
              if (maxSize < e.expectedTokenSequences[i].length) {
                maxSize = e.expectedTokenSequences[i].length;
              }
              for (int j = 0; j < e.expectedTokenSequences[i].length; j++) {
                expected.append(e.tokenImage[e.expectedTokenSequences[i][j]]).append(" ");
              }
              if (e.expectedTokenSequences[i][e.expectedTokenSequences[i].length - 1] != 0) {
                expected.append("...");
              }
              expected.append(", ");
            }

            return new ProblemDescriptor[] {
                manager.createProblemDescriptor(xmlText,range,"Expecting "+expected,
                        ProblemHighlightType.GENERIC_ERROR_OR_WARNING, LocalQuickFix.EMPTY_ARRAY)
            };
        } catch (Exception e) {
            return new ProblemDescriptor[] {
                manager.createProblemDescriptor(xmlText,range,e.getMessage(),
                        ProblemHighlightType.GENERIC_ERROR_OR_WARNING, LocalQuickFix.EMPTY_ARRAY)
            };
        }
    }

    private TextRange createSubRange(int startIndex, int end) {
        return new TextRange(startIndex,end);
    }
}
