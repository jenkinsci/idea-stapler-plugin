package org.kohsuke.stapler.idea.language;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.kohsuke.stapler.idea.psi.JellyPsi;
import org.kohsuke.stapler.idea.psi.JellyTag;

/**
 * @author Kohsuke Kawaguchi
 */
final class JellyAnnotator implements Annotator {
    public static final JellyAnnotator INSTANCE = new JellyAnnotator();

    private JellyAnnotator() {
    }

    public void annotate(PsiElement psi, AnnotationHolder holder) {
        if (psi instanceof JellyPsi) {
            JellyPsi jps = (JellyPsi) psi;
            if (jps instanceof JellyTag) {
                JellyTag tag = (JellyTag) jps;
                if(tag.getSourceElement().getLocalName().length()==3)
                    holder.createErrorAnnotation(tag,"test error");            
            }
        }
    }
}
