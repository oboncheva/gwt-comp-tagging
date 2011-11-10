package com.google.code.gwt.component.tag;

import java.io.Serializable;


/**
 * Abstract representation of tag. Tag consists from plain string that represents
 * visible tag in component <code>tag</code> and from value that identities tag
 * in backend system. There are implementations for most use cases {@link IntTag},
 * {@link StringTag}
 *
 * @author Palo Gressa <gressa@acemcee.com>
 */
public class Tag<T> implements Serializable{
    private static final long serialVersionUID = -5381676167463921282L;

    public T value;
    public String tag;

    public Tag() {
    }

    public Tag(T value, String tag) {
        this.value = value;
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public T getValue() {
        return value;
    }
}
