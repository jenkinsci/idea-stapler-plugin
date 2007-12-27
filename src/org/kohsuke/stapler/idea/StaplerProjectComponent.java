package org.kohsuke.stapler.idea;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kohsuke Kawaguchi
 */
public class StaplerProjectComponent implements ProjectComponent {
    private ReferenceProvidersRegistry registry;
//    private NamespaceFilter stripesNamespaceFilter;

    public StaplerProjectComponent(Project project)
    {
        registry = ReferenceProvidersRegistry.getInstance(project);
    }

    public void projectOpened() {
    }

    public void projectClosed() {
    }

    @NonNls @NotNull
    public String getComponentName() {
        return getClass().getSimpleName();
    }

    public void initComponent() {
        // since the first two parameters are null, this reference provider applies everywhere
        // this was actually not what I was looking for --- this defines reference from the
        // content of an XML element, not from an XML element name.
        //registry.registerXmlTagReferenceProvider(null,null,true/*what is this?*/,new JellyTagLibReferenceProvider());
    }

    public void disposeComponent() {
    }
}
