package com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.listener;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.job.ResourceWorker;
import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.marker.MarkerFactory;
import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.preferences.Preferences;

public class PropertyChangeReporter implements IPropertyChangeListener {

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (event.getProperty() == Preferences.PLUGIN_ACTIVE_STARTUP) {
            Boolean value = Boolean.valueOf((event.getNewValue().toString()));
            if (!value) {
                // OFF
                try {
                    MarkerFactory.deleteAllMarkers();
                } catch (CoreException e) {
                    e.printStackTrace();
                }
            } else {
                // ON
                ResourceWorker.scheduleFullWorkspaceScan(0);
            }
        }
    }

}
