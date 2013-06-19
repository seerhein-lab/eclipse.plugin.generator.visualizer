package com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.marker.quickfix;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.IMarkerResolutionGenerator2;

import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.marker.MarkerFactory;

public class MarkerResolutionGenerator implements IMarkerResolutionGenerator2 {
    
    @Override
    public IMarkerResolution[] getResolutions(IMarker marker) {
        return new IMarkerResolution2[] {
                new JumpToGenFileResolution(),
                new OpenInCompareViewResolution() };
    }

    @Override
    public boolean hasResolutions(IMarker marker) {
        Object attribute;
        try {
            attribute = marker.getAttribute(MarkerFactory.MARKER_ATTR_GEN_FILE_URL);
        } catch (CoreException e) {
            e.printStackTrace();
            return false;
        }
        if (attribute != null) {
            return true;
        }
        return false;
    }

}
