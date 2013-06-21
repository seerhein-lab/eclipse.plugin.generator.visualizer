package com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.listener;

import java.util.ArrayList;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;

import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.DatenmodellGeneratorVisualizerPlugin;
import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.job.ResourceWorker;
import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.preferences.Preferences;

/**
 * Listens on resource change events.
 * @see http://www.eclipse.org/articles/Article-Resource-deltas/resource-deltas.html for tipps
 */
public class ResourceChangeReporter implements IResourceChangeListener {

    @Override
    public void resourceChanged(IResourceChangeEvent event) {
        boolean activeOnSave = DatenmodellGeneratorVisualizerPlugin
                .getDefault()
                .getPreferenceStore()
                .getBoolean(Preferences.PLUGIN_ACTIVE_SAVE);
        if (!activeOnSave) {
            return;
        }
        
        IResourceDelta rootDelta = event.getDelta();
        
        final ArrayList<IResource> changed = new ArrayList<IResource>();
        IResourceDeltaVisitor visitor = new IResourceDeltaVisitor() {
           public boolean visit(IResourceDelta delta) {
              //only interested in changed resources (not added or removed)
              if (delta.getKind() != IResourceDelta.CHANGED) {
                 return true;
              }
              //only interested in content changes
              if ((delta.getFlags() & IResourceDelta.CONTENT) == 0) {
                 return true;
              }
              IResource resource = delta.getResource();
              //only interested in files with the "java" extension
              if (resource.getType() == IResource.FILE && 
               "java".equalsIgnoreCase(resource.getFileExtension())) {
                 changed.add(resource);
              }
              return true;
           }
        };
        try {
            rootDelta.accept(visitor);
        } catch (CoreException e) {
            e.printStackTrace();
        }
        
        ResourceWorker.scheduleUpdateJob(changed);

    }

}
