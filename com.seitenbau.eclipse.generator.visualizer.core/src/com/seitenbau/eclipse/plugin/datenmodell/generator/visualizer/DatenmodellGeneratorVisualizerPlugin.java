package com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.texteditor.ITextEditor;
import org.osgi.framework.BundleContext;

import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.job.ResourceWorker;
import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.listener.ResourceChangeReporter;
import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.marker.MarkerFactory;
import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.preferences.MainPrefPage;

/**
 * The activator class controls the plug-in life cycle
 */
public class DatenmodellGeneratorVisualizerPlugin extends AbstractUIPlugin implements IStartup {

    // The plug-in ID
    public static final String PLUGIN_ID = "com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer"; //$NON-NLS-1$

    // The shared instance
    private static DatenmodellGeneratorVisualizerPlugin plugin;

    /**
     * The constructor
     */
    public DatenmodellGeneratorVisualizerPlugin() {
    }
    
    public static DatenmodellGeneratorVisualizerPlugin getInstance() {
        return plugin;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
     * )
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        
        System.out.println(PLUGIN_ID);
        System.out.println(plugin.getBundle().getBundleId());
        
        // listener
        IResourceChangeListener listener = new ResourceChangeReporter();
        ResourcesPlugin.getWorkspace().addResourceChangeListener(listener, IResourceChangeEvent.POST_CHANGE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
     * )
     */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static DatenmodellGeneratorVisualizerPlugin getDefault() {
        return plugin;
    }
    
    /**
     * Returns an image descriptor for the image file at the given
     * plug-in relative path
     *
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }
    
    /**
     * Always good to have this static method as when dealing with IResources
     * having a interface to get the editor is very handy
     * @return
     */
    public static ITextEditor getEditor() {
        return (ITextEditor) getActiveWorkbenchWindow().getActivePage().getActiveEditor();
    }
    
    
    public static Shell getShell() {
        return getActiveWorkbenchWindow().getShell();
    }
    
    public static IWorkbenchWindow getActiveWorkbenchWindow() {
        return PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    }

    @Override
    public void earlyStartup() {
        if (getDefault().getPreferenceStore().getBoolean(MainPrefPage.PLUGIN_ACTIVE_STARTUP)) {
            ResourceWorker.scheduleFullWorkspaceScan(3000);
        } else {
            try {
                MarkerFactory.deleteAllMarkers(ResourcesPlugin.getWorkspace().getRoot());
            } catch (CoreException e) {
                e.printStackTrace();
            }
        }
        
    }
    
    /**
     * Returns the selection of the package explorer
     */
    public static TreeSelection getTreeSelection() {

        ISelection selection = DatenmodellGeneratorVisualizerPlugin.getActiveWorkbenchWindow().getSelectionService().getSelection();
        if (selection instanceof TreeSelection) {
            return (TreeSelection)selection;
        }
        return null;
    }

}
