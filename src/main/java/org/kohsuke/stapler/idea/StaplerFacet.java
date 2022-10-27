package org.kohsuke.stapler.idea;

import com.intellij.facet.Facet;
import com.intellij.facet.FacetManager;
import com.intellij.facet.FacetType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Kohsuke Kawaguchi
 */
public class StaplerFacet extends Facet<StaplerFacetConfiguration> {
    // suppressing rawtypes since this is imposed by the SDK
    public StaplerFacet(@SuppressWarnings("rawtypes") @NotNull FacetType facetType, @NotNull Module module, String name, @NotNull StaplerFacetConfiguration configuration, @SuppressWarnings("rawtypes") Facet underlyingFacet) {
        super(facetType, module, name, configuration, underlyingFacet);
    }

    @Nullable
    public static StaplerFacet getInstance(@NotNull Module module) {
        return FacetManager.getInstance(module).getFacetByType(StaplerFacetType.ID);
    }

    @Nullable
    public static StaplerFacet findFacetBySourceFile(@NotNull Project project, @Nullable VirtualFile file) {
        if (file == null) return null;

        final Module module = ModuleUtil.findModuleForFile(file, project);
        if (module == null) return null;

        return getInstance(module);
    }
}
