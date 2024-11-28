package org.kohsuke.stapler.idea.symbols;

import com.intellij.openapi.project.Project;

import java.util.Set;

public interface SymbolFinder {

    Set<Symbol> getSymbols(Project project);
}
