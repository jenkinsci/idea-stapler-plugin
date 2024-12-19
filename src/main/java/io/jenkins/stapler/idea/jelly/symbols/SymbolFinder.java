package io.jenkins.stapler.idea.jelly.symbols;

import com.intellij.openapi.project.Project;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public final class SymbolFinder {

    private static final ConcurrentMap<Project, Set<Symbol>> ICONS_CACHE = new ConcurrentHashMap<>();

    private static final List<SymbolLookup> SYMBOL_LOOKUPS =
            List.of(new LocalSymbolLookup(), new JenkinsSymbolLookup(), new IoniconsApiSymbolLookup());

    public static Set<Symbol> getAvailableSymbols(Project project) {
        return ICONS_CACHE.computeIfAbsent(project, SymbolFinder::computeSymbols);
    }

    private static Set<Symbol> computeSymbols(Project project) {
        return SYMBOL_LOOKUPS.stream()
                .flatMap(finder -> finder.getSymbols(project).stream())
                .collect(Collectors.toSet());
    }
}
