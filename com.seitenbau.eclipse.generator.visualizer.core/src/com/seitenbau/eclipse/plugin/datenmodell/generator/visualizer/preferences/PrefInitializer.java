package com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.DatenmodellGeneratorVisualizerPlugin;

public class PrefInitializer extends AbstractPreferenceInitializer {

    @Override
    public void initializeDefaultPreferences() {
        System.out.println("pref init");
        IPreferenceStore store = DatenmodellGeneratorVisualizerPlugin.getDefault().getPreferenceStore();
        store.setDefault(MainPrefPage.PLUGIN_ACTIVE_SAVE, true);
        store.setDefault(MainPrefPage.PLUGIN_ACTIVE_STARTUP, true);
        store.setDefault(MainPrefPage.COMPLEMENT_GEN_ROOT, "src/ungemergtes-generat");
        store.setDefault(MainPrefPage.COMPLEMENT_SRC_ROOT, "src/main/java");
        
    }

}
