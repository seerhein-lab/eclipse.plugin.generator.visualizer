package com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.menu;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.job.ResourceWorker;

public class ResetHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ResourceWorker.scheduleFullWorkspaceScan(0);
        return null;
    }

}
