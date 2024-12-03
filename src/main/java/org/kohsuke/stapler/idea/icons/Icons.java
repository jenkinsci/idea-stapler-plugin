package org.kohsuke.stapler.idea.icons;

import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.SVGLoader;

import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

/** @author Kohsuke Kawaguchi */
public class Icons {
    public static final Icon JELLY = IconLoader.getIcon("/org/kohsuke/stapler/idea/icons/jelly.svg", Icons.class);
    public static final Icon JENKINS = IconLoader.getIcon("/org/kohsuke/stapler/idea/icons/jenkins.svg", Icons.class);

    public static String convertSymbol(String svgContent) {
        return svgContent.replaceAll("var(--blue)", "currentColor")
            .replaceAll("var(--yellow)", "currentColor")
            .replaceAll("var(--red)", "currentColor")
            .replaceAll("var(--text-color-secondary)", "currentColor")
            .replaceAll("var(--text-color)", "currentColor")
            .replaceAll("currentColor", "#CED0D6");
    }

    public static Icon convertSymbolToIcon(VirtualFile svgFile) {
        try {
            // Ensure the file exists and is not a directory
            if (svgFile.exists() && !svgFile.isDirectory() && svgFile.getName().endsWith(".svg")) {
                byte[] contentBytes = svgFile.contentsToByteArray();
                return convertSymbolToIcon(convertSymbol(new String(contentBytes, StandardCharsets.UTF_8)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Icon convertSymbolToIcon(String svgContent) {
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
