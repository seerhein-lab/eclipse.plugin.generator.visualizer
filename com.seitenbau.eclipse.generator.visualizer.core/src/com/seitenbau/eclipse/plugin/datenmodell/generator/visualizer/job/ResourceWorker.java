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
import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.preferences.MainPrefPage;

public class ResourceWorker {
    
    final static String complementGenRoot = DatenmodellGeneratorVisualizerPlugin
            .getDefault()
            .getPreferenceStore()
            .getString(MainPrefPage.COMPLEMENT_GEN_ROOT);
    final static String complementSrcRoot = DatenmodellGeneratorVisualizerPlugin
            .getDefault()
            .getPreferenceStore()
            .getString(MainPrefPage.COMPLEMENT_SRC_ROOT);
    
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
                                IProject project = changedRes.getProject();
                                if (changedRes instanceof IFile) {
                                    MarkerFactory.deleteMarkersInResource(changedRes);
                                    Complement complement = ResourceWorker.findGeneratedComplement((IFile) changedRes, project);
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
    
    public static void scheduleFullWorkspaceScan() {
        // give eclipse a chance to load itself fist..
        fullWorkspaceScanJob.setPriority(Job.DECORATE);
        fullWorkspaceScanJob.setUser(true);
        fullWorkspaceScanJob.schedule(5000);
    }

    private static Job fullWorkspaceScanJob = new FullWorkspaceScan("Full Workspace Scan");
 
    
    /**
     * Finds the Complement to the given source file in the given project. If no complement exists it will return null.
     * @param srcFile
     * @param project
     * @return The Complement to the given source file in the given project. If no complement exists it will return null.
     */
    public static Complement findGeneratedComplement(IFile srcFile, IProject project) {
//        System.out.println(srcFile.getFullPath());
        // Input:
        // /{ProjectName}/src/main/java/{package}/{ClassName}.java
        String[] splitted = StringUtils.split(srcFile.getFullPath().toString(), '/');
        // 0: {ProjectName}
        // 1: src
        // 2: main
        // 3: java
        // 4: {package}
        String[] startingAtPackageRoot = Arrays.copyOfRange(splitted, 4, splitted.length);
        String packageRoot = StringUtils.join(startingAtPackageRoot, '/');
        
        IFolder ungemergtFolder = getGenRootFolderOfProject(project);
        IFile generatedComplement = ungemergtFolder.getFile(packageRoot);
//        System.out.println(generatedComplement.getFullPath());
        if (generatedComplement.exists()) {
            Complement result = new Complement(project, srcFile, generatedComplement);
//            System.out.println(result);
            return result;
        }
        return null; 
    }

}
