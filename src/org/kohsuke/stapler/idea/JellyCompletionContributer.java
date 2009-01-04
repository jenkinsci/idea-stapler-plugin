package org.kohsuke.stapler.idea;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupItem;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.XmlElementPattern;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlToken;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.ProcessingContext;
import com.intellij.util.xml.DomManager;
import org.jetbrains.annotations.NotNull;
import org.kohsuke.stapler.idea.psi.ref.TagReference;
import org.kohsuke.stapler.idea.dom.model.JellyTag;
import org.kohsuke.stapler.idea.dom.model.AttributeTag;

/**
 * Attribute name completion for Jelly tag libraries define as tag files. 
 *
 * @author Kohsuke Kawaguchi
 */
public class JellyCompletionContributer extends CompletionContributor {
    public JellyCompletionContributer() {
        extend(CompletionType.BASIC, // in case of XML completion, this always seems to be BASIC
                XML_ATTRIBUTE_NAME_PATTERN,
                new CompletionProvider<CompletionParameters>(false, true) {
                    // REFERENCE: spring plugin adds CompletionContributor as well.
                    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
                        XmlElement name = (XmlElement)parameters.getPosition();

                        // this XmlAttribute contains a dummy name (presumably just to
                        // keep the shape of the PSI tree.)
                        XmlAttribute a = (XmlAttribute) name.getParent();
                        XmlTag tag = a.getParent();

                        System.out.println("Completing attribute name inside "+tag.getName());

                        // is this a reference to a tag file? If so, try to look up
                        // definitions from there
                        XmlFile tagFile = new TagReference(tag).resolve();
                        if(tagFile!=null) {
                            DomManager manager = DomManager.getDomManager(tagFile.getProject());
                            JellyTag root = manager.getFileElement(tagFile, JellyTag.class).getRootElement();
                            for( AttributeTag att : root.getDocumentation().getAttributes() ) {
                                String n = att.getName().getStringValue();
                                if(n!=null)
                                    result.addElement(LookupItem.fromString(n));
                            }
                        }
                    }
                }
        );
    }

    // REFERENCE: the following is useful for figuring out how CompletionParameters
    // are populated and passed to us.
    //
    // On XML completion, type is always BASIC,
    // and pos for element/attribute name completions are XmlToken.
    //
    // TODO: XmlCompletionContributor is another implementation of CompletionContributer.
//    @Override
//    public boolean fillCompletionVariants(CompletionParameters parameters, CompletionResultSet result) {
//        System.out.println("type="+parameters.getCompletionType());
//        System.out.println("pos ="+parameters.getPosition());
//        return super.fillCompletionVariants(parameters, result);
//    }

    /**
     * {@link ElementPattern} that matches attribute names.
     */
    private static final ElementPattern XML_ATTRIBUTE_NAME_PATTERN =
            new XmlElementPattern(XmlToken.class) {}.withParent(XmlAttribute.class);
}
