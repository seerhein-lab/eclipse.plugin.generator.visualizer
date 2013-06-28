package com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.job;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;

import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.DatenmodellGeneratorVisualizerPlugin;
import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.diff.Differ;
import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.dto.Complement;
import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.marker.MarkerFactory;
import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.preferences.Preferences;

public class ResourceWorker {
    
    final static String complementGenRoot = DatenmodellGeneratorVisualizerPlugin
            .getDefault()
            .getPreferenceStore()
            .getString(Preferences.COMPLEMENT_GEN_ROOT);
    final static String complementSrcRoot = DatenmodellGeneratorVisualizerPlugin
            .getDefault()
            .getPreferenceStore()
            .getString(Preferences.COMPLEMENT_SRC_ROOT);
    
    public static IFolder getGenRootFolderOfProject(IProject project) {
        IFolder ungemergt = project.getFolder(complementGenRoot);
        return ungemergt;
    }
    
    public static IFolder getSrcRootFolderOfProject(IProject project) {
        IFolder src = project.getFolder(complementSrcRoot);
        return src;
    }
    
    public static String getComplementSrcRoot() {
        return complementSrcRoot;
    }
    
    private static List<IResource> toUpdate = null;
    
    public static void scheduleUpdateJob(List<IResource> toUpdate) {
        ResourceWorker.toUpdate = toUpdate;
        updateJob.schedule();
    }
    
    private static Job updateJob = new Job("Update Job") {
        
        @Override
        protected IStatus run(IProgressMonitor monitor) {
            Display.getDefault().asyncExec(new Runnable() {
                
                @Override
                public void run() {
                    try {
                        if (toUpdate != null) {
                            for (IResource changedRes : toUpdate) {
                                if (changedRes instanceof IFile) {
                                    MarkerFactory.deleteMarkersInResource(changedRes);
                                    Complement complement = ResourceWorker.findGeneratedComplement((IFile) changedRes);
//                                    System.out.println(complement);
                                    Differ.computeAndRenderAnnotations(complement);
                                }
                            }
                        }
                    } catch (CoreException e) {
                        e.printStackTrace();
                    } finally {
//                        System.out.println("Update Job done.");
                        toUpdate = null;
                    }
                }
            });
            return Status.OK_STATUS;
        }
    };
    
    public static void scheduleFullWorkspaceScan(int delay) {
        // give eclipse a chance to load itself fist..
        fullWorkspaceScanJob.setPriority(Job.DECORATE);
        fullWorkspaceScanJob.setUser(true);
        fullWorkspaceScanJob.schedule(delay);
    }

    private static Job fullWorkspaceScanJob = new FullWorkspaceScan("DG Visualizer - Full Workspace Scan");
 
    
    /**
     * Finds the Complement to the given source resource. If no complement exists it will return null.
     * @param srcResource
     * @return The Complement to the given source resource. If no complement exists it will return null.
     */
    public static Complement findGeneratedComplement(IResource srcResource) {
//        System.out.println(srcFile.getFullPath());
        // Input:
        // /{ProjectName}/src/main/java/{package}/[{ClassName}.java]
        String[] splitted = StringUtils.split(srcResource.getFullPath().toString(), '/');
        // 0: {ProjectName}
        // 1: src
        // 2: main
        // 3: java
        // 4: {package}
        String[] startingAtPackageRoot = Arrays.copyOfRange(splitted, 4, splitted.length);
        String packagePath = StringUtils.join(startingAtPackageRoot, '/');
        
        IFolder ungemergtFolder = getGenRootFolderOfProject(srcResource.getProject());
        IFile generatedComplementFile = ungemergtFolder.getFile(packagePath);
        if (generatedComplementFile.exists()) {
            Complement result = new Complement(srcResource, generatedComplementFile);
            return result;
        }
        IFolder generatedComplementFolder = ungemergtFolder.getFolder(packagePath);
        if (generatedComplementFolder.exists()) {
            Complement result = new Complement(srcResource, generatedComplementFolder);
            return result;
        }
        
        
        return null; 
    }
    

}
