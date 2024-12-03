package org.kohsuke.stapler.idea.symbols;

import static org.kohsuke.stapler.idea.ProjectHelper.getArtifactId;
import static org.kohsuke.stapler.idea.icons.Icons.convertSymbolToIcon;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import java.util.HashSet;
import java.util.Set;

public class LocalSymbolFinder implements SymbolFinder {

    /** Adds project specific symbols */
    @Override
    public Set<Symbol> getSymbols(Project project) {
        Set<Symbol> svgFiles = new HashSet<>();
        VirtualFile baseDir = project.getBaseDir();

        if (baseDir != null) {
            for (VirtualFile child : baseDir.getChildren()) {
                // Check for the 'images/symbols' subdirectory within 'src' or 'war'
                if (child.isDirectory()
                        && (child.getName().equalsIgnoreCase("src")
                                || child.getName().equalsIgnoreCase("war"))) {
                    boolean isCore = false;
                    VirtualFile imagesDir;

                    if (child.getName().equalsIgnoreCase("war")) {
                        isCore = true;
                        imagesDir = child.findFileByRelativePath("src/main/resources/images/symbols");
                    } else {
                        imagesDir = child.findFileByRelativePath("main/resources/images/symbols");
                    }

                    if (imagesDir != null && imagesDir.isDirectory()) {
                        for (VirtualFile file : imagesDir.getChildren()) {
                            if (!file.isDirectory() && file.getName().endsWith(".svg")) {
                                String artifactId;
                                artifactId = getArtifactId(project);
                                String name = "symbol-" + file.getName().replace(".svg", "");

                                svgFiles.add(new Symbol(
                                        name + (isCore ? "" : " plugin-" + artifactId),
                                        name,
                                        isCore ? null : "plugin-" + artifactId,
                                        file.getPath(),
                                        convertSymbolToIcon(file)));
                            }
                        }
                    }
                }
            }
        }

        return svgFiles;
    }
}
