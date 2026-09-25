package com.github.steveice10.opennbt.tag.builtin;


import java.util.Arrays;

public class IntArrayTag extends Tag {

    private int[] value;

    public IntArrayTag() {
        super("");
        this.value = new int[0];
    }

    public IntArrayTag(String name) {
        super(name);
        this.value = new int[0];
    }

    public IntArrayTag(String name, int[] value) {
        super(name);
        this.value = value != null ? value.clone() : new int[0];
    }

    @Override
    public int getType() {
        return TAG_INT_ARRAY;
    }

    @Override
    public int[] getValue() {
        return value.clone();
    }

    public void setValue(int[] value) {
        this.value = value != null ? value.clone() : new int[0];
    }

    @Override
    public String toString() {
        return getTypeName() + "[\"" + getName() + "\": " + Arrays.toString(value) + "]";
    }
}
