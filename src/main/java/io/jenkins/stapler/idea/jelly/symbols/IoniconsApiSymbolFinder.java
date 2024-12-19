package io.jenkins.stapler.idea.jelly.symbols;

import static org.kohsuke.stapler.idea.ProjectHelper.getPomFile;

import com.intellij.openapi.project.Project;
import io.jenkins.plugins.ionicons.Ionicons;
import java.util.Set;
import java.util.stream.Collectors;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;

public class IoniconsApiSymbolFinder implements SymbolFinder {

    private static final String PREFIX = "symbol-";
    private static final String SUFFIX = " plugin-ionicons-api";

    /** Adds the Ionicons API symbols if the plugin has the dependency added */
    @Override
    public Set<Symbol> getSymbols(Project project) {
        if (!hasIoniconsApi(project)) {
            return Set.of();
        }

        return Ionicons.getAvailableIcons().keySet().stream()
                .map(e -> new Symbol(PREFIX + e + SUFFIX, PREFIX + e, "plugin-ionicons-api"))
                .collect(Collectors.toSet());
    }

    private static boolean hasIoniconsApi(Project project) {
        Document document = getPomFile(project);

        try {
            XPath xPath = XPathFactory.newInstance().newXPath();
            String expression =
                    "/project/dependencies/dependency[groupId='io.jenkins.plugins']/artifactId[text()='ionicons-api']";

            String hasIoniconsApi = (String) xPath.evaluate(expression, document, XPathConstants.STRING);
            return !hasIoniconsApi.isEmpty();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
