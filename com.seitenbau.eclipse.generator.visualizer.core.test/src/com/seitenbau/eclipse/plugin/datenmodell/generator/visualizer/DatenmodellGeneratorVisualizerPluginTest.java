package com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer;

import static org.junit.Assert.*;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

public class DatenmodellGeneratorVisualizerPluginTest {
    
    DatenmodellGeneratorVisualizerPlugin sut;
    
    @Test
    public void testBundleId() throws Exception {
        
        BundleContext bundleContext = FrameworkUtil.getBundle(getClass()).getBundleContext();
        assertNotNull(bundleContext);
        
        String actual = bundleContext.getBundle().getSymbolicName();
        assertEquals("com.seitenbau.eclipse.generator.visualizer.core", actual);
    }
    
    @Test
    public void testFail() throws Exception {
        fail("not yet implemented");
    }

}
