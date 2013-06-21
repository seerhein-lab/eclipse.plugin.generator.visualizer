package com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.DatenmodellGeneratorVisualizerPlugin;

public class PrefInitializer extends AbstractPreferenceInitializer {

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = DatenmodellGeneratorVisualizerPlugin.getDefault().getPreferenceStore();
        store.setDefault(Preferences.PLUGIN_ACTIVE_SAVE, true);
        store.setDefault(Preferences.PLUGIN_ACTIVE_STARTUP, true);
        store.setDefault(Preferences.COMPLEMENT_GEN_ROOT, "src/ungemergtes-generat");
        store.setDefault(Preferences.COMPLEMENT_SRC_ROOT, "src/main/java");
        store.setDefault(
                Preferences.IGNORE_PREFERENCE, 
                "one" + Preferences.PREFERENCE_DELIMITER
                + "two");
    }

}
