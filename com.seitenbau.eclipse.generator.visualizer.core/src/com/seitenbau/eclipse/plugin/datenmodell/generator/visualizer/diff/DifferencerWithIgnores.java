package com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.diff;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.eclipse.compare.ResourceNode;
import org.eclipse.compare.rangedifferencer.RangeDifference;
import org.eclipse.compare.rangedifferencer.RangeDifferencer;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.core.resources.IFile;

import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.LineComparator;
import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.common.Constants;

public class DifferencerWithIgnores extends Differencer {
    
    @Override
    protected boolean contentsEqual(Object input1, Object input2) {
        if (input1 instanceof ResourceNode && input2 instanceof ResourceNode) {
            ResourceNode resLeft = (ResourceNode) input1;
            ResourceNode resRight = (ResourceNode) input2;
            if (resLeft.getResource() instanceof IFile && resRight.getResource() instanceof IFile) {
                IFile left = (IFile) resLeft.getResource();
                IFile right = (IFile) resRight.getResource();
                
                try {
                    InputStream leftInput = left.getContents();
                    InputStream rightInput = right.getContents();
                    
                    LineComparator leftLineComparator = new LineComparator(leftInput, Constants.UTF_8 );
                    LineComparator rightLineComparator = new LineComparator(rightInput, Constants.UTF_8 );
                    
                    RangeDifference[] differences = RangeDifferencer.findDifferences(leftLineComparator, rightLineComparator);
                    
                    if (differences.length == 0) {
                        return true;
                    }
                    
                    if (differences.length == 1) {
                        if (checkSvnIdLine(left, differences[0])) {
                            return true;
                        }
                        return false;
                    }
                    
                    // diffs > 1
                    return false;
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return super.contentsEqual(input1, input2);
    }
    
    private boolean checkSvnIdLine(IFile file, RangeDifference diff) {
        
        if (diff.leftLength() > 1) {
            // SVN ID is only one line!
            return false;
        }
        
        try {
            InputStream contents = file.getContents();
            List<String> lines = IOUtils.readLines(contents, Constants.UTF_8);
            
            String candidate = lines.get(diff.leftStart());
            
            String regexpSvnId = ".*\\$Id:.*";
            if (candidate != null && candidate.matches(regexpSvnId) ) {
//                System.out.println("BINGO: SVN ID: " + candidate + " <-- " + file.getName());
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

}
