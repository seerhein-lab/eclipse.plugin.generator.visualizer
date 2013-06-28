package com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.menu;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareUI;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.diff.compare.CompareInput;

public class TreeDiffMenuHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        Shell shell = HandlerUtil.getActiveShell(event);
        ISelection sel = HandlerUtil.getActiveMenuSelection(event);
        IStructuredSelection selection = (IStructuredSelection) sel;

        IWorkbenchPage page = PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getActivePage();

        Object firstElement = selection.getFirstElement();
        
        if (firstElement instanceof IProjectNature) {
            IProjectNature projectNature = (IProjectNature) firstElement;
            IProject project = projectNature.getProject();

            CompareUI.openCompareEditorOnPage(
                    new CompareInput(
                            project, 
                            getCompareConfig(), 
                            "Tree Diff (filtered)"), 
                    page);
        } else if (firstElement instanceof IPackageFragment ) {
            IPackageFragment packageFragement = (IPackageFragment) firstElement;
            IResource resource;
            try {
                resource = packageFragement.getCorrespondingResource();
                
                CompareInput input = new CompareInput(
                        resource, 
                        getCompareConfig(), 
                        "Tree Diff (filtered)");
                if (input.isNothingToCompare()) {
                    MessageDialog.openInformation(shell, "Info", "No generated Complement found for " + resource.getFullPath());
                    return null;
                }
                
                CompareUI.openCompareEditorOnPage(
                        input, 
                        page);
            } catch (JavaModelException e) {
                e.printStackTrace();
            }
        } else {
            MessageDialog.openInformation(shell, "Info", "Please select a Project or a Package.");
        }
        return null;
    }

    private CompareConfiguration getCompareConfig() {
        CompareConfiguration cc = new CompareConfiguration();
        cc.setLeftEditable(false);
        cc.setRightEditable(false);
        cc.setLeftLabel("Source file");
        cc.setRightLabel("Fully generated file");
        cc.setProperty(CompareConfiguration.IGNORE_WHITESPACE, true);
        
        return cc;
    }

}
