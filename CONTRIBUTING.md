# Contributing to "Jenkins Development Support" for IntelliJ IDEA

Thank you for considering contributing to the project! In the Jenkins project we appreciate any kind of contributions: code, documentation, design, etc.
Any contribution counts, and the size does not matter!
Check out [this page](https://jenkins.io/participate/) for more information and links!

## Providing feedback

* **Never report security issues on GitHub, public Jira issues or other public channels (Gitter/Twitter/etc.), 	
follow the instruction from [Jenkins Security](https://www.jenkins.io/security/#reporting-vulnerabilities) to 
report it on [Jenkins Jira](https://issues.jenkins.io/)**
* For anything else please use [GitHub Issues](https://github.com/jenkinsci/idea-stapler-plugin/issues)

ℹ️ Note, that issue templates are inherited from the organization. Bug report requests too many details regarding Jenkins which are irrelevant for the plugin. Please disregard and instead open "About IntelliJ" and use "Copy" button. ([Override organization issue templates for this repository](https://github.com/jenkinsci/idea-stapler-plugin/issues/110))

## Source code contribution ways of working

- For larger contributions create an issue for any required discussion
- Implement solution on a branch in your fork
- Make sure to include issue ID (if created) in commit message, and make the message speak for itself
- Once you're done create a pull request and ask at least one of the maintainers for review
    - Remember to title your pull request properly as it is used for release notes

## Things to learn before code contribution

Landing page for IntelliJ plugin developers is [JetBrains Platform](https://plugins.jetbrains.com/developers).

The most relevant information for development of this plugin is in [IntelliJ Platform Plugin SDK](https://plugins.jetbrains.com/docs/intellij/welcome.html).

Find plugin implementation examples on [IntelliJ Platform Explorer](https://plugins.jetbrains.com/intellij-platform-explorer/extensions).

IntelliJ IDEA has a hidden tooling for plugin developers. See [Internal Actions Menu](https://plugins.jetbrains.com/docs/intellij/internal-actions-intro.html) 

## How to Run Locally

### Prerequisites

JDK 11 is the only pre-requisite.

Run `./gradlew buildPlugin` or `gradlew.bat buildPlugin` to get Gradle and all the necessary dependencies

### CLI

`./gradlew runIde` will start IDEA version specified in gradle.properties with plugin installed

### IntelliJ IDEA

1. Create new project from existing source using Gradle project
2. Use one of the persisted "runIde" Run/Debug configurations to start IDEA based on gradle.properties or a specific version

https://github.com/jenkinsci/design-library-plugin is a good small project for manual testing since it demonstrates various Stapler and Jelly features.
