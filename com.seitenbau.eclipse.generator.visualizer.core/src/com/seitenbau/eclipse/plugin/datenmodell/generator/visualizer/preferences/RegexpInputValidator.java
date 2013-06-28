package com.seitenbau.eclipse.plugin.datenmodell.generator.visualizer.preferences;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.jface.dialogs.IInputValidator;

public class RegexpInputValidator implements IInputValidator {

    @Override
    public String isValid(String newText) {
        try {
            Pattern.compile(newText);
        } catch (PatternSyntaxException exception) {
            return "'" + newText + "' is not a valid Regexp!";
        }
        return null;
    }

}
