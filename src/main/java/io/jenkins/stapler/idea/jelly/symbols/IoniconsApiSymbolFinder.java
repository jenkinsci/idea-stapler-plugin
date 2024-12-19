package io.jenkins.stapler.idea.jelly.symbols;

import com.intellij.openapi.project.Project;
import io.jenkins.plugins.ionicons.Ionicons;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

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
                .map(e -> new Symbol(PREFIX + e + SUFFIX, PREFIX + e, "plugin-ionicons-api", null))
                .collect(Collectors.toSet());
    }

    private static boolean hasIoniconsApi(Project project) {
        File pomFile = new File(project.getBasePath() + "/pom.xml");

        if (!pomFile.exists()) {
            return false;
        }

        // Create a DocumentBuilderFactory
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        Document document;
        try {
            document = builder.parse(pomFile);
        } catch (SAXException | IOException e) {
            throw new RuntimeException(e);
        }

        try {
            XPath xPath = XPathFactory.newInstance().newXPath();
            String expression =
                    "/project/dependencies/dependency[groupId='io.jenkins.plugins']/artifactId[text()='ionicons-api']";

            String artifactId = (String) xPath.evaluate(expression, document, XPathConstants.STRING);
            return !artifactId.isEmpty();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
