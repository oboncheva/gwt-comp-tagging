package com.google.code.gwt.component.tag;

import com.google.gwt.dom.client.LIElement;

/**
 * 
 * @author Palo Gressa <gressa@acemcee.com>
 */
public interface SuggestionPresenter<T extends Tag> {
    public void createSuggestion(LIElement e, T tag, String text);
}
