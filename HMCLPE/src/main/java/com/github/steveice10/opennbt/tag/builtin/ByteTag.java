package com.github.steveice10.opennbt.tag.builtin;


public class ByteTag extends Tag {

    private byte value;

    public ByteTag() {
        super("");
    }

    public ByteTag(String name) {
        super(name);
    }

    public ByteTag(String name, byte value) {
        super(name);
        this.value = value;
    }

    @Override
    public int getType() {
        return TAG_BYTE;
    }

    @Override
    public Object getValue() {
        return value;
    }

    public void setValue(byte value) {
        this.value = value;
    }
}
