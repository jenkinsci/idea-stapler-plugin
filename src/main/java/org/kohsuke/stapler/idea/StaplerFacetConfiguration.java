package org.kohsuke.stapler.idea;

import com.intellij.facet.FacetConfiguration;
import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.facet.ui.FacetValidatorsManager;

/**
 * @author Kohsuke Kawaguchi
 */
public class StaplerFacetConfiguration implements FacetConfiguration {
    @Override
    public FacetEditorTab[] createEditorTabs(FacetEditorContext editorContext, FacetValidatorsManager validatorsManager) {
        // nothing to configure
        return new FacetEditorTab[0];
    }
}
