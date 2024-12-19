package io.jenkins.stapler.idea.jelly.symbols;

import static org.kohsuke.stapler.idea.MavenProjectHelper.getArtifactId;

import com.intellij.openapi.project.Project;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class JenkinsSymbolLookup implements SymbolLookup {

    private static final String PREFIX = "symbol-";

    /** Adds the core Jenkins symbols if the project is a plugin */
    @Override
    public Set<Symbol> getSymbols(Project project) {
        if (Objects.equals(getArtifactId(project), "jenkins-parent")) {
            return Set.of();
        }

        return getCoreSymbols();
    }

    private Set<Symbol> getCoreSymbols() {
        Set<String> symbols = new HashSet<>();
        symbols.add("add");
        symbols.add("analytics");
        symbols.add("arrow-left");
        symbols.add("arrow-right");
        symbols.add("arrow-up");
        symbols.add("browsers");
        symbols.add("brush-outline");
        symbols.add("build-history");
        symbols.add("build-steps");
        symbols.add("build");
        symbols.add("changes");
        symbols.add("check");
        symbols.add("chevron-down");
        symbols.add("chevron-up");
        symbols.add("close-circle");
        symbols.add("close");
        symbols.add("cloud");
        symbols.add("code-working");
        symbols.add("compatible");
        symbols.add("computer-disconnected");
        symbols.add("computer-not-accepting");
        symbols.add("computer-offline");
        symbols.add("computer-paused");
        symbols.add("computer");
        symbols.add("cube");
        symbols.add("details");
        symbols.add("disconnect");
        symbols.add("document-text");
        symbols.add("downgrade-circle");
        symbols.add("download");
        symbols.add("edit-note");
        symbols.add("edit");
        symbols.add("ellipse");
        symbols.add("environment");
        symbols.add("error");
        symbols.add("expand");
        symbols.add("external");
        symbols.add("eye-off-outline");
        symbols.add("file-tray");
        symbols.add("fingerprint");
        symbols.add("flask");
        symbols.add("folder");
        symbols.add("freestyle-project");
        symbols.add("hammer");
        symbols.add("heart");
        symbols.add("help-circle");
        symbols.add("hourglass");
        symbols.add("id-card");
        symbols.add("indeterminate");
        symbols.add("information-circle");
        symbols.add("jenkins");
        symbols.add("journal");
        symbols.add("key");
        symbols.add("link");
        symbols.add("list");
        symbols.add("lock-closed");
        symbols.add("log-out");
        symbols.add("logs");
        symbols.add("menu");
        symbols.add("none");
        symbols.add("notifications");
        symbols.add("paper-plane-outline");
        symbols.add("parameters");
        symbols.add("pause");
        symbols.add("people");
        symbols.add("person-circle");
        symbols.add("person");
        symbols.add("play");
        symbols.add("plugins");
        symbols.add("post-build");
        symbols.add("power");
        symbols.add("project-relationship");
        symbols.add("redo");
        symbols.add("refresh");
        symbols.add("reload");
        symbols.add("ribbon");
        symbols.add("rss");
        symbols.add("search-shortcut");
        symbols.add("search");
        symbols.add("server");
        symbols.add("settings");
        symbols.add("shield-warning");
        symbols.add("shopping-bag");
        symbols.add("source-code-management");
        symbols.add("status-aborted-anime");
        symbols.add("status-aborted");
        symbols.add("status-blue-anime");
        symbols.add("status-blue");
        symbols.add("status-disabled-anime");
        symbols.add("status-disabled");
        symbols.add("status-nobuilt-anime");
        symbols.add("status-nobuilt");
        symbols.add("status-red-anime");
        symbols.add("status-red");
        symbols.add("status-yellow-anime");
        symbols.add("status-yellow");
        symbols.add("swap");
        symbols.add("tag");
        symbols.add("terminal");
        symbols.add("trash-bin");
        symbols.add("trash");
        symbols.add("trigger");
        symbols.add("undo");
        symbols.add("up-to-date");
        symbols.add("view");
        symbols.add("weather-icon-health-00to19");
        symbols.add("weather-icon-health-20to39");
        symbols.add("weather-icon-health-40to59");
        symbols.add("weather-icon-health-60to79");
        symbols.add("weather-icon-health-80plus");
        symbols.add("windows");

        return symbols.stream()
                .map(e -> new Symbol(PREFIX + e, PREFIX + e, null))
                .collect(Collectors.toSet());
    }
}
