package com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.diff.compare;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.ResourceNode;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;

import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.diff.DifferencerWithIgnores;
import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.dto.Complement;
import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.job.ResourceWorker;
import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.preferences.Preferences;

/**
 * 
 * @see http://wiki.eclipse.org/FAQ_How_do_I_create_a_compare_editor%3F
 * 
 */
public class CompareInput extends CompareEditorInput {

    private Complement complement;

    private IProject project;

    private ResourceNode left;

    private ResourceNode right;
    
    private static CompareConfiguration config = new CompareConfiguration();
    
    static {
        // TODO: save
        config.setLeftEditable(true);
        config.setLeftLabel("src file");
        config.setRightLabel("fully generated file");
        // it is important to NOT ignore whitespaces in order to jump 
        // to the correct diff at startup of the compare view.
        config.setProperty(CompareConfiguration.IGNORE_WHITESPACE, false);
    }
    
    public CompareInput(Complement toCompare) {
        super(config);
        this.complement = toCompare;
        setTitle("Complement Compare View");
    }
    
    public CompareInput(IProject project) {
        super(config);
        this.project = project;
    }

    @Override
    protected Object prepareInput(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        
        return decideWhatToCompare(monitor);
    }
    
    private Object decideWhatToCompare(IProgressMonitor monitor) {
        if (this.complement != null) {
            System.out.println("Comparing two files");
            this.left = new ResourceNode((IResource) complement.getSrcFile());
            this.right = new ResourceNode((IResource) complement.getGeneratedFile());
            Differencer d= new Differencer();
            Object differences = d.findDifferences(false, monitor, null, null, left, right);
            return differences;
        } 
        
        if (this.project != null) {
            System.out.println("Comparing the whole project");
            
            IFolder src = ResourceWorker.getSrcRootFolderOfProject(this.project);
            IFolder gen = ResourceWorker.getGenRootFolderOfProject(this.project);
            
            this.left = new ResourceNode((IResource) src);
            this.right = new ResourceNode((IResource) gen);
            
            String[] ignores = Preferences.getPreferenceForIgnores();
            DifferencerWithIgnores d= new DifferencerWithIgnores(ignores);
            Object differences = d.findDifferences(false, monitor, null, null, left, right);
            return differences;
        }
        return null;
    }

}
