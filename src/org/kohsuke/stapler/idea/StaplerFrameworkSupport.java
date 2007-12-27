package org.kohsuke.stapler.idea;

import com.intellij.facet.impl.ui.FacetTypeFrameworkSupportProvider;
import com.intellij.javaee.web.facet.WebFacet;
import com.intellij.openapi.roots.ModifiableRootModel;

/**
 * This component exposes "stapler" as a facet to the new project wizard.
 * @author Kohsuke Kawaguchi
 */
public class StaplerFrameworkSupport extends FacetTypeFrameworkSupportProvider<StaplerFacet> {
    public StaplerFrameworkSupport() {
        super(StaplerFacetType.INSTANCE);
    }

    /**
     * Returns the list of required facets to use our facet.
     */
    public String[] getPrecedingFrameworkProviderIds() {
        return new String[]{getProviderId(WebFacet.ID)};
    }

    protected void setupConfiguration(StaplerFacet facet, ModifiableRootModel modifiablerootmodel, String s) {
        // noop?
    }
}
