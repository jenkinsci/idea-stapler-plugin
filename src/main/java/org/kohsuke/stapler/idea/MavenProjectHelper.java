package org.kohsuke.stapler.idea;

import com.intellij.openapi.project.Project;

import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

public class MavenProjectHelper {

    private static MavenProject getMavenProject(Project project) {
        MavenProjectsManager manager = MavenProjectsManager.getInstance(project);

        if (manager == null || !manager.isMavenizedProject() || manager.getProjects().isEmpty()) {
            return null;
        }

        return manager.getProjects().get(0);
    }

    public static String getArtifactId(Project project) {
        MavenProject mavenProject = getMavenProject(project);

        if (mavenProject == null) {
            return null;
        }

        return mavenProject.getMavenId().getArtifactId();
    }

    public static boolean hasDependency(Project project, String groupId, String artifactId) {
        MavenProject mavenProject = getMavenProject(project);

        if (mavenProject == null) {
            return false;
        }

        System.out.println(mavenProject.getDependencies());

        return mavenProject.hasDependency(groupId, artifactId);
    }
}
