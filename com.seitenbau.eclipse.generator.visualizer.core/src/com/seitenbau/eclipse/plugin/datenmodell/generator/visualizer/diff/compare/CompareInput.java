package com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.diff.compare;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.dto.Complement;

/**
 * 
 * @see http://wiki.eclipse.org/FAQ_How_do_I_create_a_compare_editor%3F
 * 
 */
public class CompareInput extends CompareEditorInput {

    private Complement complement;
    
    private static CompareConfiguration config = new CompareConfiguration();
    
    {
        // TODO: editable, java syntax coloring, ...
        config.setLeftEditable(true);
    }
    
    public CompareInput(Complement toCompare) {
        super(config);
        this.complement = toCompare;
    }

    @Override
    protected Object prepareInput(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        try {
            CompareItem left = new CompareItem("Left", complement.getSrcFileAsString());
            CompareItem right = new CompareItem("Right", complement.getGeneratedFileAsString());
            return new DiffNode(null, Differencer.CHANGE, null, left, right);
        } catch (CoreException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
