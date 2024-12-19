package io.jenkins.stapler.idea.jelly.symbols;

import static org.kohsuke.stapler.idea.MavenProjectHelper.hasDependency;

import com.intellij.openapi.project.Project;
import io.jenkins.plugins.ionicons.Ionicons;
import java.util.Set;
import java.util.stream.Collectors;

public class IoniconsApiSymbolFinder implements SymbolFinder {

    private static final String PREFIX = "symbol-";
    private static final String SUFFIX = " plugin-ionicons-api";

    /** Adds the Ionicons API symbols if the plugin has the dependency added */
    @Override
    public Set<Symbol> getSymbols(Project project) {
        if (!hasDependency(project, "io.jenkins.plugins", "ionicons-api")) {
            return Set.of();
        }

        return Ionicons.getAvailableIcons().keySet().stream()
                .map(e -> new Symbol(PREFIX + e + SUFFIX, PREFIX + e, "plugin-ionicons-api"))
                .collect(Collectors.toSet());
    }
}
