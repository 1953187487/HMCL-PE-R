package com.github.steveice10.opennbt;

import com.github.steveice10.opennbt.tag.builtin.ByteArrayTag;
import com.github.steveice10.opennbt.tag.builtin.ByteTag;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import com.github.steveice10.opennbt.tag.builtin.DoubleTag;
import com.github.steveice10.opennbt.tag.builtin.FloatTag;
import com.github.steveice10.opennbt.tag.builtin.IntArrayTag;
import com.github.steveice10.opennbt.tag.builtin.IntTag;
import com.github.steveice10.opennbt.tag.builtin.ListTag;
import com.github.steveice10.opennbt.tag.builtin.Tag;
import com.github.steveice10.opennbt.tag.builtin.LongArrayTag;
import com.github.steveice10.opennbt.tag.builtin.LongTag;
import com.github.steveice10.opennbt.tag.builtin.ShortTag;
import com.github.steveice10.opennbt.tag.builtin.StringTag;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class NBTIO {

    private NBTIO() {
    }

    public static Tag readTag(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            throw new IOException("Input stream cannot be null");
        }
        DataInputStream dis = new DataInputStream(new BufferedInputStream(inputStream));
        int type = dis.readUnsignedByte();
        if (type == Tag.TAG_END) {
            return null;
        }
        return readTyped(dis, "", type);
    }

    public static void writeTag(OutputStream outputStream, Tag tag) throws IOException {
        if (outputStream == null || tag == null) {
            throw new IOException("Output stream and tag cannot be null");
        }
        DataOutputStream dos = new DataOutputStream(outputStream);
        writeBody(dos, tag);
        dos.flush();
    }

    private static Tag readTyped(DataInputStream dis, String name, int type) throws IOException {
        switch (type) {
            case Tag.TAG_BYTE:
                return new ByteTag(name, dis.readByte());
            case Tag.TAG_SHORT:
                return new ShortTag(name, dis.readShort());
            case Tag.TAG_INT:
                return new IntTag(name, dis.readInt());
            case Tag.TAG_LONG:
                return new LongTag(name, dis.readLong());
            case Tag.TAG_FLOAT:
                return new FloatTag(name, dis.readFloat());
            case Tag.TAG_DOUBLE:
                return new DoubleTag(name, dis.readDouble());
            case Tag.TAG_BYTE_ARRAY: {
                byte[] data = new byte[dis.readInt()];
                dis.readFully(data);
                return new ByteArrayTag(name, data);
            }
            case Tag.TAG_INT_ARRAY: {
                int size = dis.readInt();
                int[] data = new int[size];
                for (int i = 0; i < size; i++) {
                    data[i] = dis.readInt();
                }
                return new IntArrayTag(name, data);
            }
            case Tag.TAG_LONG_ARRAY: {
                int size = dis.readInt();
                long[] data = new long[size];
                for (int i = 0; i < size; i++) {
                    data[i] = dis.readLong();
                }
                return new LongArrayTag(name, data);
            }
            case Tag.TAG_STRING:
                return new StringTag(name, readString(dis));
            case Tag.TAG_LIST:
                return readList(dis, name);
            case Tag.TAG_COMPOUND:
                return readCompound(dis, name);
            default:
                throw new IOException("Unknown tag type: " + type);
        }
    }

    private static ListTag readList(DataInputStream dis, String name) throws IOException {
        int elementType = dis.readUnsignedByte();
        int size = dis.readInt();
        ListTag list = new ListTag(name, elementType);
        for (int i = 0; i < size; i++) {
            list.put(readTyped(dis, "", elementType));
        }
        return list;
    }

    private static CompoundTag readCompound(DataInputStream dis, String name) throws IOException {
        CompoundTag compound = new CompoundTag(name);
        while (true) {
            int type = dis.readUnsignedByte();
            if (type == Tag.TAG_END) {
                break;
            }
            String tagName = readString(dis);
            compound.put(readTyped(dis, tagName, type));
        }
        return compound;
    }

    private static String readString(DataInputStream dis) throws IOException {
        int length = dis.readUnsignedShort();
        byte[] data = new byte[length];
        dis.readFully(data);
        return new String(data, StandardCharsets.UTF_16);
    }

    private static void writeBody(DataOutputStream dos, Tag tag) throws IOException {
        dos.writeByte(tag.getType());
        writeValue(dos, tag);
    }

    private static void writeNamedBody(DataOutputStream dos, Tag tag) throws IOException {
        dos.writeByte(tag.getType());
        writeString(dos, tag.getName());
        writeValue(dos, tag);
    }

    private static void writeValue(DataOutputStream dos, Tag tag) throws IOException {
        switch (tag.getType()) {
            case Tag.TAG_BYTE:
                dos.writeByte((Byte) tag.getValue());
                break;
            case Tag.TAG_SHORT:
                dos.writeShort((Short) tag.getValue());
                break;
            case Tag.TAG_INT:
                dos.writeInt((Integer) tag.getValue());
                break;
            case Tag.TAG_LONG:
                dos.writeLong((Long) tag.getValue());
                break;
            case Tag.TAG_FLOAT:
                dos.writeFloat((Float) tag.getValue());
                break;
            case Tag.TAG_DOUBLE:
                dos.writeDouble((Double) tag.getValue());
                break;
            case Tag.TAG_BYTE_ARRAY: {
                byte[] data = (byte[]) tag.getValue();
                dos.writeInt(data.length);
                dos.write(data);
                break;
            }
            case Tag.TAG_INT_ARRAY: {
                int[] data = (int[]) tag.getValue();
                dos.writeInt(data.length);
                for (int value : data) {
                    dos.writeInt(value);
                }
                break;
            }
            case Tag.TAG_LONG_ARRAY: {
                long[] data = (long[]) tag.getValue();
                dos.writeInt(data.length);
                for (long value : data) {
                    dos.writeLong(value);
                }
                break;
            }
            case Tag.TAG_STRING:
                writeString(dos, (String) tag.getValue());
                break;
            case Tag.TAG_LIST: {
                ListTag list = (ListTag) tag;
                dos.writeByte(list.getElementType());
                dos.writeInt(list.size());
                for (Tag child : list.getList()) {
                    writeBody(dos, child);
                }
                break;
            }
            case Tag.TAG_COMPOUND: {
                CompoundTag compound = (CompoundTag) tag;
                for (Tag child : compound.getMap().values()) {
                    writeNamedBody(dos, child);
                }
                dos.writeByte(Tag.TAG_END);
                break;
            }
            default:
                throw new IOException("Unsupported tag type: " + tag.getType());
        }
    }

    private static void writeString(DataOutputStream dos, String value) throws IOException {
        if (value == null) {
            value = "";
        }
        byte[] data = value.getBytes(StandardCharsets.UTF_16);
        dos.writeShort(data.length);
        dos.write(data);
    }
}
