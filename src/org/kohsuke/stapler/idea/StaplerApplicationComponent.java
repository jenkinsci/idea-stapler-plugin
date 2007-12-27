package org.kohsuke.stapler.idea;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.facet.FacetTypeRegistry;
import com.intellij.codeInspection.InspectionToolProvider;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kohsuke Kawaguchi
 */
public class StaplerApplicationComponent implements ApplicationComponent, InspectionToolProvider {
    @NonNls @NotNull
    public String getComponentName() {
        return getClass().getSimpleName();
    }

    public void initComponent() {
        FacetTypeRegistry.getInstance().registerFacetType(StaplerFacetType.INSTANCE);
    }

    public void disposeComponent() {
        // noop
    }

    public Class[] getInspectionClasses() {
        return new Class[]{JexlInspection.class};
    }
}
