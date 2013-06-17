package com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.dto;


/**
 * Eine Line im Kontext des gesamten Textes.
 */
public class LineAttrs {
    
    private int offset;
    
    private String lineText;
    
    public LineAttrs(int offset, String lineText) {
        super();
        this.offset = offset;
        this.lineText = lineText;
    }
    
    /**
     * @return offset to complete text start.
     */
    public int getOffset() {
        return offset;
    }
    
    /**
     * @return text of line.
     */
    public String getLineText() {
        return lineText;
    }
    
    @Override
    public String toString() {
        return offset + " >> " + lineText;
    }

}
