package org.kohsuke.stapler.idea.symbols;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.SVGLoader;

import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import static org.kohsuke.stapler.idea.ProjectHelper.getArtifactId;

public class LocalSymbolFinder implements SymbolFinder {

    /**
     * Adds project specific symbols
     */
    @Override
    public Set<Symbol> getSymbols(Project project) {
        Set<Symbol> svgFiles = new HashSet<>();
        VirtualFile baseDir = project.getBaseDir();

        if (baseDir != null) {
            for (VirtualFile child : baseDir.getChildren()) {
                // Check for the 'images/symbols' subdirectory within 'src' or 'war'
                if (child.isDirectory() && (child.getName().equalsIgnoreCase("src") || child.getName().equalsIgnoreCase("war"))) {
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

                                svgFiles.add(new Symbol(name + (isCore ? "" : " plugin-" + artifactId), name, isCore ? null : "plugin-" + artifactId, file.getPath(), convertSymbolToIcon(file)));
                            }
                        }
                    }
                }
            }
        }

        return svgFiles;
    }

    public static Icon convertSymbolToIcon(VirtualFile svgFile) {
        try {
            // Ensure the file exists and is not a directory
            if (svgFile.exists() && !svgFile.isDirectory() && svgFile.getName().endsWith(".svg")) {
                byte[] contentBytes = svgFile.contentsToByteArray();
                return convertSymbolToIcon(new String(contentBytes, StandardCharsets.UTF_8)
                    .replaceAll("var(--blue)", "currentColor")
                    .replaceAll("var(--yellow)", "currentColor")
                    .replaceAll("var(--red)", "currentColor")
                    .replaceAll("var(--text-color-secondary)", "currentColor")
                    .replaceAll("var(--text-color)", "currentColor")
                    .replaceAll("currentColor", "#CED0D6"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Icon convertSymbolToIcon(String svgContent) {
        try {
            // Convert the SVG content into an InputStream
            ByteArrayInputStream inputStream = new ByteArrayInputStream(svgContent.getBytes(StandardCharsets.UTF_8));

            // Use IntelliJ's SVGLoader to render the icon
            var image = SVGLoader.load(inputStream, 1f);

            return new ImageIcon(image);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
