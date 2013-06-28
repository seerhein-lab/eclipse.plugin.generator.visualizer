package com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.diff.compare;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.ResourceNode;
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

    private IProject project;
    
    private IResource resource;
    
    private String error = null;
    
    public CompareInput(IProject project, CompareConfiguration cc, String viewTitle) {
        super(new CompareConfiguration());
        this.project = project;
        setTitle(viewTitle);
    }
    
    public CompareInput(IResource resource, CompareConfiguration cc, String viewTitle) {
        super(new CompareConfiguration());
        this.resource = resource;
        Complement complement = ResourceWorker.findGeneratedComplement(resource);
        if (complement == null) {
            error = "No generated Complement found for " + resource.getFullPath();
        }
        setTitle(viewTitle);
    }

    @Override
    protected Object prepareInput(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        
        return decideWhatToCompare(monitor);
    }
    
    private Object decideWhatToCompare(IProgressMonitor monitor) {
        
        if (this.project != null) {
            System.out.println("Comparing the whole project");
            
            IFolder src = ResourceWorker.getSrcRootFolderOfProject(this.project);
            IFolder gen = ResourceWorker.getGenRootFolderOfProject(this.project);
            
            ResourceNode left = new ResourceNode((IResource) src);
            ResourceNode right = new ResourceNode((IResource) gen);
            
            Object differences = filteredDiff(left, right, monitor);
            return differences;
        }
        
        if (this.resource != null) {
            System.out.println("Comparing everything under '" + this.resource.getFullPath() + "'");
            Complement complement = ResourceWorker.findGeneratedComplement(resource);
            if (complement == null) {
                return null;
            }

            ResourceNode left = new ResourceNode((IResource) complement.getSrcResource());
            ResourceNode right = new ResourceNode((IResource) complement.getGenResource());
            
            Object differences = filteredDiff(left, right, monitor);
            return differences;
        }
        return null;
    }
    
    private Object filteredDiff(ResourceNode left, ResourceNode right, IProgressMonitor monitor) {
        String[] ignores = Preferences.getPreferenceForIgnores();
        DifferencerWithIgnores d= new DifferencerWithIgnores(ignores);
        Object differences = d.findDifferences(false, monitor, null, null, left, right);
        return differences;
    }
    
    public String getError() {
        return error;
    }
    
    public boolean hasError() {
        return StringUtils.isNotBlank(error);
    }

}
