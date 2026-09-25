package com.github.steveice10.opennbt.tag.builtin;


public class LongTag extends Tag {

    private long value;

    public LongTag() {
        super("");
    }

    public LongTag(String name) {
        super(name);
    }

    public LongTag(String name, long value) {
        super(name);
        this.value = value;
    }

    @Override
    public int getType() {
        return TAG_LONG;
    }

    @Override
    public Object getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }
}
