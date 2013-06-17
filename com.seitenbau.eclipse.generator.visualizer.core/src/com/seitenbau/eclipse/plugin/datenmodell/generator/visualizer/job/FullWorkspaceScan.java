package com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.diff.Differ;
import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.dto.Complement;
import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.marker.MarkerFactory;

public class FullWorkspaceScan extends Job {
    
    static IProgressMonitor myMonitor = null;
    
    static int progress = 0;
    
    static int totalWork = 100;

    public FullWorkspaceScan(String jobName) {
        super(jobName);
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        FullWorkspaceScan.myMonitor = monitor;
        progress = 0;
        
        monitor.beginTask("Datenmodell Generator Visualizer - Full Workspace Scan", totalWork);
        
        try {
            doItNow();
            System.out.println("Full Workspace Scan done.");
        } catch (CoreException e) {
            e.printStackTrace();
        }
        monitor.done();
        return Status.OK_STATUS;
    }
    
    public static void doItNow() throws CoreException {
        Map<String, List<Complement>> candidates = findCandidates();
        // monitor update
        int remainig = totalWork - progress;
        int incStep = remainig / candidates.size();

        for (Entry<String, List<Complement>> toAnnotate : candidates.entrySet()) {
            myMonitor.subTask("Datenmodell Generator Visualizer - Adding Markers and Annotations to Project " + toAnnotate.getKey());
            progress += incStep;
            myMonitor.worked(progress);
            System.out.println(progress);
            
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
        myMonitor.subTask("Looking for candidates ...");
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
        progress = 10;
        myMonitor.worked(progress);
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
        
        IFolder ungemegt = ResourceWorker.getGenRootFolderOfProject(project);
        
        if (ungemegt.exists()) {
            // BINGO, this is a 'datenmodell generator' extended project!

            // mvn default structure
            IFolder srcMainJavaFolder = project.getFolder(ResourceWorker.getComplementSrcRoot());
            if (srcMainJavaFolder.exists()) {
                List<IFile> packageScanResult = new ArrayList<IFile>();
                packageScan(srcMainJavaFolder, packageScanResult);
                
                for (IFile res : packageScanResult) {
                    Complement complement = ResourceWorker.findGeneratedComplement(res, project);
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

}
