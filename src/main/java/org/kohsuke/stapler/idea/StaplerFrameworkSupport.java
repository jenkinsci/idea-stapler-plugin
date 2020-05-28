package org.kohsuke.stapler.idea;

import com.intellij.facet.ui.FacetBasedFrameworkSupportProvider;
import com.intellij.ide.util.frameworkSupport.FrameworkVersion;
import com.intellij.openapi.roots.ModifiableRootModel;

/**
 * This component exposes "stapler" as a facet to the new project wizard.
 * @author Kohsuke Kawaguchi
 */
public class StaplerFrameworkSupport extends FacetBasedFrameworkSupportProvider<StaplerFacet> {
    public StaplerFrameworkSupport() {
        super(StaplerFacetType.INSTANCE);
    }

    // GWT Studio returns this, but I have no idea what this does.
    // just leaving it here as a note
//    public String[] getPrecedingFrameworkProviderIds() {
//        return new String[]{getProviderId(WebFacet.ID)};
//    }

    @Override
    protected void setupConfiguration(StaplerFacet facet, ModifiableRootModel rootModel, FrameworkVersion version) {
        // noop?
    }
}
