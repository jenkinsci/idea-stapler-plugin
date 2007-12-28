package org.kohsuke.stapler.idea;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.intellij.psi.xml.XmlToken;
import com.intellij.psi.xml.XmlFile;
import com.intellij.pom.event.PomModelListener;
import com.intellij.pom.event.PomModelEvent;
import com.intellij.pom.event.PomChangeSet;
import com.intellij.pom.xml.XmlChangeSet;
import com.intellij.pom.xml.XmlAspect;
import com.intellij.pom.xml.events.XmlChange;
import com.intellij.pom.PomModelAspect;
import com.intellij.pom.PomModel;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.kohsuke.stapler.idea.psi.impl.ChangeVisitorImpl;

import java.util.Iterator;

/**
 * @author Kohsuke Kawaguchi
 */
public class StaplerProjectComponent implements ProjectComponent {
    private ReferenceProvidersRegistry registry;
//    private NamespaceFilter stripesNamespaceFilter;

    public StaplerProjectComponent(Project project, PomModel pomModel, final XmlAspect xmlAspect) {
        registry = ReferenceProvidersRegistry.getInstance(project);

        pomModel.addModelListener(new PomModelListener() {
            public void modelChanged(PomModelEvent event) {
                PomChangeSet changeSet = event.getChangeSet(xmlAspect);
                if (changeSet instanceof XmlChangeSet) {
                    XmlChangeSet xmlChangeSet = (XmlChangeSet) changeSet;
                    XmlFile file = xmlChangeSet.getChangedFile();
                    if (file == null || !file.getName().endsWith(".jelly"))
                        return;

                    for (XmlChange c : xmlChangeSet.getChanges())
                        c.accept(ChangeVisitorImpl.INSTANCE);
                }
            }

            public boolean isAspectChangeInteresting(PomModelAspect aspect) {
                return aspect == xmlAspect;
            }
        }, project);
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
//        registry.registerXmlTagReferenceProvider(null,null,true/*what is this?*/,new JellyTagLibReferenceProvider());

        // this doesn't call us back at all
//        registry.registerReferenceProvider(XmlToken.class,new JellyTagLibReferenceProvider());
    }

    public void disposeComponent() {
    }
}
