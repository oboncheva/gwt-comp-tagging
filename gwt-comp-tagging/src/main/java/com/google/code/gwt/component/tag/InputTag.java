package com.google.code.gwt.component.tag;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
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
public class InputTag<T extends Tag> extends Widget {

    /*------------------------------*/
    /*-- Component DOM Elements   --*/
    /*------------------------------*/
    /** Main component DIV element */
    private Element component;
    /** List that holds items */
    private Element tagList;
    /** Suggestions list */
    private Element suggestionList;
    /** Currently active input text */
    private InputElement inputText;
    /** Span element which is positioned outside of screen for computing width of new tag */
    private SpanElement widthSpanTester;
    /*----------------------------------*/
    /*-- Component Business Objects   --*/
    /*----------------------------------*/
    /** Tags */
    private List<ItemTag<T>> tags;
    /** Reference to previously suggested tags */
    private List<T> suggestedTags;
    /** New tags validator */
    private TagValidator tagValidator;

    /*--------------------------------*/
    /*-- Component Worker Objects   --*/
    /*--------------------------------*/
    /** Delegete for getting suggestions for given input */
    private SuggestionDelegate<T> suggestionDelegate;
    /** Presenter for suggestions for given input */
    private SuggestionPresenter<T> suggestionPresenter;
    /** Caret last position used to compute whether the focus of active element should be switched to next / previous sibling */
    private int caretLastPosition = 0;
    /** Mode of tag input */
    private Mode mode;

    public InputTag(List<T> tags) {
        // init tags
        this.tags = new ArrayList<ItemTag<T>>();
        // init layout
        initLayout(tags);
        // set default mode
        setMode(Mode.DEFAULT);
        // sed default presenter
        suggestionPresenter = new DefaultSuggestionPresenter<T>();
        // initialize suggestions
        suggestedTags = new ArrayList<T>();
    }

    public InputTag() {
        this(null);
    }

    public void setWidth(int pixels) {
        this.getElement().getStyle().setWidth(pixels, Unit.PX);
        suggestionList.getParentElement().getStyle().setWidth(pixels, Unit.PX);

    }

    @Override
    public void setWidth(String value) {
        DOM.setStyleAttribute(this.getElement(), "width", value);
        DOM.setStyleAttribute(suggestionList.getParentElement().<com.google.gwt.user.client.Element>cast(), "width", value);
    }

    private void initLayout(List<T> tags) {
        // init main wrapper area
        component = DOM.createDiv();
        component.setClassName("input-tag");

        /** UL list wrapper */
        Element tagListWrapeer = DOM.createDiv();
        tagListWrapeer.setClassName("input-tag-list");

        // init list area
        tagList = Document.get().createULElement();
        tagList.setClassName("input-tag-list-tags");

        // init
        tagListWrapeer.appendChild(tagList);
        component.appendChild(tagListWrapeer);
        setElement(component);

        Element e = DOM.createInputText();
        DOM.setStyleAttribute(e.<com.google.gwt.user.client.Element>cast(), "display", "none");
        component.appendChild(e);

        // insert first input text for tagging.
        initializeInputText();

        if (tags != null) {
            for (Tag tagItem : tags) {
                appendTag(tagItem);
            }
        }


        // init suggestion list
        suggestionList = Document.get().createULElement();
        suggestionList.setClassName("tags-suggestion-list");

        // suggestion list wrapper
        Element suggestionListWraper = DOM.createDiv();
        suggestionListWraper.setClassName("tags-suggestion-wrapper");
        suggestionListWraper.appendChild(suggestionList);

        component.appendChild(suggestionListWraper);
    }

    protected LIElement createTagLIElement(final EventListener listener, int eventBits) {
        // create list item
        final LIElement listItem = Document.get().createLIElement();
        listItem.setClassName("input-tag-list-item");
        listItem.setTabIndex(0);
        // Set event listeners
        DOM.setEventListener(listItem.<com.google.gwt.user.client.Element>cast(), new EventListener() {

            @Override
            public void onBrowserEvent(Event event) {
                if (listener != null) {
                    listener.onBrowserEvent(event);
                }
                if (event.getTypeInt() == Event.ONMOUSEOUT || event.getTypeInt() == Event.ONBLUR) {
                    listItem.removeClassName("input-tag-list-item-hover");
                } else if (event.getTypeInt() == Event.ONMOUSEOVER || event.getTypeInt() == Event.ONFOCUS) {
                    listItem.addClassName("input-tag-list-item-hover");
                }
            }
        });
        DOM.sinkEvents(listItem.<com.google.gwt.user.client.Element>cast(),  Event.FOCUSEVENTS | Event.ONMOUSEOUT | Event.ONMOUSEOVER | eventBits);
        return listItem;
    }

    /**
     * Appends tag to InputTag component. Tag is placed right after last tag.
     * @param tag representation
     * @return list item
     */
    protected void appendTag(Tag tag) {
        // item
        final Element item = createTagLIElement(new EventListener() {
            @Override
            public void onBrowserEvent(Event event) {

                if (event.getTypeInt() == Event.ONKEYDOWN && (event.getKeyCode() == KeyCodes.KEY_BACKSPACE || event.getKeyCode() == KeyCodes.KEY_DELETE)) {
                    removeTag(event.getEventTarget().<com.google.gwt.dom.client.Element>cast());
                } else if (event.getTypeInt() == Event.ONKEYDOWN && event.getKeyCode() == KeyCodes.KEY_LEFT) {
                    shiftFocusLeft(event.getEventTarget().<com.google.gwt.dom.client.Element>cast());
                } else if (event.getTypeInt() == Event.ONKEYDOWN && event.getKeyCode() == KeyCodes.KEY_RIGHT) {
                    shiftFocusRight(event.getEventTarget().<com.google.gwt.dom.client.Element>cast());
                }
            }
        },  Event.ONKEYDOWN);
        item.addClassName("input-tag-list-box");
        item.addClassName("input-tag-list-item-deletable");
        
        // create tag text
        SpanElement tagSpan = Document.get().createSpanElement();
        tagSpan.setInnerText(tag.getTag());
        item.appendChild(tagSpan);

        // delete tag
        Element deleteAnchor = Document.get().createAnchorElement();
        deleteAnchor.setClassName("input-tag-list-tag-delete");
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
        tagList.insertBefore(item, inputText.getParentElement());
        // insert into inner list
        tags.add(new ItemTag(tag, item));
    }

    protected LIElement createSuggestionElement() {
        // create list item
        final LIElement listItem = Document.get().createLIElement();
        listItem.setTabIndex(0);
        // Set event listeners
        DOM.setEventListener(listItem.<com.google.gwt.user.client.Element>cast(), new EventListener() {

            @Override
            public void onBrowserEvent(Event event) {
                if (event.getTypeInt() == Event.ONMOUSEOUT ) {
                    listItem.removeClassName("tags-suggestion-list-suggestion-focus");
                } else if (event.getTypeInt() == Event.ONMOUSEOVER ) {
                    listItem.addClassName("tags-suggestion-list-suggestion-focus");
                } else if (event.getTypeInt() == Event.ONCLICK || (event.getTypeInt() == Event.ONKEYDOWN && event.getKeyCode() == KeyCodes.KEY_ENTER)) {
                }
            }
        });
        DOM.sinkEvents(listItem.<com.google.gwt.user.client.Element>cast(), Event.ONMOUSEOUT | Event.ONMOUSEOVER | Event.ONCLICK | Event.ONKEYUP);
        return listItem;
    }

    private void removeTag(Element listItem) {
        // we can remove tag only when mode allows it
        if (mode != Mode.READ_ONLY) {

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
    }

    private void resetInputText() {
        // reset input text
        inputText.setValue("");
        inputText.getStyle().setWidth(50, Unit.PX);
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

        String text = inputText.getValue();

        // update input text width
        widthSpanTester.setInnerText(text);
        inputText.getStyle().setWidth(widthSpanTester.getOffsetWidth() + 20, Unit.PX);

        // hide and clear suggestions
        hideAndClearSuggestionList();

        // try suggestion oraculum for tags
        if (getSuggestionDelegate() != null) {

            // clear suggestions
            suggestedTags.clear();
            // clear suggestions element
            while (suggestionList.hasChildNodes()) {
                suggestionList.removeChild(suggestionList.getFirstChild());
            }

            // try to find new suggestions
            getSuggestionDelegate().findSuggestions(text, suggestedTags);
            if (suggestedTags.size() > 0) {
                suggestionList.getStyle().setDisplay(Display.BLOCK);
                // create suggestions list items
                for (T tag : suggestedTags) {
                    LIElement suggestionElement = createSuggestionElement();
                    getSuggestionPresenter().createSuggestion(suggestionElement, tag, text);
                    suggestionList.appendChild(suggestionElement);
                }
            }
        }
    }

    private void hideAndClearSuggestionList() {
        suggestionList.getStyle().setDisplay(Display.NONE);
    }

    private void handleNewTag() {
        String value = inputText.getValue();
        if (value.length() > 0) {
            // find possilbe tag in suggested tags
            Tag tag = findInSuggestedTags(value);
            
            // when mode allows only tags from oracle and there is no match, do nothing
            if (mode == Mode.ONLY_SUGGESTED_TAGS && tag == null) {
                return;
            }

            // if we did not found tag from oracle then create new one
            if (tag == null) {
                if(tagValidator != null && !tagValidator.isValid(value)){
                    return;
                }
                tag = new Tag(null, value);
            }
            appendTag(tag);
            resetInputText();
        }
    }

    private Tag findInSuggestedTags(String value) {
        if (suggestedTags != null) {
            for (Tag tag : suggestedTags) {
                if (tag.getTag().equalsIgnoreCase(value)) {
                    return tag;
                }
            }
        }
        return null;
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
     * @return the mode
     */
    public Mode getMode() {
        return mode;
    }

    /**
     * @param mode the mode to set
     */
    public void setMode(Mode mode) {
        this.mode = mode;
        switch (mode) {
            case DEFAULT:
                setEditable(true);
                break;
            case ONLY_SUGGESTED_TAGS:
                setEditable(true);
                break;
            case READ_ONLY:
                setEditable(false);
                break;
        }
    }

    /**
     * Make component only read only. This method has same effect when setting mode
     * to {@link Mode#READ_ONLY}.
     *
     * @param value boolean value, when <code>true</code> then component is editable otherwise is not
     */
    public void setEditable(boolean value) {
        if (inputText.getParentElement().getParentElement() == null && value) {
            tagList.appendChild(inputText.getParentElement());
            for (ItemTag<T> itemTag : tags) {
                itemTag.listItem.getFirstChildElement().getNextSiblingElement().getStyle().setVisibility(Visibility.VISIBLE);
                itemTag.listItem.addClassName("input-tag-list-item-deletable");
            }
        } else if (inputText.getParentElement().getParentElement() != null && !value) {
            inputText.getParentElement().removeFromParent();
            for (ItemTag<T> itemTag : tags) {
                itemTag.listItem.getFirstChildElement().getNextSiblingElement().getStyle().setVisibility(Visibility.HIDDEN);
                itemTag.listItem.removeClassName("input-tag-list-item-deletable");
            }
        }
    }

    /**
     * Initializes inputText element for inserting new tags by keyboard. There is
     * only one inputText in time. 
     */
    protected void initializeInputText() {
        inputText = (DOM.createInputText()).cast();
        inputText.setClassName("input-tag-list-tag-input");
        inputText.setTabIndex(0);
        DOM.setEventListener(inputText.<com.google.gwt.user.client.Element>cast(), new EventListener() {

            @Override
            public void onBrowserEvent(Event event) {
                if (event.getTypeInt() == Event.ONKEYPRESS && event.getKeyCode() == KeyCodes.KEY_ENTER) {
                    handleNewTag();
                } else if (event.getTypeInt() == Event.ONKEYDOWN) {
                    if ((caretLastPosition == 1 || caretLastPosition == 0) && getCursorPos(inputText) == 0) {
                        if (event.getKeyCode() == KeyCodes.KEY_BACKSPACE || event.getKeyCode() == KeyCodes.KEY_LEFT) {
                            shiftFocusLeft(inputText.getParentNode().<com.google.gwt.user.client.Element>cast());
                        }
                    }
                    caretLastPosition = getCursorPos(inputText);
                } else if (event.getTypeInt() == Event.ONKEYUP) {
                    handleInputChange();
                } else if (event.getTypeInt() == Event.ONFOCUS) {
                    inputText.getParentElement().addClassName("input-tag-list-tag-focus");
                } else if (event.getTypeInt() == Event.ONBLUR) {
                    inputText.getParentElement().removeClassName("input-tag-list-tag-focus");
                    hideAndClearSuggestionList();

                }
            }
        });
        DOM.sinkEvents(inputText.<com.google.gwt.user.client.Element>cast(), Event.ONKEYPRESS | Event.ONKEYDOWN | Event.ONKEYUP | Event.FOCUSEVENTS);


        widthSpanTester = Document.get().createSpanElement();
        widthSpanTester.setAttribute("style", "float: left; left: -1000px; position: absolute; display: inline-block;");


        // create list item element and append all items
        Element item = createTagLIElement(new EventListener() {

            @Override
            public void onBrowserEvent(Event event) {

                if (event.getTypeInt() == Event.ONFOCUS) {
                    inputText.focus();
                }
            }
        }, Event.ONFOCUS);
        item.addClassName("input-tag-list-tag-editable");
        item.appendChild(inputText);
        item.appendChild(widthSpanTester);

        // append new list item into list
        tagList.appendChild(item);
    }

    /**
     * Delegete for getting suggestions for given input
     * @return the suggestionDelegate
     */
    public SuggestionDelegate<T> getSuggestionDelegate() {
        return suggestionDelegate;
    }

    /**
     * Delegete for getting suggestions for given input
     * @param suggestionDelegate the suggestionDelegate to set
     */
    public void setSuggestionDelegate(SuggestionDelegate<T> suggestionDelegate) {
        this.suggestionDelegate = suggestionDelegate;
    }

    /**
     * @return the suggestionPresenter
     */
    public SuggestionPresenter<T> getSuggestionPresenter() {
        return suggestionPresenter;
    }

    /**
     * @param suggestionPresenter the suggestionPresenter to set
     */
    public void setSuggestionPresenter(SuggestionPresenter<T> suggestionPresenter) {
        this.suggestionPresenter = suggestionPresenter;
    }

    /**
     * @return the tagValidator
     */
    public TagValidator getTagValidator() {
        return tagValidator;
    }

    /**
     * @param tagValidator the tagValidator to set
     */
    public void setTagValidator(TagValidator tagValidator) {
        this.tagValidator = tagValidator;
    }

    /**
     * Inner private class that identifies existing tags with list elements.
     */
    private class ItemTag<T extends Tag> {

        private T tag;
        private Element listItem;

        public ItemTag(T tag, Element listItem) {
            this.tag = tag;
            this.listItem = listItem;
        }

        public Element getListItem() {
            return listItem;
        }

        public T getTag() {
            return tag;
        }
    }

    /**
     * Enum that defines modes component modes.
     */
    public enum Mode {

        /** Read only mode, no other tags can be inserted */
        READ_ONLY,
        /** Tags can be only from given set of items provided by {@link ... } */
        ONLY_SUGGESTED_TAGS,
        /** Free tags */
        DEFAULT;
    }
}
