package org.kohsuke.stapler.idea;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlText;
import org.apache.commons.jexl.ExpressionFactory;
import org.apache.commons.jexl.parser.ParseException;

/**
 * @author Kohsuke Kawaguchi
 */
public class JexlInspection extends LocalXmlInspectionTool {

    @Override
    protected ProblemDescriptor[] checkXmlAttributeValue(XmlAttributeValue text, InspectionManager manager, boolean onTheFly) {
        return check(text,manager,text.getValue(),text.getValueTextRange(), onTheFly);
    }

    @Override
    protected ProblemDescriptor[] checkXmlText(XmlText xmlText, InspectionManager manager, boolean onTheFly) {
        return check(xmlText,manager,xmlText.getText(),xmlText.getTextRange(), onTheFly);
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
    /**
     * Checks JEXL expresisons and return problem descriptors if found.
     *  @param text
     *      The text value that may contain JEXL expressions.
     * @param range
     * @param onTheFly
     */
    protected ProblemDescriptor[] check(XmlElement psi, InspectionManager manager, String text, TextRange range, boolean onTheFly) {
        if(!psi.getContainingFile().getName().endsWith(".jelly"))
            return EMPTY_ARRAY; // not a jelly script
        if(!shouldCheck(psi))
            return EMPTY_ARRAY; // stapler not enabled

        int len = text.length();

        int startIndex = text.indexOf( "${" );

        if ( startIndex < 0) {
            return EMPTY_ARRAY;
        }

        // convert range to be relative to the PSI element
        range = range.shiftRight(-psi.getTextRange().getStartOffset());

        int endIndex = text.indexOf( "}", startIndex+2 );

        if ( endIndex < 0 )
            return new ProblemDescriptor[] {
                manager.createProblemDescriptor(psi,
                        new TextRange(startIndex, len),
                        "Missing '}' character at the end of expression: ",
                        ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                        onTheFly,
                        LocalQuickFix.EMPTY_ARRAY)
            };

        if ( startIndex == 0 && endIndex == len - 1 )
            return toArray(parseJexl(manager,psi,shrink(range,2,1),text.substring(2, endIndex),onTheFly));

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
                                    ProblemDescriptor[] r = toArray(parseJexl(manager,psi,
                                            new TextRange(cur - expr.length() - 2, cur + 1).shiftRight(range.getStartOffset()),
                                            expr.toString(), onTheFly));
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
                                manager.createProblemDescriptor(psi,
                                        new TextRange(cur - expr.length() - 2, cur + 1).shiftRight(range.getStartOffset()),
                                        "Missing '}' character at the end of expression: ",
                                        ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                                        onTheFly,
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

    private ProblemDescriptor[] toArray(ProblemDescriptor p) {
        if(p==null) return EMPTY_ARRAY;
        return new ProblemDescriptor[]{p};
    }

    private TextRange shrink(TextRange range, int l, int r) {
        return new TextRange(range.getStartOffset()-l, range.getEndOffset()-r);
    }

    /**
     * Parses the expression to JEXL and report back any error.
     *  @param psi
     *      PSI element that contains the given text.
     * @param range
     * @param onTheFly
     */
    private ProblemDescriptor parseJexl(InspectionManager manager, XmlElement psi, TextRange range, String expr, boolean onTheFly) {
        try {
            if(expr.startsWith("%")) { // property reference
                int idx = expr.indexOf('(');
                if(idx<0)
                    // no arguments
                    return null;

                if(idx==1) {
                    // no property name given
                    range = new TextRange( // +3 to skip "${%"
                            range.getStartOffset()+3,
                            range.getStartOffset()+4);

                    return manager.createProblemDescriptor(psi,range,"Property name is empty",
                        ProblemHighlightType.GENERIC_ERROR_OR_WARNING, onTheFly, LocalQuickFix.EMPTY_ARRAY);
                }

                int offset = range.getStartOffset()+2+idx+1;
                expr = expr.substring(idx+1);   // at this point text="arg,arg)"
                while(expr.length()>0) {
                    String token = tokenize(expr);
                    if(token==null)
                        return manager.createProblemDescriptor(psi,range,"Missing ')' at the end",
                            ProblemHighlightType.GENERIC_ERROR_OR_WARNING, onTheFly, LocalQuickFix.EMPTY_ARRAY);

                    try {
                        ExpressionFactory.createExpression(token);
                    } catch (ParseException e) {
                        return handleParseException(manager, psi, e, offset, onTheFly);
                    }
                    expr = expr.substring(token.length()+1);
                    offset += token.length()+1;
                }
                // OK
                return null;
            } else {
                ExpressionFactory.createExpression(expr);
                return null;
            }
        } catch (ParseException e) {
            return handleParseException(manager, psi, e, range.getStartOffset()+2, onTheFly); // +2 to skip "${"
        } catch (Exception e) {
            String msg = e.getMessage();
            if(msg==null)   msg=e.toString();
            return manager.createProblemDescriptor(psi,range,msg,
                ProblemHighlightType.GENERIC_ERROR_OR_WARNING, onTheFly, LocalQuickFix.EMPTY_ARRAY);
        }
    }

    private ProblemDescriptor handleParseException(InspectionManager manager, XmlElement psi, ParseException e, int offset, boolean onTheFly) {
        TextRange range;
        range = new TextRange(
                offset +e.currentToken.next.beginColumn-1, // column is 1 origin
                offset +e.currentToken.next.endColumn);   // end origin is inclusive

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

        return manager.createProblemDescriptor(psi,range,"Expecting "+expected,
                    ProblemHighlightType.GENERIC_ERROR_OR_WARNING, onTheFly, LocalQuickFix.EMPTY_ARRAY);
    }

    private String tokenize(String text) {
        int parenthesis=0;
        for(int idx=0;idx<text.length();idx++) {
            char ch = text.charAt(idx);
            switch (ch) {
            case ',':
                if(parenthesis==0)
                    return text.substring(0,idx);
                break;
            case '(':
            case '{':
            case '[':
                parenthesis++;
                break;
            case ')':
                if(parenthesis==0)
                    return text.substring(0,idx);
                // fall through
            case '}':
            case ']':
                parenthesis--;
                break;
            case '"':
            case '\'':
                // skip strings
                idx = text.indexOf(ch,idx+1);
                break;
            }
        }
        return null;
    }
}
