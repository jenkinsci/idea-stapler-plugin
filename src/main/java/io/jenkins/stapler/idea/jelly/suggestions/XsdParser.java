package io.jenkins.stapler.idea.jelly.suggestions;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import org.kohsuke.stapler.idea.JellyCompletionContributor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XsdParser {

    /**
     * @param file The schema to load
     * @return a list of tags from the schema definition
     */
    public static List<String> getTagsFromSchema(String file) {
        List<String> response = new ArrayList<>();
        try {
            ClassLoader classLoader = JellyCompletionContributor.class.getClassLoader();
            InputStream inputStream =
                    classLoader.getResourceAsStream("org/kohsuke/stapler/idea/resources/schemas/" + file + ".xsd");

            if (inputStream == null) {
                throw new RuntimeException("Schema not found: " + file + ".xsd");
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            Document document = factory.newDocumentBuilder().parse(inputStream);
            NodeList attributeNodes = document.getElementsByTagName("xsd:element");

            for (int i = 0; i < attributeNodes.getLength(); i++) {
                Element element = (Element) attributeNodes.item(i);
                String name = element.getAttribute("name");
                response.add(name);
            }
        } catch (Exception e) {
            System.out.println("Error parsing XSD: " + e.getMessage());
        }
        return response;
    }
}
