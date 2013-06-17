package com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.preferences;


import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.DatenmodellGeneratorVisualizerPlugin;
import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.common.Constants;

public class MainPrefPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
    
    public static final String PLUGIN_ACTIVE_SAVE = Constants.PLUGIN_NS_PREFIX + ".prefs.pluginActiveSave";
    public static final String PLUGIN_ACTIVE_STARTUP = Constants.PLUGIN_NS_PREFIX + ".prefs.pluginActiveStartup";
    public static final String COMPLEMENT_GEN_ROOT = Constants.PLUGIN_NS_PREFIX + ".prefs.complement.gen.folder";
    public static final String COMPLEMENT_SRC_ROOT = Constants.PLUGIN_NS_PREFIX + ".prefs.complement.src.folder";
    
    public MainPrefPage() {
        super(GRID);
    }

    @Override
    public void init(IWorkbench workbench) {
        setPreferenceStore(DatenmodellGeneratorVisualizerPlugin.getDefault().getPreferenceStore());
        setDescription("Configuration of your Datenmodell Generator Visualizer Plugin.");
    }

    @Override
    protected void createFieldEditors() {
        addField(
                new BooleanFieldEditor(
                        PLUGIN_ACTIVE_SAVE, 
                        "&Visualizer runs after saving a resource\n" +
                        "(only the changed resources will be analyzed)", 
                        getFieldEditorParent()));
        addField(
                new BooleanFieldEditor(
                        PLUGIN_ACTIVE_STARTUP, 
                        "&Visualizer runs after eclipse startup\n" +
                        "(full workspace scan will be performed)", 
                        getFieldEditorParent()));
        
        Group complement = new Group(getFieldEditorParent(), SWT.NULL);
        complement.setText("Complement Settings");
        
        addField(
                new StringFieldEditor(
                        COMPLEMENT_GEN_ROOT , 
                        "Root folder of the generated resources:", 
                        complement));
        addField(
                new StringFieldEditor(
                        COMPLEMENT_SRC_ROOT , 
                        "Root folder of the complement sourcecode:", 
                        complement));
    }

}
