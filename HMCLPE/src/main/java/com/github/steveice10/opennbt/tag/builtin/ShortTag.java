package com.github.steveice10.opennbt.tag.builtin;


public class ShortTag extends Tag {

    private short value;

    public ShortTag() {
        super("");
    }

    public ShortTag(String name) {
        super(name);
    }

    public ShortTag(String name, short value) {
        super(name);
        this.value = value;
    }

    @Override
    public int getType() {
        return TAG_SHORT;
    }

    @Override
    public Object getValue() {
        return value;
    }

    public void setValue(short value) {
        this.value = value;
    }
}
