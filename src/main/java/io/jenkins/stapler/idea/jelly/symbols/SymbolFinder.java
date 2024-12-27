package io.jenkins.stapler.idea.jelly.symbols;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.AsyncFileListener;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Service(Service.Level.PROJECT)
public final class SymbolFinder implements Disposable {

    private final Project project;

    private Set<Symbol> ICONS_CACHE = null;

    private final List<SymbolLookup> SYMBOL_LOOKUPS =
            List.of(new LocalSymbolLookup(), new JenkinsSymbolLookup(), new IoniconsApiSymbolLookup());

    public SymbolFinder(@NotNull Project project) {
        this.project = project;

        VirtualFileManager.getInstance()
                .addAsyncFileListener(
                        new AsyncFileListener() {
                            @Override
                            public @Nullable ChangeApplier prepareChange(
                                    @NotNull List<? extends @NotNull VFileEvent> events) {
                                boolean svgChanged = events.stream()
                                        .anyMatch(event -> event.getFile() != null
                                                && event.getFile().getName().endsWith(".svg"));

                                if (svgChanged) {
                                    return new ChangeApplier() {
                                        @Override
                                        public void afterVfsChange() {
                                            invalidateCache();
                                        }
                                    };
                                }

                                return null;
                            }
                        },
                        this);
    }

    public static SymbolFinder getInstance(Project project) {
        return project.getService(SymbolFinder.class);
    }

    public Set<Symbol> getAvailableSymbols() {
        if (ICONS_CACHE == null) {
            ICONS_CACHE = computeSymbols();
        }
        return ICONS_CACHE;
    }

    public void invalidateCache() {
        ICONS_CACHE = null;
    }

    private Set<Symbol> computeSymbols() {
        return SYMBOL_LOOKUPS.stream()
                .flatMap(finder -> finder.getSymbols(project).stream())
                .collect(Collectors.toSet());
    }

    @Override
    public void dispose() {
        // Needn't implement this, 'addVirtualFileListener' will dispose when this class does
    }
}
