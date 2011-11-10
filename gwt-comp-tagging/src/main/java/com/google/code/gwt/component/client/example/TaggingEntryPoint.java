package com.google.code.gwt.component.client.example;

import com.google.code.gwt.component.tag.SuggestionDelegate;
import com.google.code.gwt.component.tag.Tag;
import com.google.code.gwt.component.tag.InputTag;
import com.google.code.gwt.component.tag.StringTag;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Palo Gressa <gressa@acemcee.com>
 */
public class TaggingEntryPoint implements EntryPoint {

    @Override
    public void onModuleLoad() {
        
        
        
        
        List<StringTag> items = new ArrayList<StringTag>();
        items.add(new StringTag("Tag 1", "Tag 1"));
        items.add(new StringTag("Tag 2", "Tag 2"));
        items.add(new StringTag("Tag 3", "Tag 3"));

        InputTag<StringTag> ti1 = new InputTag<StringTag>(items);
        ti1.setWidth("400px");
        ti1.setSuggestionDelegate(new SuggestionDelegateMock());

        InputTag<StringTag> ti2 = new InputTag<StringTag>(items);
        ti2.setWidth("400px");
        ti2.setMode(InputTag.Mode.READ_ONLY);

        InputTag<StringTag> ti3 = new InputTag<StringTag>(items);
        ti3.setWidth("400px");
        ti3.setSuggestionDelegate(new SuggestionDelegateMock());

        RootPanel.get("test").add(ti1);
        RootPanel.get("test").add(ti2);
        RootPanel.get("test").add(ti3);

    }

    protected class SuggestionDelegateMock implements SuggestionDelegate<StringTag> {

        @Override
        public void findSuggestions(String text, List<StringTag> suggestions) {
            if (text.startsWith("j")) {
                suggestions.add(new StringTag(text, "java"));
                suggestions.add(new StringTag(text, "javascipt"));
                suggestions.add(new StringTag(text, "jackson"));
                suggestions.add(new StringTag(text, "jarvana"));
                suggestions.add(new StringTag(text, "jabadabadoo"));
                suggestions.add(new StringTag(text, "joseph"));
                suggestions.add(new StringTag(text, "johnny"));
            } else if (text.startsWith("ja")) {
                suggestions.add(new StringTag(text, "java"));
                suggestions.add(new StringTag(text, "javascipt"));
                suggestions.add(new StringTag(text, "jackson"));
                suggestions.add(new StringTag(text, "jarvana"));
                suggestions.add(new StringTag(text, "jabadabadoo"));                
            } else if (text.startsWith("ja")) {
                suggestions.add(new StringTag(text, "java"));
                suggestions.add(new StringTag(text, "javascipt"));
            }
        }
    }
}
