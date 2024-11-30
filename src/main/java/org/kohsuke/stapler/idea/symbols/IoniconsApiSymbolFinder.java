package org.kohsuke.stapler.idea.symbols;

import com.intellij.openapi.project.Project;
import io.jenkins.plugins.ionicons.Ionicons;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.stream.Collectors;

import static org.kohsuke.stapler.idea.icons.Icons.convertSymbol;
import static org.kohsuke.stapler.idea.icons.Icons.convertSymbolToIcon;

public class IoniconsApiSymbolFinder implements SymbolFinder {

    private static final String PREFIX = "symbol-";
    private static final String SUFFIX = " plugin-ionicons-api";

    /**
     * Adds the Ionicons API symbols if the plugin has the dependency added
     */
    @Override
    public Set<Symbol> getSymbols(Project project) {
        if (!hasIoniconsApi(project)) {
            return Set.of();
        }

        return Ionicons.getAvailableIcons().keySet().stream().map(e -> new Symbol(PREFIX + e + SUFFIX,
            PREFIX + e, "plugin-ionicons-api", null, readSvg(e))).collect(Collectors.toSet());
    }

    private static boolean hasIoniconsApi(Project project) {
        File pomFile = new File(project.getBasePath() + "/pom.xml");

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
            // Create an XPath object
            XPath xPath = XPathFactory.newInstance().newXPath();
            String expression = "/project/dependencies/dependency[groupId='io.jenkins.plugins']/artifactId[text()='ionicons-api']";

            // Evaluate the XPath expression and return the result
            String artifactId = (String) xPath.evaluate(expression, document, XPathConstants.STRING);
            return !artifactId.isEmpty();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Icon readSvg(String name) {
        // Get the resource path for the SVG
        InputStream fileStream = Ionicons.class.getResourceAsStream("/images/symbols/" + name + ".svg");

        if (fileStream == null) {
            throw new IllegalArgumentException("SVG file not found: " + name);
        }

        // Use BufferedReader to read the file as a String
        StringBuilder svgContent = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                svgContent.append(line).append("\n");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error reading SVG file: " + name, e);
        }

        return convertSymbolToIcon(convertSymbol(svgContent.toString()));
    }
}
