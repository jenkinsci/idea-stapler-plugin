package org.kohsuke.stapler.idea.symbols;

import javax.swing.*;

public class Symbol {

    private final String name;

    private final String path;

    private final Icon icon;

    public Symbol(String name, String path, Icon icon) {
        this.name = name;
        this.path = path;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public Icon getIcon() {
        return icon;
    }

    @Override
    public String toString() {
        return "Symbol{" +
            "name='" + name + '\'' +
            ", path='" + path + '\'' +
            '}';
    }
}
