package com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.common;

import org.eclipse.core.resources.IEncodedStorage;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

public class Utilities {
    
    public static String getCharset(Object resource) {
        if (resource instanceof IEncodedStorage) {
            try {
                return ((IEncodedStorage) resource).getCharset();
            } catch (CoreException ex) {
                ex.printStackTrace();
            }
        }
        return ResourcesPlugin.getEncoding();
    }

}
