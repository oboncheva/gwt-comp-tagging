package com.google.code.gwt.component.tag;

import java.util.List;

/**
 * Suggestion 
 * @author Pavol Gressa <gressa@acemcee.com>
 * @param <T> 
 */
public interface SuggestionDelegate<T extends Tag> {

    public void findSuggestions(String text, List<T> suggestions);
}


