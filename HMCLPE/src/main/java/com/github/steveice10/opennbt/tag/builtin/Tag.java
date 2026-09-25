package com.github.steveice10.opennbt.tag.builtin;

public abstract class Tag {

    public static final int TAG_END = 0;
    public static final int TAG_BYTE = 1;
    public static final int TAG_SHORT = 2;
    public static final int TAG_INT = 3;
    public static final int TAG_LONG = 4;
    public static final int TAG_FLOAT = 5;
    public static final int TAG_DOUBLE = 6;
    public static final int TAG_BYTE_ARRAY = 7;
    public static final int TAG_INT_ARRAY = 8;
    public static final int TAG_STRING = 9;
    public static final int TAG_LIST = 10;
    public static final int TAG_COMPOUND = 11;
    public static final int TAG_LONG_ARRAY = 12;

    private final String name;

    protected Tag(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract int getType();

    public abstract Object getValue();

    public String getTypeName() {
        switch (getType()) {
            case TAG_END: return "TAG_END";
            case TAG_BYTE: return "TAG_BYTE";
            case TAG_SHORT: return "TAG_SHORT";
            case TAG_INT: return "TAG_INT";
            case TAG_LONG: return "TAG_LONG";
            case TAG_FLOAT: return "TAG_FLOAT";
            case TAG_DOUBLE: return "TAG_DOUBLE";
            case TAG_BYTE_ARRAY: return "TAG_BYTE_ARRAY";
            case TAG_INT_ARRAY: return "TAG_INT_ARRAY";
            case TAG_STRING: return "TAG_STRING";
            case TAG_LIST: return "TAG_LIST";
            case TAG_COMPOUND: return "TAG_COMPOUND";
            case TAG_LONG_ARRAY: return "TAG_LONG_ARRAY";
            default: return "TAG_UNKNOWN";
        }
    }

    @Override
    public String toString() {
        return getTypeName() + "[\"" + name + "\": " + getValue() + "]";
    }
}
