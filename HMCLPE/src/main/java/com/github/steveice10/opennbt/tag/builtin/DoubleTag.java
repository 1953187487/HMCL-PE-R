package com.github.steveice10.opennbt.tag.builtin;


public class DoubleTag extends Tag {

    private double value;

    public DoubleTag() {
        super("");
    }

    public DoubleTag(String name) {
        super(name);
    }

    public DoubleTag(String name, double value) {
        super(name);
        this.value = value;
    }

    @Override
    public int getType() {
        return TAG_DOUBLE;
    }

    @Override
    public Object getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
