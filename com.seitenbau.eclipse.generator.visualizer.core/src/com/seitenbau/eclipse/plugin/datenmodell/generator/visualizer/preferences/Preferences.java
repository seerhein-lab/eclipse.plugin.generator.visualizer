package com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.preferences;

import java.util.StringTokenizer;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.DatenmodellGeneratorVisualizerPlugin;
import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.common.Constants;

/**
 * 
 * Table:
 * @see org.eclipse.ui.internal.dialogs.FileEditorsPreferencePage.class
 * @see org.eclipse.team.internal.ui.preferences.IgnorePreferencePage.class
 *
 */
public class Preferences extends PreferencePage implements IWorkbenchPreferencePage, Listener {
    
    public static final String PLUGIN_ACTIVE_SAVE = Constants.PLUGIN_NS_PREFIX + ".prefs.pluginActiveSave";
    public static final String PLUGIN_ACTIVE_STARTUP = Constants.PLUGIN_NS_PREFIX + ".prefs.pluginActiveStartup";
    public static final String COMPLEMENT_GEN_ROOT = Constants.PLUGIN_NS_PREFIX + ".prefs.complement.gen.folder";
    public static final String COMPLEMENT_SRC_ROOT = Constants.PLUGIN_NS_PREFIX + ".prefs.complement.src.folder";
    
    public static final String IGNORE_PREFERENCE = Constants.PLUGIN_NS_PREFIX + ".prefs.ignore.list";
    public static final String PREFERENCE_DELIMITER = "<#>";
    
    private Label ignoreLabel;
    private Table ignoreTable;
    private Composite groupComponent;
    private GridLayout groupLayout;
    private Button addIgnoreButton;
    private Button removeIgnoreButton;
    private Button activeAtStartup;
    private Button activeAtSave;
    private StringFieldEditor fullyGenRootFolder;
    private StringFieldEditor rootFolderMerged;
    
    @Override
    public void init(IWorkbench workbench) {
        setPreferenceStore(DatenmodellGeneratorVisualizerPlugin.getDefault().getPreferenceStore());
        setDescription("Configuration of your Datenmodell Generator Visualizer Plugin.");
    }

    /**
     * Creates the page's UI content.
     */
    protected Control createContents(Composite parent) {
        // define container & its gridding
        Composite pageComponent = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        pageComponent.setLayout(layout);
        GridData data = new GridData();
        data.verticalAlignment = GridData.FILL;
        data.horizontalAlignment = GridData.FILL;
        pageComponent.setLayoutData(data);

        //layout the contents
        
        // CB activation of plugin
        activeAtStartup = new Button(pageComponent, SWT.CHECK);
        activeAtStartup.setText("&Run after eclipse startup (full workspace)");
        data = new GridData(SWT.FILL, SWT.NONE, true, false);
        data.horizontalSpan = 2;
        activeAtStartup.setLayoutData(data);

        activeAtSave = new Button(pageComponent, SWT.CHECK);
        activeAtSave.setText("&Remark after saving resources (only changed resources)");
        data = new GridData(SWT.FILL, SWT.NONE, true, false);
        data.horizontalSpan = 2;
        activeAtSave.setLayoutData(data);
        
        // complement settings
        Group complementGroup = new Group(pageComponent, SWT.NONE);
        complementGroup.setText("Complement Settings");
        data = new GridData(SWT.FILL, SWT.NONE, true, false);
        data.horizontalSpan = 2;
        complementGroup.setLayoutData(data);
        
        fullyGenRootFolder = new StringFieldEditor(
                    COMPLEMENT_GEN_ROOT , 
                    "Fully generated root folder:", 
                    complementGroup);
        rootFolderMerged = new StringFieldEditor(
                    COMPLEMENT_SRC_ROOT , 
                    "Root folder of merged sources:", 
                    complementGroup);
        
        // layout the ignore table & its buttons
        ignoreLabel = new Label(pageComponent, SWT.LEFT);
        ignoreLabel.setText("&Patterns to ignore in tree diff (Regexp)");
        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.horizontalSpan = 2;
        ignoreLabel.setLayoutData(data);

        ignoreTable = new Table(pageComponent, SWT.MULTI | SWT.BORDER);
        ignoreTable.addListener(SWT.Selection, this);
        ignoreTable.addListener(SWT.DefaultSelection, this);
        data = new GridData(GridData.FILL_BOTH);
        data.heightHint = ignoreTable.getItemHeight() * 7;
        ignoreTable.setLayoutData(data);

        groupComponent = new Composite(pageComponent, SWT.NULL);
        groupLayout = new GridLayout();
        groupLayout.marginWidth = 0;
        groupLayout.marginHeight = 0;
        groupComponent.setLayout(groupLayout);
        data = new GridData();
        data.verticalAlignment = GridData.FILL;
        data.horizontalAlignment = GridData.FILL;
        groupComponent.setLayoutData(data);

        addIgnoreButton = new Button(groupComponent, SWT.PUSH);
        addIgnoreButton.setText("Add Expression");
        addIgnoreButton.addListener(SWT.Selection, this);
        addIgnoreButton.setLayoutData(data);
        setButtonLayoutData(addIgnoreButton);

        removeIgnoreButton = new Button(groupComponent, SWT.PUSH);
        removeIgnoreButton.setText("Remove Expression"); 
        removeIgnoreButton.addListener(SWT.Selection, this);
        setButtonLayoutData(removeIgnoreButton);

        fillFields();
        fillIgnoreTable();
        applyDialogFont(pageComponent);

        return pageComponent;
        
    }

    private void fillFields() {
        activeAtStartup.setSelection(getValOrDefaultAsFallback4Boolean(PLUGIN_ACTIVE_STARTUP));
        activeAtSave.setSelection(getValOrDefaultAsFallback4Boolean(PLUGIN_ACTIVE_SAVE));
        
        fullyGenRootFolder.setStringValue(getValOrDefaultAsFallback4String(COMPLEMENT_GEN_ROOT));
        rootFolderMerged.setStringValue(getValOrDefaultAsFallback4String(COMPLEMENT_SRC_ROOT));
        
    }
    
    private String getValOrDefaultAsFallback4String(String prefKey) {
        IPreferenceStore store = getPreferenceStore();
        if (store.contains(prefKey)) {
            return store.getString(prefKey);
        } else {
            return store.getDefaultString(prefKey);
        }
    }
    
    private boolean getValOrDefaultAsFallback4Boolean(String prefKey) {
        IPreferenceStore store = getPreferenceStore();
        if (store.contains(prefKey)) {
            return store.getBoolean(prefKey);
        } else {
            return store.getDefaultBoolean(prefKey);
        }
    }

    private void fillIgnoreTable() {
        ignoreTable.removeAll();
        String[] ignoresPreference = getPreferenceForIgnores();
        for (String ignore : ignoresPreference) {
            TableItem item = new TableItem(ignoreTable, SWT.NULL);
            item.setText(ignore);
        }
        removeIgnoreButton.setEnabled(false);
    }

    @Override
    public void handleEvent(Event event) {
        if (event.widget == addIgnoreButton) {
            promptForIgnoreRegexp();
        } else if (event.widget == removeIgnoreButton) {
            removeIgnore();
        } else if (event.widget == ignoreTable) {
            handleSelection();
        }
    }

    private void promptForIgnoreRegexp() {
        IgnoreInputDialog dialog = new IgnoreInputDialog(
                getControl().getShell(), 
                "Add Ignore Regexp",
                "message",
                "init",
                null);
        if (dialog.open() == Window.OK) {
            String value = dialog.getValue();
            addIgnoreRegexp(value);
        }
        
    }

    private void addIgnoreRegexp(String value) {
        // Check if the item already exists
        TableItem[] items = ignoreTable.getItems();
        for (int i = 0; i < items.length; i++) {
            if (items[i].getText().equals(value)) {
                MessageDialog.openWarning(
                        getShell(), 
                        "Pattern exists already", 
                        "Your Pattern is already in the ignore list.");
                return;
            }
        }
        TableItem item = new TableItem(ignoreTable, SWT.NONE);
        item.setText(value);
    }
    
    private void removeIgnore() {
        int[] selection = ignoreTable.getSelectionIndices();
        ignoreTable.remove(selection);
    }
    
    private void handleSelection() {
        if (ignoreTable.getSelectionCount() > 0) {
            removeIgnoreButton.setEnabled(true);
        } else {
            removeIgnoreButton.setEnabled(false);
        }
    }
    
    @Override
    protected void performDefaults() {
        super.performDefaults();
        
        IPreferenceStore store = getPreferenceStore();
        
        activeAtStartup.setSelection(store.getDefaultBoolean(PLUGIN_ACTIVE_STARTUP));
        activeAtSave.setSelection(store.getDefaultBoolean(PLUGIN_ACTIVE_SAVE));
        
        fullyGenRootFolder.setStringValue(store.getDefaultString(COMPLEMENT_GEN_ROOT));
        rootFolderMerged.setStringValue(store.getDefaultString(COMPLEMENT_SRC_ROOT));
        
        ignoreTable.removeAll();
        String[] ignore = getDefaultPreferenceForIgnores();
        for (String val : ignore) {
            TableItem item = new TableItem(ignoreTable, SWT.NONE);
            item.setText(val);
        }
    }
    
    @Override
    protected void performApply() {
        storeValues();
        super.performApply();
    }
    
    @Override
    public boolean performOk() {
        storeValues();
        return super.performOk();
    }

    private void storeValues() {
        IPreferenceStore store = getPreferenceStore();
        
        store.setValue(COMPLEMENT_SRC_ROOT, rootFolderMerged.getStringValue());
        store.setValue(COMPLEMENT_GEN_ROOT, fullyGenRootFolder.getStringValue());
        
        store.setValue(PLUGIN_ACTIVE_STARTUP, activeAtStartup.getSelection());
        store.setValue(PLUGIN_ACTIVE_SAVE, activeAtSave.getSelection());
        
        // store ignore list
        TableItem[] items = ignoreTable.getItems();
        String[] elements = new String[items.length];
        for (int ti = 0; ti < items.length; ti++) {
            elements[ti] = items[ti].getText();
        }
        storePreferenceForIgnores(elements);
    }
    
    /**
     * Return the bad words preference default.
     */
    public String[] getDefaultPreferenceForIgnores(){
        return convert(getPreferenceStore().getDefaultString(IGNORE_PREFERENCE));
    }

    /**
     * Returns the bad words preference.
     */
    public static String[] getPreferenceForIgnores() {
        IPreferenceStore store = DatenmodellGeneratorVisualizerPlugin.getDefault().getPreferenceStore();
        return convert(store.getString(IGNORE_PREFERENCE));
    }
        
    /**
     * http://www.eclipse.org/articles/article.php?file=Article-Preferences/article.html
     * Converts PREFERENCE_DELIMITER delimited String to a String array.
     */
    private static String[] convert(String preferenceValue) {
        StringTokenizer tokenizer = new StringTokenizer(preferenceValue, PREFERENCE_DELIMITER);
        int tokenCount = tokenizer.countTokens();
        String[] elements = new String[tokenCount];
        for (int i = 0; i < tokenCount; i++) {
            elements[i] = tokenizer.nextToken();
        }

        return elements;
    }

    /**
     * Sets the bad words preference.
     */
    public void storePreferenceForIgnores(String[] elements) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < elements.length; i++) {
            buffer.append(elements[i]);
            buffer.append(PREFERENCE_DELIMITER);
        }
        getPreferenceStore().setValue(IGNORE_PREFERENCE, buffer.toString());
    }

}
