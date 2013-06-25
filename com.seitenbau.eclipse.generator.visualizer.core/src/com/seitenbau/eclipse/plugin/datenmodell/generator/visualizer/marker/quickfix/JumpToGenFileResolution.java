package com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.marker.quickfix;


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
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.DatenmodellGeneratorVisualizerPlugin;
import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.marker.MarkerFactory;

public class JumpToGenFileResolution implements IMarkerResolution2 {
    
    private static final Image image = 
            AbstractUIPlugin
                .imageDescriptorFromPlugin(
                        DatenmodellGeneratorVisualizerPlugin
                            .getInstance()
                            .getBundle()
                            .getSymbolicName(), 
                        "icons/fontAwesome/share.png").createImage();
    
    private static final String label = "Jump to generated complement file"; 
    private static final String description = "Using the provided link the generated complement " +
            "will be opend in an new edior view."; 

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
                /*IEditorPart editor = */IDE.openEditor(page, toOpen);
                // ref: http://stackoverflow.com/a/12258003/810944
                // IDE.gotoMarker(editor, marker);
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
