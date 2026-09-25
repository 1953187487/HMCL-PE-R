package com.github.steveice10.opennbt.tag.builtin;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListTag extends Tag {

    private int elementType = TAG_END;
    private final ArrayList<Tag> value = new ArrayList<>();

    public ListTag() {
        super("");
    }

    public ListTag(String name) {
        super(name);
    }

    public ListTag(String name, int elementType) {
        super(name);
        this.elementType = elementType;
    }

    @Override
    public int getType() {
        return TAG_LIST;
    }

    @Override
    public Object getValue() {
        return Collections.unmodifiableList(value);
    }

    public int getElementType() {
        return elementType;
    }

    public void put(Tag tag) {
        if (tag == null) {
            return;
        }
        if (value.isEmpty()) {
            elementType = tag.getType();
        }
        value.add(tag);
    }

    public Tag get(int index) {
        return value.get(index);
    }

    public int size() {
        return value.size();
    }

    public List<Tag> getList() {
        return value;
    }
}
