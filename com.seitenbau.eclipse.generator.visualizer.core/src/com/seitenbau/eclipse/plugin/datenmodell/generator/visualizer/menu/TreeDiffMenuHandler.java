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
import org.eclipse.jdt.core.IPackageFragmentRoot;
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

    private static String COMPARE_TITLE = "Tree Diff (filtered)";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        Shell shell = HandlerUtil.getActiveShell(event);
        ISelection sel = HandlerUtil.getActiveMenuSelection(event);
        IStructuredSelection selection = (IStructuredSelection) sel;

        IWorkbenchPage page = PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getActivePage();
        CompareInput input = null;

        Object firstElement = selection.getFirstElement();
        
        if (firstElement instanceof IProjectNature) /* package explorer */ {
            IProjectNature projectNature = (IProjectNature) firstElement;
            IProject project = projectNature.getProject();
            input = new CompareInput(project, getCompareConfig(), COMPARE_TITLE);
        } else if (firstElement instanceof IProject) /* navigator */ {
            IProject project = (IProject) firstElement;
            input = new CompareInput(project, getCompareConfig(), COMPARE_TITLE);
        } else if (firstElement instanceof IPackageFragment) /* package explorer */ {
            IPackageFragment packageFragement = (IPackageFragment) firstElement;
            try {
                IResource resource = packageFragement.getCorrespondingResource();
                input = new CompareInput(resource, getCompareConfig(), COMPARE_TITLE);
            } catch (JavaModelException e) {
                e.printStackTrace();
            }
        } else if (firstElement instanceof IPackageFragmentRoot) /* package explorer */ {
            IPackageFragmentRoot packageFragmentRoot = (IPackageFragmentRoot) firstElement;
            try {
                IResource resource = packageFragmentRoot.getCorrespondingResource();
                input = new CompareInput(resource, getCompareConfig(), COMPARE_TITLE );
            } catch (JavaModelException e) {
                e.printStackTrace();
            }
        } else if (firstElement instanceof IResource) /* navigator */ {
            IResource resource = (IResource) firstElement;
            input = new CompareInput(resource, getCompareConfig(), COMPARE_TITLE);
        } else {
            MessageDialog.openInformation(
                    shell, 
                    "Info", 
                    "Please select a Project or a Resource (Source-Folder / Package).");
            return null;
        }
        
        if (input.hasError()) {
            MessageDialog.openInformation(shell, "Info", input.getError());
            return null;
        }
        CompareUI.openCompareEditorOnPage(input, page);
        
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
