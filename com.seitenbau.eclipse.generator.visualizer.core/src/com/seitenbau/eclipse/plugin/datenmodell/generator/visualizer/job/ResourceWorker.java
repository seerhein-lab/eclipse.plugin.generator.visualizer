package com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.job;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
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
    
    private static IFolder getGenRootFolderOfProject(IProject project) {
        IFolder ungemergt = project.getFolder(complementGenRoot);
        return ungemergt;
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
        fullWorkspaceScanJob.schedule();
    }

    private static Job fullWorkspaceScanJob = new Job("Full Workspace Scan") {
        
        @Override
        protected IStatus run(IProgressMonitor monitor) {
            Display.getDefault().asyncExec(new Runnable() {
                
                @Override
                public void run() {
                    try {
                        doItNow();
                        System.out.println("Full Workspace Scan done.");
                    } catch (CoreException e) {
                        e.printStackTrace();
                    }
                }
            });
            return Status.OK_STATUS;
        }
    };
    
    private static void doItNow() throws CoreException {
        Map<String, List<Complement>> candidates = findCandidates();
        for (Entry<String, List<Complement>> toAnnotate : candidates.entrySet()) {
            for (Complement anno : toAnnotate.getValue()) {
                Differ.computeAndRenderAnnotations(anno);
            }
        }
    }
    
    /**
     * Finds all candidates for annotations.
     * @return Map with key = project name.
     * @throws CoreException
     */
    private static Map<String, List<Complement>> findCandidates() throws CoreException {
        Map<String, List<Complement>> result = new HashMap<String, List<Complement>>();
        
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IWorkspaceRoot workspaceRoot = workspace.getRoot();
        // TODO: --> config!
        for (IProject project : workspaceRoot.getProjects()) {
            if (project.isOpen()) {
                List<Complement> complements = scanProject(project);
                result.put(project.getName(), complements);
            }
        }
        return result;
    }
    
    /**
     * Scans a project and finds all files which have a generated complement.
     * 
     * @param project
     * @return
     * @throws CoreException
     */
    private static List<Complement> scanProject(IProject project) throws CoreException {
        List<Complement> complementResult = new ArrayList<Complement>();
        // First of all: clean up
        MarkerFactory.deleteAllMarkers(project);
        
        IFolder ungemegt = getGenRootFolderOfProject(project);
        
        if (ungemegt.exists()) {
            // BINGO, this is a 'datenmodell generator' extended project!

            // mvn default structure
            IFolder srcMainJavaFolder = project.getFolder(complementSrcRoot);
            if (srcMainJavaFolder.exists()) {
                List<IFile> packageScanResult = new ArrayList<IFile>();
                packageScan(srcMainJavaFolder, packageScanResult);
                
                for (IFile res : packageScanResult) {
                    Complement complement = findGeneratedComplement(res, project);
                    if (complement != null) {
                        complementResult.add(complement);
                    }
                }
            }
        }
        return complementResult;
    }

    /**
     * Go recursively through all folders and finds the files.
     * @param parent
     * @param result
     * @throws CoreException
     */
    private static void packageScan(IFolder parent, List<IFile> result) throws CoreException {
        for (IResource member : parent.members()) {
            if (member instanceof IFolder) {
                packageScan((IFolder) member, result);
            } else if (member instanceof IFile) {
                result.add((IFile) member);
            }
        }
    }
    
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
