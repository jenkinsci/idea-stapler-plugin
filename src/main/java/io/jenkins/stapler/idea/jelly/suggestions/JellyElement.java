package io.jenkins.stapler.idea.jelly.suggestions;

import com.intellij.xml.XmlAttributeDescriptor;
import java.util.List;
import javax.swing.*;

public record JellyElement(
        String name, int contentType, List<XmlAttributeDescriptor> attributesDescriptors, Icon icon) {}
