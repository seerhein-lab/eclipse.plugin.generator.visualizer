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

/**
 * This is a special Differencer.
 * He will ignore changed lines which matches the given pattern(s).
 */
public class DifferencerWithIgnores extends Differencer {
    
    /** array of ignore patterns. */
    private String[] ignores;
    
    /**
     * A Differencer who will ignore changed lines which matches the given pattern(s).
     * @param ignores patterns (regexp) to ignore.
     */
    public DifferencerWithIgnores(String[] ignores) {
        this.ignores = ignores;
    }

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
                    
                    for (RangeDifference df : differences) {
                        if (! checkDiffIsAIgnoreLine(left, right, df)) {
                            // we found a diff which isn't on our ignore list.
//                            System.out.println("I can't ignore " + left.getName() + " because of diff " + df);
                            return false;
                        }
                    }
                    // we found diffs, but all these diffs are on our ignore list.
//                    System.out.println("All lines with diff in file mathes the ignores: " + left.getName());
                    return true;
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        // not a resourceNode ... go the normal way...
        return super.contentsEqual(input1, input2);
    }
    
    private boolean checkDiffIsAIgnoreLine(IFile left, IFile right, RangeDifference diff) {
        
        if (diff.leftLength() > 1 || diff.rightLength() > 1) {
            // only one line is supported by now.
            return false;
        }
        
        try {
            List<String> linesLeft = IOUtils.readLines(left.getContents(), Constants.UTF_8);
            List<String> linesRight = IOUtils.readLines(right.getContents(), Constants.UTF_8);
            
            String candidateLeft = linesLeft.get(diff.leftStart());
            String candidateRight = linesRight.get(diff.rightStart());
            
            if (matches(candidateLeft)) {
                // ignore line in left
                return true;
            }
            if (matches(candidateRight)) {
                // ignore line in right
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    private boolean matches(String candidate) {
        String regexp = getIgnoresAsRegexp();
        if (candidate != null && candidate.matches(regexp) ) {
//            System.out.println("!BINGO! Ignoring because of line: '" + candidate + "'");
            return true;
        }
        return false;
    }
    
    private String getIgnoresAsRegexp() {
        StringBuilder regexp = new StringBuilder();
        for (String ignore : ignores) {
            regexp.append("(");
            regexp.append(ignore);
            regexp.append(")|");
        }
        if (regexp.length() > 0) {
            // cut off last '|'
            regexp.setLength(regexp.length() - 1);
        }
        String result = regexp.toString();
//        System.out.println(result);
        return result;
        
    }

}
