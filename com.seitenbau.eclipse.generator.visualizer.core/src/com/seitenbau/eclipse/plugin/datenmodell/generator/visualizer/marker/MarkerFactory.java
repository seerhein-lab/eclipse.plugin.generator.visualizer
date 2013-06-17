package com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.marker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.compare.rangedifferencer.RangeDifference;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.Position;
import org.eclipse.ui.texteditor.MarkerUtilities;

import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.common.Constants;
import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.dto.Complement;

public class MarkerFactory {

    // Marker ID
    public static final String GEN_MARKER = Constants.PLUGIN_NS_PREFIX + ".marker.generated";
    // Annotation ID
    public static final String GEN_ANNOTATION = Constants.PLUGIN_NS_PREFIX + ".annotation.generated";

    // Marker ID
    public static final String ADDED_MARKER = Constants.PLUGIN_NS_PREFIX + ".marker.added";
    // Annotation ID
    public static final String ADDED_ANNOTATION = Constants.PLUGIN_NS_PREFIX + ".annotation.added";

    // Marker ID
    public static final String DELETED_MARKER = Constants.PLUGIN_NS_PREFIX + ".marker.deleted";
    // Annotation ID
    public static final String DELETED_ANNOTATION = Constants.PLUGIN_NS_PREFIX + ".annotation.deleted";

    // Marker ID
    public static final String CHANGED_BOTH_MARKER = Constants.PLUGIN_NS_PREFIX + ".marker.changedBoth";
    // Annotation ID
    public static final String CHANGED_BOTH_ANNOTATION = Constants.PLUGIN_NS_PREFIX + ".annotation.changedBoth";
    
    /*
     * Common consts
     */
    public static final String MARKER_ATTR_GEN_FILE_URL = "com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.marker.attr.complement.genFile.url";

    public enum MarkerType {

        GENERATED(GEN_MARKER, GEN_ANNOTATION, "Generated"), //
        ADDED(ADDED_MARKER, ADDED_ANNOTATION, "Added"), // 
        DELETED(DELETED_MARKER, DELETED_ANNOTATION, "Deleted"), //
        CHANGED_BOTH(CHANGED_BOTH_MARKER, CHANGED_BOTH_ANNOTATION, "Modified"), ;

        private String markerId;

        private String annotationId;
        
        private String markerPrefix;

        private MarkerType(String markerId, String annotationId, String markerPrefix) {
            this.markerId = markerId;
            this.annotationId = annotationId;
            this.markerPrefix = markerPrefix;
        }

        public String getMarkerId() {
            return markerId;
        }

        public String getAnnotationId() {
            return annotationId;
        }
        
        public String getMarkerPrefix() {
            return markerPrefix;
        }
        
        public static MarkerType getByDiff(RangeDifference diff) {
            int leftLength = diff.leftLength();
            int rightLength = diff.rightLength();
            if (rightLength == 0) {
                return ADDED;
            }
            if (leftLength == 0) {
                return DELETED;
            }
            return CHANGED_BOTH;
        }
    }

    /*
     * Creates a Marker
     */
    public static void createMarkerByTextSelection(
            Complement res, 
            MarkerType markerType,
            Position position, 
            String markerMsg) throws CoreException {
        int start = position.getOffset();
        int end = position.getOffset() + position.getLength();
        
        Map<String, Object> attrs = new HashMap<String, Object>();
        attrs.put(IMarker.CHAR_START, start);
        attrs.put(IMarker.CHAR_END, end);
        attrs.put(IMarker.MESSAGE, markerType.getMarkerPrefix() + ": " + markerMsg);
        attrs.put(MARKER_ATTR_GEN_FILE_URL, res.getGeneratedFile().getFullPath().toString());
        
        MarkerUtilities.createMarker(res.getSrcFile(), attrs, markerType.getMarkerId());
//        System.out.println("New Marker '" + markerType + "' in " + res.getName() + " at " + position);
    }

    /**
     * Returns a list of markers that are linked to the resource or any sub
     * resource of the resource.
     */
    public static List<IMarker> findAllMarkers(IResource resource) {
        return findMarkers(resource, IResource.DEPTH_INFINITE);
    }
    
    /**
     * Returns a list of markers that are linked to the resource.
     */
    public static List<IMarker> findAllMarkersInResource(IResource resource) {
        return findMarkers(resource, IResource.DEPTH_ZERO);
    }
    
    public static void deleteMarkersInResource(IResource resource) throws CoreException {
        List<IMarker> allMarkersInResource = findAllMarkersInResource(resource);
        removeMarkers(allMarkersInResource);
    }
    
    public static void deleteAllMarkers(IResource resource) throws CoreException {
        List<IMarker> allMarkers = findAllMarkers(resource);
        removeMarkers(allMarkers);
    }
    
    private static void removeMarkers(List<IMarker> findAllMarkers) throws CoreException {
        System.out.println("Deleting " + findAllMarkers.size() + " markers.");
        for (IMarker toDel : findAllMarkers) {
            toDel.delete();
        }
    }
    
    private static List<IMarker> findMarkers(IResource resource, int depth) {
        try {
            List<IMarker> result = new ArrayList<IMarker>();

             result.addAll(
                     Arrays.asList(
                             resource.findMarkers(
                                     MarkerType.ADDED.markerId, 
                                     true, 
                                     depth)));
             result.addAll(
                     Arrays.asList(
                             resource.findMarkers(
                                     MarkerType.CHANGED_BOTH.markerId,
                                     true, 
                                     depth)));
             result.addAll(
                     Arrays.asList(
                             resource.findMarkers(
                                     MarkerType.DELETED.markerId,
                                     true, 
                                     depth)));
             result.addAll(
                     Arrays.asList(
                             resource.findMarkers(
                                     MarkerType.GENERATED.markerId,
                                     true, 
                                     depth)));
//            result.addAll(Arrays.asList(resource.findMarkers(null, true,
//                    IResource.DEPTH_INFINITE)));

            return result;
        } catch (CoreException e) {
            return new ArrayList<IMarker>();
        }
    }
}
