package com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.marker.quickfix;

import org.eclipse.compare.CompareUI;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.DatenmodellGeneratorVisualizerPlugin;
import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.diff.compare.CompareInput;
import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.dto.Complement;
import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.job.ResourceWorker;
import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.marker.MarkerFactory;

public class OpenInCompareViewResolution extends BaseResolution {

    private static final Image icon = 
            AbstractUIPlugin
                .imageDescriptorFromPlugin(
                        DatenmodellGeneratorVisualizerPlugin
                            .getInstance()
                            .getBundle()
                            .getSymbolicName(), 
                        "icons/fontAwesome/columns.png").createImage();

    public OpenInCompareViewResolution() {
        super(
                "Open Complement in a Compare View", 
                "Opens a compare view. " +
                "On the left side the source file will be compared with the " +
                "generated original file on the right side.", 
                icon);
    }

    @Override
    public void run(IMarker marker) {
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        String filePath = marker.getAttribute(MarkerFactory.MARKER_ATTR_GEN_FILE_URL, null);
        
        if (filePath != null) {
            if (marker.getResource() instanceof IFile) {
                Complement toCompare = 
                        ResourceWorker
                            .findGeneratedComplement(
                                    (IFile) marker.getResource(), 
                                    marker.getResource().getProject());
                
                CompareUI.openCompareEditorOnPage(new CompareInput(toCompare), page);
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

    @Override
    public String getLabel() {
        return label;
    }

}
