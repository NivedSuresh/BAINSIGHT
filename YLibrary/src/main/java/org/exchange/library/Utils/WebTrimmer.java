package org.exchange.library.Utils;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;


public class WebTrimmer {
    public static void setCustomEditorForWebBinder(WebDataBinder binder) {
        StringTrimmerEditor ste = new StringTrimmerEditor(true);
        binder.registerCustomEditor(String.class, ste);
    }
}
