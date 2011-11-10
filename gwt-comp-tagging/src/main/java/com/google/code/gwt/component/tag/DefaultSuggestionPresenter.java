package com.google.code.gwt.component.tag;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.user.client.DOM;

/**
 * 
 * @author Palo Gressa <gressa@acemcee.com>
 */
public class DefaultSuggestionPresenter<T extends Tag> implements SuggestionPresenter<T>{
    
    @Override
    public void createSuggestion(LIElement e, T tag, String text) {
        e.setClassName("tags-suggestion-list-suggestion");
        Element span = DOM.createSpan();
        if(tag.getTag().startsWith(text)){
            span.setInnerHTML("<strong>"+text+"</strong>"+tag.getTag().substring(text.length()));
        }else{
            span.setInnerText(tag.getTag());
        }
        e.appendChild(span);
    }




}
