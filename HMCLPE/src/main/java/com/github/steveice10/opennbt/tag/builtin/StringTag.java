package com.github.steveice10.opennbt.tag.builtin;


public class StringTag extends Tag {

    private String value;

    public StringTag() {
        super("");
        this.value = "";
    }

    public StringTag(String name) {
        super(name);
        this.value = "";
    }

    public StringTag(String name, String value) {
        super(name);
        this.value = value;
    }

    @Override
    public int getType() {
        return TAG_STRING;
    }

    @Override
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
