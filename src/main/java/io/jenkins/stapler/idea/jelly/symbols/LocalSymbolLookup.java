package io.jenkins.stapler.idea.jelly.symbols;

import static org.kohsuke.stapler.idea.MavenProjectHelper.getArtifactId;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import java.util.HashSet;
import java.util.Set;
import org.intellij.images.fileTypes.impl.SvgFileType;

public class LocalSymbolLookup implements SymbolLookup {

    /** Adds project-specific symbols */
    @Override
    public Set<Symbol> getSymbols(Project project) {
        String artifactId = getArtifactId(project);
        Set<Symbol> svgFiles = new HashSet<>();

        FileTypeIndex.processFiles(
                SvgFileType.INSTANCE,
                file -> {
                    if (isValidSymbolFile(file)) {
                        processSvgFile(artifactId, file, svgFiles);
                    }
                    return true;
                },
                GlobalSearchScope.projectScope(project));

        return svgFiles;
    }

    /**
     * Validates if a given file is in a valid symbols directory.
     *
     * @param file The file to validate.
     * @return True if the file resides in an "images/symbols" directory, false otherwise.
     */
    private boolean isValidSymbolFile(VirtualFile file) {
        VirtualFile parent = file.getParent();
        return parent != null
                && parent.getPath().contains("images/symbols")
                && !parent.getPath().contains("test");
    }

    /**
     * Processes a valid SVG file and adds it to the set of symbols.
     *
     * @param artifactId The name of the project
     * @param file The SVG file to process.
     * @param svgFiles The set to which the new symbol will be added.
     */
    private void processSvgFile(String artifactId, VirtualFile file, Set<Symbol> svgFiles) {
        boolean isCore = file.getPath().contains("/war/");

        String name = "symbol-" + file.getNameWithoutExtension();
        svgFiles.add(new Symbol(
                name + (isCore ? "" : " plugin-" + artifactId), name, isCore ? null : "plugin-" + artifactId));
    }
}
