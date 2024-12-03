package org.kohsuke.stapler.idea.psi;

import com.intellij.psi.FileViewProvider;
import com.intellij.psi.impl.source.xml.XmlFileImpl;
import com.intellij.psi.tree.IElementType;

/** @author Julien Greffe */
public class JellyFileImpl extends XmlFileImpl implements JellyFile {

    public JellyFileImpl(FileViewProvider viewProvider, IElementType elementType) {
        super(viewProvider, elementType);
    }

    @Override
    public String toString() {
        return "JellyFile:" + getName();
    }
}
