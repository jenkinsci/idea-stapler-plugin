package org.kohsuke.stapler.idea.structureview;

import java.util.Collection;
import java.util.Collections;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.xml.XmlFileTreeElement;
import com.intellij.psi.xml.XmlFile;
import org.jetbrains.annotations.NotNull;

/**
 * Simple tree element to display file without children
 *
 * @author Julien Greffe
 */
public class LinkJellyFileTreeElement extends XmlFileTreeElement {

    public LinkJellyFileTreeElement(XmlFile file) {
        super(file);
    }

    @Override
    public @NotNull Collection<StructureViewTreeElement> getChildrenBase() {
        return Collections.emptyList();
    }
}
