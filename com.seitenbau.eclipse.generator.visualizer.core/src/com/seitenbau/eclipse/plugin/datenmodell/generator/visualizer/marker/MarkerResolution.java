package com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.marker;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public class MarkerResolution implements IMarkerResolution2 {
    
    private String label;
    private String description;
    private Image image;
    
    public MarkerResolution(String label, String description) {
        this(label, description, null);
    }

    public MarkerResolution(String label, String description, Image image) {
        this.label = label;
        this.description = description;
        this.image = image;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public void run(IMarker marker) {
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        String filePath = marker.getAttribute(MarkerFactory.MARKER_ATTR_GEN_FILE_URL, null);
        
        if (filePath != null) {
            IPath path = new Path(filePath);
            IFile toOpen = marker.getResource().getWorkspace().getRoot().getFile(path);
            try {
                IDE.openEditor(page, toOpen);
            } catch (PartInitException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Image getImage() {
        return image;
    }

}
