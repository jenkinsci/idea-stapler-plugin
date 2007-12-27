package org.kohsuke.stapler.idea;

import com.intellij.facet.Facet;
import com.intellij.facet.FacetType;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kohsuke Kawaguchi
 */
public class StaplerFacet extends Facet<StaplerFacetConfiguration> {
    public StaplerFacet(@NotNull FacetType facetType, @NotNull Module module, String name, @NotNull StaplerFacetConfiguration configuration, Facet underlyingFacet) {
        super(facetType, module, name, configuration, underlyingFacet);
    }

//    public WebFacet getWebFacet() {
//        return (WebFacet) getUnderlyingFacet();
//    }
//
//    public PsiFile getWebXmlPsiFile() {
//        return getWebFacet().getWebXmlDescriptor().getPsiFile();
//    }
}
