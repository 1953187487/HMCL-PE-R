package com.github.steveice10.opennbt.tag.builtin;


import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class CompoundTag extends Tag {

    private final LinkedHashMap<String, Tag> value = new LinkedHashMap<>();

    public CompoundTag() {
        super("");
    }

    public CompoundTag(String name) {
        super(name);
    }

    @Override
    public int getType() {
        return TAG_COMPOUND;
    }

    @Override
    public Object getValue() {
        return Collections.unmodifiableMap(value);
    }

    @SuppressWarnings("unchecked")
    public <T extends Tag> T get(String key) {
        return (T) value.get(key);
    }

    public boolean contains(String key) {
        return value.containsKey(key);
    }

    public void put(Tag tag) {
        if (tag == null || tag.getName() == null || tag.getName().isEmpty()) {
            throw new IllegalArgumentException("Cannot put unnamed tag into a compound tag");
        }
        value.put(tag.getName(), tag);
    }

    public Tag put(String key, Object val) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Tag key cannot be empty");
        }
        Tag tag = fromObject(val);
        return value.put(key, tag);
    }

    public Tag remove(String key) {
        return value.remove(key);
    }

    public Map<String, Tag> getMap() {
        return value;
    }

    public int size() {
        return value.size();
    }

    private static Tag fromObject(Object val) {
        if (val instanceof Tag) {
            return (Tag) val;
        }
        if (val instanceof String) {
            return new StringTag("", (String) val);
        }
        if (val instanceof Byte) {
            return new ByteTag("", (Byte) val);
        }
        if (val instanceof Short) {
            return new ShortTag("", (Short) val);
        }
        if (val instanceof Integer) {
            return new IntTag("", (Integer) val);
        }
        if (val instanceof Long) {
            return new LongTag("", (Long) val);
        }
        if (val instanceof Float) {
            return new FloatTag("", (Float) val);
        }
        if (val instanceof Double) {
            return new DoubleTag("", (Double) val);
        }
        if (val instanceof byte[]) {
            return new ByteArrayTag("", (byte[]) val);
        }
        if (val instanceof int[]) {
            return new IntArrayTag("", (int[]) val);
        }
        if (val instanceof long[]) {
            return new LongArrayTag("", (long[]) val);
        }
        throw new IllegalArgumentException("Unsupported tag value: " + val);
    }
}
