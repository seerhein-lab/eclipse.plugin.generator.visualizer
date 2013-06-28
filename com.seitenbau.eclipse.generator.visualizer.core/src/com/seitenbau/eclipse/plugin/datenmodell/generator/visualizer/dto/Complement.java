package com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.dto;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.eclipse.compare.rangedifferencer.RangeDifference;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.common.Utilities;

/**
 * Holds the source file and the generated file.
 */
public class Complement {
    
    private IResource srcResource;
    
    private IResource genResource;
    
    private RangeDifference[] diffs;

    public Complement(IResource srcResource, IResource genResource) {
        this.srcResource = srcResource;
        this.genResource = genResource;
    }
    
    /**
     * Typically in /{ProjectName}/src/main/java/{package}/{ClassName}.java
     * @return The source file or null.
     */
    public IFile getSrcFile() {
        if (this.srcResource instanceof IFile) {
            return (IFile) srcResource;
        }
        return null;
    }
    
    public IResource getSrcResource() {
        return srcResource;
    }
    
    /**
     * Typically in /{ProjectName}/src/ungemergtes-generat/java/{package}/{ClassName}.java
     * @return The generated file or null.
     */
    public IFile getGenFile() {
        if (this.genResource instanceof IFile) {
            return (IFile) genResource;
        }
        return null;
    }
    
    public IResource getGenResource() {
        return genResource;
    }
    
    public String getSrcFileAsString() throws CoreException, IOException {
        if (! (this.srcResource instanceof IFile)) {
            return null;
        }
        StringWriter writer = new StringWriter();
        IFile file = (IFile) this.srcResource;
        IOUtils.copy(file.getContents(), writer, Utilities.getCharset(file));
        
        return writer.toString();
    }
    
    public String getGeneratedFileAsString() throws IOException, CoreException {
        if (! (this.genResource instanceof IFile)) {
            return null;
        }
        StringWriter writer = new StringWriter();
        IFile file = (IFile) this.genResource;
        IOUtils.copy(file.getContents(), writer, Utilities.getCharset(file));
        
        return writer.toString();
    }
    
    public void setDiffs(RangeDifference[] diffs) {
        this.diffs = diffs;
    }
    
    /**
     * @return the computed differences between the source file and the generated file.
     */
    public RangeDifference[] getDiffs() {
        return diffs;
    }
    
    /**
     * @return Key: line number.
     */
    public Map<Integer, LineAttrs> getLineMapOfSrcFile() throws CoreException, IOException {
        Map<Integer, LineAttrs> result = new HashMap<Integer, LineAttrs>();
        
        String regexpSvnId = ".*\\$Id:.*";
        
        String regexp = "(.*)((\r\n)|(\r)|(\n))";
        String srcFileAsString = getSrcFileAsString();
        Matcher matcher = Pattern.compile(regexp).matcher(srcFileAsString);
        
        Integer lineNo = 0;
        int offset = 0;
        while(matcher.find()) {
            String lineText = matcher.group(1);
            LineAttrs crtLine = new LineAttrs(offset, lineText);
            if (lineText != null && lineText.matches(regexpSvnId) ) {
                crtLine.setDoNotMarkMe(true);
            }
            result.put(lineNo, crtLine);
            if (lineText != null) {
                offset += lineText.length() + matcher.group(2).length();
            }
            lineNo++;
        }
        
        handleLastLine(result, srcFileAsString, lineNo, offset);
        
        return result;
    }

    /**
     * Regexp found all lines expect of last line (dosn't end with '\n') so find it here:
     */
    private void handleLastLine(
            Map<Integer, LineAttrs> result,
            String srcFileAsString, 
            Integer lineNo, 
            int offset) {
        int length = srcFileAsString.length();
        
        Stack<Character> lastLineStack = new Stack<Character>();
        while (length >= 0) {
            char crtChar = srcFileAsString.charAt(length - 1);
            if (crtChar == '\n' || crtChar == '\r') {
                // job done
                break;
            }
            lastLineStack.push(crtChar);
            length--;
        }
        
        String lastLine = "";
        while (!lastLineStack.isEmpty()) {
            lastLine += lastLineStack.pop();
        }
        
        // last line
        result.put(lineNo, new LineAttrs(offset, lastLine));
        // offset of last line
        result.put(++lineNo, new LineAttrs(offset + lastLine.length(), "<<<<< this is the end"));
    }
    
    @Override
    public String toString() {
        return srcResource.getFullPath() + " <> " + genResource.getFullPath();
    }
}