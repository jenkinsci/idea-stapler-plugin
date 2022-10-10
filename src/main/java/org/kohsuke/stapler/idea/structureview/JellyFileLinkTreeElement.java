package org.kohsuke.stapler.idea.structureview;

import java.util.Collection;
import java.util.Collections;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.xml.XmlFileTreeElement;
import org.jetbrains.annotations.NotNull;
import org.kohsuke.stapler.idea.psi.JellyFile;

/**
 * Simple tree element to display file without children
 *
 * @author Julien Greffe
 */
public class JellyFileLinkTreeElement extends XmlFileTreeElement {

    public JellyFileLinkTreeElement(JellyFile file) {
        super(file);
    }

    @Override
    public @NotNull Collection<StructureViewTreeElement> getChildrenBase() {
        return Collections.emptyList();
    }
}
