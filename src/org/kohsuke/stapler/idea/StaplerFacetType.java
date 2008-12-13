package org.kohsuke.stapler.idea;

import com.intellij.facet.Facet;
import com.intellij.facet.FacetType;
import com.intellij.facet.FacetTypeId;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.JavaModuleType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kohsuke.stapler.idea.icons.Icons;

import javax.swing.*;

/**
 * @author Kohsuke Kawaguchi
 */
public final class StaplerFacetType extends FacetType<StaplerFacet, StaplerFacetConfiguration> {
    public final static FacetTypeId<StaplerFacet> ID = new FacetTypeId<StaplerFacet>("stapler");

    public final static StaplerFacetType INSTANCE = new StaplerFacetType();

    private StaplerFacetType() {
        // setting the 4th parameter restricits this facet to be only available as
        // a child of the given facet. For example it could be "WebFacet.ID"
        super(ID, "Stapler", "Stapler");
    }

    public StaplerFacetConfiguration createDefaultConfiguration() {
        return new StaplerFacetConfiguration();
    }

    public StaplerFacet createFacet(@NotNull Module module, String name, @NotNull StaplerFacetConfiguration configuration, @Nullable Facet underlyingFacet) {
        return new StaplerFacet(this, module, name, configuration, underlyingFacet);
    }

    /**
     * Returning true means only one facet can be configured per a module.
     */
    @Override
    public boolean isOnlyOneFacetAllowed() {
        return true;
    }

    @Override
    public Icon getIcon() {
        return Icons.STAPLER;
    }

    @Override
    public boolean isSuitableModuleType(ModuleType moduleType) {
        return moduleType instanceof JavaModuleType;
    }

    // if we can auto-detect the facet from a project
//    public void registerDetectors(final FacetDetectorRegistry<StaplerFacetConfiguration> facetDetectorRegistry) {
//      FacetDetectorRegistryEx<StaplerFacetConfiguration> detectorRegistry = (FacetDetectorRegistryEx<StaplerFacetConfiguration>)facetDetectorRegistry;
//      FacetDetector<VirtualFile,StaplerFacetConfiguration> facetDetector = new FacetDetector<VirtualFile,StaplerFacetConfiguration>() {
//        public StaplerFacetConfiguration detectFacet(final VirtualFile source,
//                                                              final Collection<StaplerFacetConfiguration> existentFacetConfigurations) {
//          if (!existentFacetConfigurations.isEmpty()) {
//            return existentFacetConfigurations.iterator().next();
//          }
//          return new StaplerFacetConfiguration();
//        }
//      };
//      detectorRegistry.registerUniversalDetector(StdFileTypes.XML, virtualFile().withName(string().endsWith(".gwt.xml")), facetDetector);
//    }
}
