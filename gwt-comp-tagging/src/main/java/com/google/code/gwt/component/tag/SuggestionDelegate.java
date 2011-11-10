package com.google.code.gwt.component.tag;

import java.util.List;

public interface SuggestionDelegate<T extends Tag> {

    public void findSuggestions(String text, List<T> suggestions);
}
