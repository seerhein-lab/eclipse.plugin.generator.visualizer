package com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.marker.quickfix;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution2;

public abstract class BaseResolution implements IMarkerResolution2 {
    
    protected String label;
    protected String description;
    protected Image image;
    
    public BaseResolution(String label, String description, Image image) {
        super();
        this.label = label;
        this.description = description;
        this.image = image;
    }

}
