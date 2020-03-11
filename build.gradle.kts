plugins {
    id("org.jetbrains.intellij") version "0.4.16"
    java
}

group = "org.kohsuke.stapler.idea"
version = "1.8"

repositories {
    mavenCentral()
    maven("https://repo.jenkins-ci.org/releases/")
}

dependencies {
    compile("org.jenkins-ci", "commons-jexl", "1.1-jenkins-20111212")
    compile("net.java.dev.textile-j", "textile-j", "2.2.864")

    //testCompile("junit", "junit", "4.12") // Not yet used
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version = "2016.3.1"
    setPlugins("properties")
}
configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks.getByName<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
    changeNotes("""
      Add change notes here.<br>
      <em>most HTML tags may be used</em>""")
}
