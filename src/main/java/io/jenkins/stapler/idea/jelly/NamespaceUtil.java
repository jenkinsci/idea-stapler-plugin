package io.jenkins.stapler.idea.jelly;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlTag;
import java.util.HashMap;
import java.util.Map;
import org.kohsuke.stapler.idea.language.JellyFileType;
import org.kohsuke.stapler.idea.psi.JellyFile;

public class NamespaceUtil {

    /** Collects all namespaces (and prefixes) from .jelly files in the project */
    public static Map<String, String> collectProjectNamespaces(Project project) {
        Map<String, String> namespaces = new HashMap<>();

        FileTypeIndex.processFiles(
                JellyFileType.INSTANCE,
                file -> {
                    PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
                    if (psiFile instanceof JellyFile jellyFile) {
                        XmlTag rootTag = jellyFile.getRootTag();
                        if (rootTag != null) {
                            String[] uris = rootTag.knownNamespaces();

                            for (String uri : uris) {
                                String prefix = rootTag.getPrefixByNamespace(uri);

                                if (prefix == null || prefix.isEmpty()) {
                                    continue;
                                }

                                // Ignore local prefixes as they're not for global usage
                                if (prefix.equals("local") || prefix.equals("this")) {
                                    continue;
                                }

                                namespaces.put(prefix, uri);
                            }
                        }
                    }
                    return true;
                },
                GlobalSearchScope.projectScope(project));

        return namespaces;
    }
}
