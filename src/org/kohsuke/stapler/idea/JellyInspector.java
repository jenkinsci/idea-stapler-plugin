package org.kohsuke.stapler.idea;

import com.intellij.codeInspection.InspectionToolProvider;

/**
 * @see http://d.hatena.ne.jp/masanobuimai/20070822
 * @author Kohsuke Kawaguchi
 */
public class JellyInspector implements InspectionToolProvider {
    public Class[] getInspectionClasses() {
        return new Class[]{JexlInspection.class};
    }
}
