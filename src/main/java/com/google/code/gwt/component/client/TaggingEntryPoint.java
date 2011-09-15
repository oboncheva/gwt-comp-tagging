package com.google.code.gwt.component.client;

import com.google.code.gwt.component.client.tag.StringTag;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import java.util.ArrayList;
import java.util.List;



/**
 * 
 * @author Palo Gressa <gressa@acemcee.com>
 */
public class TaggingEntryPoint implements EntryPoint{

    @Override
    public void onModuleLoad(){
       List<Tag> items = new ArrayList<Tag>();
       items.add(new StringTag("Tag 1", "Tag 1"));
       items.add(new StringTag("Tag 2", "Tag 2"));
       items.add(new StringTag("Tag 3", "Tag 3"));
       TagsInput ti = new TagsInput(items);
       ti.setWidth("400px");
       RootPanel.get("test").add(ti);
       
    }
    




}
