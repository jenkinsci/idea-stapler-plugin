# ["Stapler Framework Support" for IntelliJ IDEA](https://plugins.jetbrains.com/plugin/1885-stapler-plugin-for-intellij-idea)

[Stapler HTTP request handling engine](https://stapler.kohsuke.org/) support. As well as support for [Jelly](https://commons.apache.org/proper/commons-jelly/index.html) and [JEXL](http://commons.apache.org/proper/commons-jexl/) that do no have specialized plugins.

Some of the features

## Stapler

* "Go to stapler view" to jump from a Java class to its views (Cmd/Ctrl+Shift+P)
* Select a string expression, then "Refactor" > "i18n for Stapler" to create a message resource

## Jelly

* Automatically recognized as an XML file (`*.jelly` and `*.jellytag`)
* Navigation from the Jelly tags to their definitions
* Error checks and auto completion on attributes and elements of taglibs
* `style` attribute and `script` tag contents should be recognized as CSS and JavaScript correspondingly.

## JEXL

* Syntax error checks on JEXL expressions (if Stapler [Facet](https://www.jetbrains.com/help/idea/facet-page.html) is configured)
