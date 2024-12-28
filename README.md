# ["Jenkins Development Support" for IntelliJ IDEA](https://plugins.jetbrains.com/plugin/1885-stapler-plugin-for-intellij-idea)

[![Build Status](https://ci.jenkins.io/buildStatus/icon?job=Plugins%2Fidea-stapler-plugin%2Fmaster)](https://ci.jenkins.io/job/Plugins/job/idea-stapler-plugin/job/master/)
[![Join the chat at https://gitter.im/jenkinsci/idea-stapler-plugin](https://badges.gitter.im/jenkinsci/idea-stapler-plugin.svg)](https://gitter.im/jenkinsci/idea-stapler-plugin)

[//]: # (Update plugin features in build.gradle as well if they are worth mentioning in key features)
## Features

### Stapler

* "Go to stapler view" to jump from a Java class to its views (Cmd/Ctrl+Shift+P)
* Java Structure tool window shows Jelly views as members
* Jelly Structure tool window shows an owner class besides XML tree
* Select a string expression, then "Refactor" > "i18n for Stapler" to create a message resource

### Jelly

* Automatically recognized as an XML file (`*.jelly` and `*.jellytag`)
* "Jenkins Jelly View" file template to speed up creation of views
* Navigation from the Jelly tags to their definitions
  * Integrate `st:documentation` tag into IntelliJ documentation support
  * Report custom tag attributes that are marked deprecated in `st:documentation`.
* Error checks and autocompletion on attributes and elements of taglibs
* `style` attribute and `script` tag contents should be recognized as CSS and JavaScript correspondingly.
* [Jenkins Symbols](https://weekly.ci.jenkins.io/design-library/symbols/) suggestions are available when using `<l:icon src="..." />`

## JEXL

* Syntax error checks on JEXL expressions
