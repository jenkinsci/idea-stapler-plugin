package org.kohsuke.stapler.idea.psi.link;

import com.intellij.psi.FileViewProvider;
import com.intellij.psi.impl.source.xml.XmlFileImpl;
import com.intellij.psi.tree.IElementType;

/**
 * Link typed jelly file to avoid infinite loops when displaying structure view
 *
 * @author Julien Greffe
 */
public class LinkJellyFileImpl extends XmlFileImpl {

    public LinkJellyFileImpl(FileViewProvider viewProvider, IElementType elementType) {
        super(viewProvider, elementType);
    }

    @Override
    public String toString() {
        return "Link" + super.toString();
    }
}
