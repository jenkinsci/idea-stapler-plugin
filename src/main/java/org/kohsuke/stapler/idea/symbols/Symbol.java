package org.kohsuke.stapler.idea.symbols;

import javax.swing.Icon;

public class Symbol {

    private final String name;

    private final String displayText;

    private final String group;

    private final String path;

    private final Icon icon;

    public Symbol(String name, String displayText, String group, String path, Icon icon) {
        this.name = name;
        this.displayText = displayText;
        this.group = group;
        this.path = path;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public String getDisplayText() {
        return displayText;
    }

    public String getGroup() {
        return group;
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
