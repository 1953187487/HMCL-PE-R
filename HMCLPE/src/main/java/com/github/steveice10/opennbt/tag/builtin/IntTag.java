package com.github.steveice10.opennbt.tag.builtin;


public class IntTag extends Tag {

    private int value;

    public IntTag() {
        super("");
    }

    public IntTag(String name) {
        super(name);
    }

    public IntTag(String name, int value) {
        super(name);
        this.value = value;
    }

    @Override
    public int getType() {
        return TAG_INT;
    }

    @Override
    public Object getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
