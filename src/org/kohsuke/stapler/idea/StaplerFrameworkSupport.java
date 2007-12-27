package org.kohsuke.stapler.idea;

import com.intellij.facet.impl.ui.FacetTypeFrameworkSupportProvider;
import com.intellij.openapi.roots.ModifiableRootModel;

/**
 * This component exposes "stapler" as a facet to the new project wizard.
 * @author Kohsuke Kawaguchi
 */
public class StaplerFrameworkSupport extends FacetTypeFrameworkSupportProvider<StaplerFacet> {
    public StaplerFrameworkSupport() {
        super(StaplerFacetType.INSTANCE);
    }

    // GWT Studio returns this, but I have no idea what this does.
    // just leaving it here as a note
//    public String[] getPrecedingFrameworkProviderIds() {
//        return new String[]{getProviderId(WebFacet.ID)};
//    }

    protected void setupConfiguration(StaplerFacet facet, ModifiableRootModel modifiablerootmodel, String s) {
        // noop?
    }
}
