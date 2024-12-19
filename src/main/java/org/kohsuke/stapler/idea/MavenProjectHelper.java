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

public class MavenProjectHelper {

    private static Document getMavenProject(Project project) {
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
        Document mavenProject = getMavenProject(project);

        if (mavenProject == null) {
            return null;
        }

        try {
            XPath xPath = XPathFactory.newInstance().newXPath();
            String expression = "/project/artifactId";

            String artifactId = (String) xPath.evaluate(expression, mavenProject, XPathConstants.STRING);
            return artifactId.isEmpty() ? null : artifactId;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean hasDependency(Project project, String groupId, String artifactId) {
        Document mavenProject = getMavenProject(project);

        if (mavenProject == null) {
            return false;
        }

        try {
            XPath xPath = XPathFactory.newInstance().newXPath();
            String expression = "/project/dependencies/dependency[groupId='" + groupId + "']/artifactId[text()='"
                    + artifactId + "']";

            String hasIoniconsApi = (String) xPath.evaluate(expression, mavenProject, XPathConstants.STRING);
            return !hasIoniconsApi.isEmpty();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
