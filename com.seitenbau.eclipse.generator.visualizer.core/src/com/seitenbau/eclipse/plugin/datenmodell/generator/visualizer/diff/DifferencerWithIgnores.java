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
import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.common.Utilities;

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
                
                InputStream leftInput = null;
                InputStream rightInput = null;
                try {
                    leftInput = left.getContents();
                    rightInput = right.getContents();
                    
                    LineComparator leftLineComparator = new LineComparator(leftInput, Utilities.getCharset(left) );
                    LineComparator rightLineComparator = new LineComparator(rightInput, Utilities.getCharset(right) );
                    
                    RangeDifference[] differences = RangeDifferencer.findDifferences(leftLineComparator, rightLineComparator);
                    
                    if (differences.length == 0) {
                        return true;
                    }
                    
                    for (RangeDifference df : differences) {
                        if (! isDiffIgnorable(left, right, df)) {
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
                } finally {
                    IOUtils.closeQuietly(leftInput);
                    IOUtils.closeQuietly(rightInput);
                }
            }
        }
        // not a resourceNode ... go the normal way...
        return super.contentsEqual(input1, input2);
    }
    
    private boolean isDiffIgnorable(IFile left, IFile right, RangeDifference diff) {
        
        try {
            List<String> linesLeft = IOUtils.readLines(left.getContents(), Utilities.getCharset(left));
            List<String> linesRight = IOUtils.readLines(right.getContents(), Utilities.getCharset(right));
            
            // left
            for (int line = diff.leftStart(); line < diff.leftEnd(); line++) {
                String candidateLeft = linesLeft.get(line);
                if (matches(candidateLeft)) {
                    // ignore line in left
                    continue;
                } else {
                    // found a diff which we can not ignore.
                    return false;
                }
            }
            
            // right
            for (int line = diff.rightStart(); line < diff.rightEnd(); line++) {
                String candidateRight = linesRight.get(line);
                if (matches(candidateRight)) {
                    // ignore line in right
                    continue;
                } else {
                    // found a diff which we can not ignore.
                    return false;
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        // diffs we found should be ignored.
        return true;
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
