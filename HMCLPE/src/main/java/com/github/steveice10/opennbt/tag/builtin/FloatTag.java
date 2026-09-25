package com.github.steveice10.opennbt.tag.builtin;


public class FloatTag extends Tag {

    private float value;

    public FloatTag() {
        super("");
    }

    public FloatTag(String name) {
        super(name);
    }

    public FloatTag(String name, float value) {
        super(name);
        this.value = value;
    }

    @Override
    public int getType() {
        return TAG_FLOAT;
    }

    @Override
    public Object getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
