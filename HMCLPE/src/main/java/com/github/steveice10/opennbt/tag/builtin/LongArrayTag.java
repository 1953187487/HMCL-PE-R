package com.github.steveice10.opennbt.tag.builtin;


import java.util.Arrays;

public class LongArrayTag extends Tag {

    private long[] value;

    public LongArrayTag() {
        super("");
        this.value = new long[0];
    }

    public LongArrayTag(String name) {
        super(name);
        this.value = new long[0];
    }

    public LongArrayTag(String name, long[] value) {
        super(name);
        this.value = value != null ? value.clone() : new long[0];
    }

    @Override
    public int getType() {
        return TAG_LONG_ARRAY;
    }

    @Override
    public long[] getValue() {
        return value.clone();
    }

    public void setValue(long[] value) {
        this.value = value != null ? value.clone() : new long[0];
    }

    @Override
    public String toString() {
        return getTypeName() + "[\"" + getName() + "\": " + Arrays.toString(value) + "]";
    }
}
