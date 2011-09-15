package com.google.code.gwt.component.client;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Widget;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 *
 *
 * @author Palo Gressa <gressa@acemcee.com>
 */
public class TagsInput extends Widget {

    /** Main component DIV element */
    private Element component;
    /** List that holds items */
    private Element ulList;
    /** UL list wrapper */
    private Element ulListWrapeer;
    /** Currently active input text */
    private InputElement activeInputText;
    /** Span that is used for autowidth of input type text */
    private SpanElement widthSpanTester;
    /** Tags */
    private List<ItemTag> tags;
    private int caretLastPosition = 0;

    public TagsInput(List<Tag> tags) {
        // init tags
        this.tags = new ArrayList<ItemTag>();
        // init layout
        initLayout(tags);
    }

    public void setWidth(int pixels) {
        DOM.setStyleAttribute(this.getElement(), "width", pixels + "px");
    }

    @Override
    public void setWidth(String value) {
        DOM.setStyleAttribute(this.getElement(), "width", value);
    }

    private void initLayout(List<Tag> tags) {
        // init main wrapper area
        component = DOM.createDiv();
        component.setClassName("tags-input");

        ulListWrapeer = DOM.createDiv();
        ulListWrapeer.setClassName("tags-input-list");

        // init list area
        ulList = Document.get().createULElement();
        ulList.setClassName("tags-input-list-tags");

        // init
        ulListWrapeer.appendChild(ulList);
        component.appendChild(ulListWrapeer);
        setElement(component);

        Element e = DOM.createInputText();
        DOM.setStyleAttribute(e.<com.google.gwt.user.client.Element>cast(), "display", "none");
        component.appendChild(e);

        // insert first input text for tagging.
        createInputListItem();

        // hide first input text and append exist tags        
        for (Tag tagItem : tags) {
            createTagItem(tagItem);
        }
    }

    protected LIElement createListElement() {
        // create list item
        final LIElement listItem = Document.get().createLIElement();
        listItem.setClassName("tags-input-list-item");
        listItem.setTabIndex(0);
        // Set event listeners
        DOM.setEventListener(listItem.<com.google.gwt.user.client.Element>cast(), new EventListener() {

            @Override
            public void onBrowserEvent(Event event) {
                if (event.getTypeInt() == Event.ONMOUSEOUT) {
                    listItem.removeClassName("tags-input-list-item-hover");
                } else if (event.getTypeInt() == Event.ONMOUSEOVER) {
                    listItem.addClassName("tags-input-list-item-hover");
                }
            }
        });
        DOM.sinkEvents(listItem.<com.google.gwt.user.client.Element>cast(), Event.ONMOUSEOUT | Event.ONMOUSEOVER);
        return listItem;
    }

    protected void createInputListItem() {
        // create and initialize input type text element
        final InputElement inputText = (DOM.createInputText()).cast();
        inputText.setClassName("tags-input-list-tag-input");
        inputText.setTabIndex(0);
        DOM.setEventListener(inputText.<com.google.gwt.user.client.Element>cast(), new EventListener() {

            @Override
            public void onBrowserEvent(Event event) {
                if (event.getTypeInt() == Event.ONKEYPRESS) {
                    if (event.getKeyCode() == KeyCodes.KEY_ENTER) {
                        handleNewTag();
                    } else {
                        handleInputChange();
                    }
                } else if (event.getTypeInt() == Event.ONKEYDOWN) {
                    if ((caretLastPosition == 1 || caretLastPosition == 0) && getCursorPos(activeInputText) == 0) {
                        if (event.getKeyCode() == KeyCodes.KEY_BACKSPACE || event.getKeyCode() == KeyCodes.KEY_LEFT) {
                            shiftFocusLeft();
                        }
                    }
                    handleInputChange();                    
                    caretLastPosition = getCursorPos(activeInputText);

                } else if (event.getTypeInt() == Event.ONFOCUS) {
                    activeInputText.getParentElement().addClassName("tags-input-list-tag-focus");
                } else if (event.getTypeInt() == Event.ONBLUR) {
                    activeInputText.getParentElement().removeClassName("tags-input-list-tag-focus");
                }
            }
        });
        DOM.sinkEvents(inputText.<com.google.gwt.user.client.Element>cast(), Event.ONKEYPRESS | Event.ONKEYDOWN | Event.FOCUSEVENTS);


        // create span for auto width
        SpanElement span = Document.get().createSpanElement();
        span.setAttribute("style", "float: left; left: -1000px; position: absolute; display: inline-block;");


        // create list item element and append all items
        Element item = createListElement();
        item.addClassName("tags-input-list-tag-editable");
        item.appendChild(inputText);
        item.appendChild(span);
        // whent list item will gain focus then we will pass focus to input text
        DOM.setEventListener(item.<com.google.gwt.user.client.Element>cast(), new EventListener() {

            @Override
            public void onBrowserEvent(Event event) {

                if (event.getTypeInt() == Event.ONFOCUS) {
                    activeInputText.focus();
                }
            }
        });
        DOM.sinkEvents(item.<com.google.gwt.user.client.Element>cast(), Event.ONFOCUS);

        // append new list item into list
        ulList.appendChild(item);

        // set into global properties
        widthSpanTester = span;
        activeInputText = inputText;
    }

    /**
     * Creates item list that represents a tag.
     * @param tag representation
     * @return list item
     */
    protected void createTagItem(Tag tag) {
        // item
        final Element item = createListElement();
        item.addClassName("tags-input-list-box");
        item.addClassName("tags-input-list-item-deletable");

        // add additional listeners to item
        DOM.setEventListener(item.<com.google.gwt.user.client.Element>cast(), new EventListener() {

            @Override
            public void onBrowserEvent(Event event) {

                if (event.getTypeInt() == Event.ONKEYDOWN && event.getKeyCode() == KeyCodes.KEY_BACKSPACE) {
                    removeTag(item);
                } else if (event.getTypeInt() == Event.ONKEYDOWN && event.getKeyCode() == KeyCodes.KEY_LEFT) {
                    shiftFocusLeft(item);
                } else if (event.getTypeInt() == Event.ONKEYDOWN && event.getKeyCode() == KeyCodes.KEY_RIGHT) {
                    shiftFocusRight(item);
                }
            }
        });
        DOM.sinkEvents(item.<com.google.gwt.user.client.Element>cast(), Event.ONKEYDOWN);


        // create tag text
        SpanElement tagSpan = Document.get().createSpanElement();
        tagSpan.setInnerText(tag.getTag());
        item.appendChild(tagSpan);

        // delete tag
        Element deleteAnchor = Document.get().createAnchorElement();
        deleteAnchor.setClassName("tags-input-list-tag-delete");
        DOM.setEventListener(deleteAnchor.<com.google.gwt.user.client.Element>cast(), new EventListener() {

            @Override
            public void onBrowserEvent(Event event) {
                if (event.getTypeInt() == Event.ONCLICK) {
                    removeTag(item);
                }
            }
        });
        DOM.sinkEvents(deleteAnchor.<com.google.gwt.user.client.Element>cast(), Event.ONCLICK);
        item.appendChild(deleteAnchor);
        // insert into DOM
        ulList.insertBefore(item, activeInputText.getParentElement());
        // insert into inner list
        tags.add(new ItemTag(tag, item));
    }

    private void removeTag(Element listItem) {
        // remove tag from inner tag list
        ItemTag t = null;
        for (ItemTag itemTag : tags) {
            if (itemTag.getListItem().equals(listItem)) {
                t = itemTag;
                break;
            }
        }
        if (t == null) {
            throw new NullPointerException("List item element that has to be removed was not found!");
        }
        tags.remove(t);

        // make next sibling active
        shiftFocusRight(listItem);

        // remove tag from DOM
        listItem.removeFromParent();
    }

    private void shiftFocusLeft() {
        Node sib = activeInputText.getParentNode().getPreviousSibling();
        if (sib != null) {
            sib.<com.google.gwt.user.client.Element>cast().focus();
        }
    }

    private void shiftFocusLeft(Element listItem) {
        Node sib = listItem.getPreviousSibling();
        if (sib != null) {
            sib.<com.google.gwt.user.client.Element>cast().focus();
        }
    }

    private void shiftFocusRight(Element listItem) {
        Element sib = listItem.getNextSiblingElement();
        if (sib != null) {
            sib.focus();
        }
    }

    private void handleInputChange() {
        widthSpanTester.setInnerText(activeInputText.getValue());
        activeInputText.getStyle().setWidth(widthSpanTester.getOffsetWidth() + 20, Unit.PX);
    }

    private void handleNewTag() {
        String value = activeInputText.getValue();
        if (value.length() > 0) {
            Tag tag = new Tag(null, value);
            createTagItem(tag);

        }
        activeInputText.setValue("");
        activeInputText.getStyle().setWidth(50, Unit.PX);
    }

    /**
     * Copy from {@link com.google.gwt.user.client.ui.ValueBoxBase}
     */
    public native int getCursorPos(Element elem) /*-{
    // Guard needed for FireFox.
    try{
    return elem.selectionStart;
    } catch (e) {
    return 0;
    }
    }-*/;

    /**
     * Inner private class that identifies existing tags with list elements.
     */
    private class ItemTag {

        private Tag tag;
        private Element listItem;

        public ItemTag(Tag tag, Element listItem) {
            this.tag = tag;
            this.listItem = listItem;
        }

        public Element getListItem() {
            return listItem;
        }

        public Tag getTag() {
            return tag;
        }
    }
}
