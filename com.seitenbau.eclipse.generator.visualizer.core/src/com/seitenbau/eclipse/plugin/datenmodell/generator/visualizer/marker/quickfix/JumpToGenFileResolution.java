package com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.marker.quickfix;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.DatenmodellGeneratorVisualizerPlugin;
import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.marker.MarkerFactory;

public class JumpToGenFileResolution extends BaseResolution {
    
    private static final Image icon = 
            AbstractUIPlugin
                .imageDescriptorFromPlugin(
                        DatenmodellGeneratorVisualizerPlugin
                            .getInstance()
                            .getBundle()
                            .getSymbolicName(), 
                        "icons/jumpto.gif").createImage();
    
    public JumpToGenFileResolution() {
        super(
                "Jump to generated complement file", 
                "Using the provided link the generated complement will be opend in an new edior view.", 
                icon
            );
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
