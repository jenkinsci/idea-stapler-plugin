package org.kohsuke.stapler.idea;

import com.intellij.facet.FacetType;
import com.intellij.facet.Facet;
import com.intellij.javaee.web.facet.WebFacet;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kohsuke.stapler.idea.icons.Icons;

import javax.swing.*;

/**
 * @author Kohsuke Kawaguchi
 */
public final class StaplerFacetType extends FacetType<StaplerFacet, StaplerFacetConfiguration> {
    public final static StaplerFacetType INSTANCE = new StaplerFacetType();

    private StaplerFacetType() {
        super(StaplerFacet.FACET_TYPE_ID, "Stapler", "Stapler", WebFacet.ID);
    }

    public StaplerFacetConfiguration createDefaultConfiguration() {
        return new StaplerFacetConfiguration();
    }

    public StaplerFacet createFacet(@NotNull Module module, String name, @NotNull StaplerFacetConfiguration configuration, @Nullable Facet underlyingFacet) {
        return new StaplerFacet(this, module, name, configuration, underlyingFacet);
    }

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
        return moduleType == ModuleType.JAVA;
    }
}
