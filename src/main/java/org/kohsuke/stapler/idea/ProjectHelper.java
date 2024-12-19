package org.kohsuke.stapler.idea;

import com.intellij.openapi.project.Project;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class ProjectHelper {

    public static Document getPomFile(Project project) {
        File pomFile = new File(project.getBasePath() + "/pom.xml");

        if (!pomFile.exists()) {
            return null;
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        try {
            return builder.parse(pomFile);
        } catch (SAXException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getArtifactId(Project project) {
        Document document = getPomFile(project);

        try {
            // Create an XPath object
            XPath xPath = XPathFactory.newInstance().newXPath();
            String expression = "/project/artifactId";

            // Evaluate the XPath expression and return the result
            String artifactId = (String) xPath.evaluate(expression, document, XPathConstants.STRING);
            return artifactId.isEmpty() ? null : artifactId;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
