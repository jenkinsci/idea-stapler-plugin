# ["Jenkins Development Support" for IntelliJ IDEA](https://plugins.jetbrains.com/plugin/1885-stapler-plugin-for-intellij-idea)

[//]: # (Content between "Plugin description" markers are extracted by gradle build. No markdown formatting. Simple html only.)
<!-- Plugin description -->
<p>Assists with development of Jenkins core and plugins.</p>
<p>Supports following technologies that are not covered by any other IntelliJ plugins:</p>
<ul>
  <li><a href="https://stapler.kohsuke.org/">Stapler HTTP request handling engine</a></li>
  <li><a href="https://commons.apache.org/proper/commons-jelly/index.html">Jelly : Executable XML</a></li>
  <li><a href="https://commons.apache.org/proper/commons-jexl/">Java Expression Language (JEXL)</a></li>
</ul>
<!-- Plugin description end -->

Features include:

## Stapler

* "Go to stapler view" to jump from a Java class to its views (Cmd/Ctrl+Shift+P)
* Select a string expression, then "Refactor" > "i18n for Stapler" to create a message resource

## Jelly

* Automatically recognized as an XML file (`*.jelly` and `*.jellytag`)
* Navigation from the Jelly tags to their definitions
  * Integrate `st:documentation` tag into IntelliJ documentation support
* Error checks and autocompletion on attributes and elements of taglibs
* `style` attribute and `script` tag contents should be recognized as CSS and JavaScript correspondingly.

## JEXL

* Syntax error checks on JEXL expressions (if Stapler [Facet](https://www.jetbrains.com/help/idea/facet-page.html) is configured)
