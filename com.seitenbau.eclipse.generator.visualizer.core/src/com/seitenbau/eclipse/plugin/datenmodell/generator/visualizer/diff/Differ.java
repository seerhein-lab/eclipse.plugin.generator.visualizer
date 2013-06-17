package com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.diff;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.compare.contentmergeviewer.TokenComparator;
import org.eclipse.compare.rangedifferencer.RangeDifference;
import org.eclipse.compare.rangedifferencer.RangeDifferencer;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.Position;

import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.DatenmodellGeneratorVisualizerPlugin;
import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.LineComparator;
import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.common.Constants;
import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.dto.Complement;
import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.dto.LineAttrs;
import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.marker.MarkerFactory;
import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.marker.MarkerFactory.MarkerType;

public class Differ {
    
    public static void computeAndRenderAnnotations(Complement complement) {
        try {
            RangeDifference[] compareOnLevelLine = compareOnLevelLine(complement);

            Map<Integer, LineAttrs> lines = complement.getLineMapOfSrcFile();
//            System.out.println(printLineOffset(lines));
            /*
             * visited lines:
             * 1: these lines have annotations
             * 0: no annotations --> these are generated lines
             */
            Integer[] visitedLines = new Integer[lines.keySet().size()];
            Arrays.fill(visitedLines, new Integer(0));
            
            // mark changes
            for (int i = 0; i < compareOnLevelLine.length; i++) {
                RangeDifference crt = compareOnLevelLine[i];

                if (! lines.containsKey(crt.leftStart())) {
                    // doesn't know these line.
                    continue;
                }
                if (lines.containsKey(crt.leftStart())) {
                    if (lines.get(crt.leftStart()).isDoNotMarkMe()) {
                        continue;
                    }
                }
                Position position = posByLineNumber(lines, crt.leftStart(), crt.leftEnd());
                
                MarkerType markerType = MarkerType.getByDiff(crt);
                MarkerFactory.createMarkerByTextSelection(
                        complement, 
                        markerType, 
                        position, 
                        crt.toString());
                
                for (int v = crt.leftStart(); v <= (crt.leftEnd() - 1); v++) {
                    visitedLines[v] = 1;
                }
            }
            // last line is only a helper line
            visitedLines[visitedLines.length - 1] = -1;
            
            // mark generated
            for (int lineStart = 0; lineStart < visitedLines.length; lineStart++) {
                if (visitedLines[lineStart] == 0) {
                    // start gen annotation
                    int lineEnd = lineStart + 1;
                    while (lineEnd < visitedLines.length && visitedLines[lineEnd] == 0) {
                        lineEnd++;
                    }
                    // end gen
                    Position position = posByLineNumber(lines, lineStart, lineEnd);
                    MarkerFactory.createMarkerByTextSelection(
                            complement, 
                            MarkerType.GENERATED, 
                            position, 
                            (lineStart + 1) + " -> " + (lineEnd + 1));
                    lineStart = lineEnd;
                }
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }
    
    private static Position posByLineNumber(Map<Integer, LineAttrs> lines, int lineStart, int lineEnd) {
        LineAttrs lineStartOffset = lines.get(lineStart);
        
        LineAttrs lineEndOffset = lines.get(lineEnd);
        
        Position position = new Position(lineStartOffset.getOffset());
        if (lineEndOffset != null) {
            int length = lineEndOffset.getOffset() - lineStartOffset.getOffset();
            position.setLength(length);
        } else {
            System.err.println("Ui!");
            System.err.println("lines: " + lineStart + " -> " + lineEnd);
            System.err.println(printLineOffset(lines));
        }
        return position;
    }
    
    private static String printLineOffset(Map<Integer, LineAttrs> lines) {
        int l = 0;
        String txt = "";
        while (lines.containsKey(l)) {
            LineAttrs lineOffset = lines.get(l);
            String lineText = lineOffset.getLineText();
            String toConsole = StringUtils.replace(lineText, "\n", "¶");
            txt +=
                    StringUtils.leftPad(l + "", 2) 
                    + " << " +  StringUtils.leftPad(lineOffset.getOffset() + "", 3) 
                    + ": " + toConsole
                    + "\n";
            l++;
        }
        return txt;
    }
    
    private static RangeDifference[] compareOnLevelLine(Complement toCompare) {
        
        RangeDifference[] differences = null;
        try {
            InputStream leftInput = toCompare.getSrcFile().getContents();
            InputStream rightInput = toCompare.getGeneratedFile().getContents();
            LineComparator leftLineComparator = new LineComparator(leftInput, Constants.UTF_8 );
            LineComparator rightLineComparator = new LineComparator(rightInput, Constants.UTF_8 );
            
            differences = RangeDifferencer.findDifferences(leftLineComparator, rightLineComparator);
        } catch (CoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        toCompare.setDiffs(differences);
        return differences;
    }
    
    @Deprecated
    private static List<Position> compareOnLevelToken(String leftAkaHumanInput, String rightAkaGeneratorInput) {

        // create token comparators of input
        TokenComparator leftTokenComparator = new TokenComparator(leftAkaHumanInput);
        TokenComparator rightTokenComparator = new TokenComparator(rightAkaGeneratorInput);
        // find the differences
        RangeDifference[] differences = RangeDifferencer.findDifferences(leftTokenComparator, rightTokenComparator);
        System.out.println(Arrays.toString(differences));
        
        // we only want to display changes in the source file, so only in left file.
        List<Position> leftDiff = new ArrayList<Position>(differences.length);
        
        for (int i = 0; i < differences.length; i++) {
            int leftStart = differences[i].leftStart();
            int leftEnd = differences[i].leftEnd();
            int tokenStart = leftTokenComparator.getTokenStart(leftStart);
            int tokenEnd = leftTokenComparator.getTokenStart(leftEnd);
            
            String diff = leftAkaHumanInput.substring(tokenStart, tokenEnd);
            System.out.println(differences[i]);
            System.out.println("DIFF:");
            System.out.println("'"+diff+"'");
            leftDiff.add(new Position(tokenStart, tokenEnd - tokenStart));
        }
        
        return leftDiff;
    }

}
