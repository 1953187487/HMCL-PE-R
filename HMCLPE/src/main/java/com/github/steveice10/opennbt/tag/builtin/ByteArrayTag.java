package com.github.steveice10.opennbt.tag.builtin;


import java.util.Arrays;

public class ByteArrayTag extends Tag {

    private byte[] value;

    public ByteArrayTag() {
        super("");
        this.value = new byte[0];
    }

    public ByteArrayTag(String name) {
        super(name);
        this.value = new byte[0];
    }

    public ByteArrayTag(String name, byte[] value) {
        super(name);
        this.value = value != null ? value.clone() : new byte[0];
    }

    @Override
    public int getType() {
        return TAG_BYTE_ARRAY;
    }

    @Override
    public byte[] getValue() {
        return value.clone();
    }

    public void setValue(byte[] value) {
        this.value = value != null ? value.clone() : new byte[0];
    }

    @Override
    public String toString() {
        return getTypeName() + "[\"" + getName() + "\": " + Arrays.toString(value) + "]";
    }
}
