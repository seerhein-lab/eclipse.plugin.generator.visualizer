package com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.dto;
import static org.fest.assertions.Assertions.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.rule.PowerMockRule;

import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.EclipseMock;

/**
 * Run these tests only in Eclipse!
 * Currently no tycho support (don't know how to solve it) 
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest( { ResourcesPlugin.class })
public class ComplementTest {
    
//    @Rule
//    public PowerMockRule rule = new PowerMockRule();
//    
//    static {
//        PowerMockAgent.initializeIfNeeded();
//    }
    final String offsetFile = "resources/com/seitenbau/eclipse/plugin/datenmodell/generator/visualizer/dto/offset";
    EclipseMock mock;
    IFile offset;
    
    @Before
    public void setup() throws Exception {
        mock = new EclipseMock();
        offset = mock.mockFileWithContents(offsetFile);
    }
    
    @Test
    public void srcAsString() throws Exception {
        // prepare
        String expected = FileUtils.readFileToString(new File(offsetFile), "UTF-8");

        // execute
        Complement actual = new Complement(null, offset, null);
        
        // verify
        assertThat(actual.getSrcFileAsString()).isEqualTo(expected);
    }

    @Test
    public void offset() throws Exception {
        //prepare
        Complement complement = new Complement(null, offset, null);
        int expectedLines = 7 + 1;
        int expLength1 = 11;
        int expLength2 = 11;
        int expLength3 = 31;
        int expLength4 = 21;
        int expLength5 = 11;
        int expLength6 = 23;
        int expLength7 = 0;
        
        // execute
        Map<Integer, LineAttrs> actual = complement.getLineMapOfSrcFile();
        
        // verify
        assertThat(actual.keySet()).hasSize(expectedLines);
        for (int l = 0; l < expectedLines; l++) {
            assertThat(actual.containsKey(l)).isTrue();
        }
        
        assertThat(actual.get(0).getOffset()).isEqualTo(0);
        assertThat(actual.get(1).getOffset()).isEqualTo(expLength1);
        assertThat(actual.get(2).getOffset()).isEqualTo(expLength1 + expLength2);
        assertThat(actual.get(3).getOffset()).isEqualTo(expLength1 + expLength2 + expLength3);
        assertThat(actual.get(4).getOffset()).isEqualTo(expLength1 + expLength2 + expLength3 + expLength4);
        assertThat(actual.get(5).getOffset()).isEqualTo(expLength1 + expLength2 + expLength3 + expLength4 + expLength5);
        assertThat(actual.get(6).getOffset()).isEqualTo(expLength1 + expLength2 + expLength3 + expLength4 + expLength5 + expLength6);
        assertThat(actual.get(7).getOffset()).isEqualTo(expLength1 + expLength2 + expLength3 + expLength4 + expLength5 + expLength6 + expLength7);
        
        assertThat(actual.get(2).getLineText()).isEqualTo("123456789012345678901234567890");
        assertThat(actual.get(7).getLineText()).isEqualTo("<<<<< this is the end");
    }

}
