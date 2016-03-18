package me.kenzierocks.spongeschem;

import static com.google.common.base.Preconditions.checkState;

import java.util.List;
import java.util.stream.Collectors;

import org.jnbt.ByteArrayTag;
import org.jnbt.ByteTag;
import org.jnbt.CompoundTag;
import org.jnbt.DoubleTag;
import org.jnbt.FloatTag;
import org.jnbt.IntArrayTag;
import org.jnbt.IntTag;
import org.jnbt.ListTag;
import org.jnbt.LongTag;
import org.jnbt.ShortTag;
import org.jnbt.StringTag;
import org.jnbt.Tag;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;

/**
 * A small helper class so that tests can be designed with ease.
 */
public final class XmlToNbt {

    private static final String COMPOUND = "compund";
    private static final String BYTE_ARR = "bytearray";
    private static final String BYTE = "byte";
    private static final String DOUBLE = "double";
    private static final String FLOAT = "float";
    private static final String INT_ARR = "intarray";
    private static final String INT = "int";
    private static final String LIST = "list";
    private static final String LONG = "long";
    private static final String SHORT = "short";
    private static final String STRING = "string";

    public static CompoundTag transform(XMLDocument obj) {
        List<XML> compoundTags = obj.nodes("//" + COMPOUND);
        checkState(compoundTags.size() == 1,
                "too many root compound tags in %s", obj);
        CompoundTag tag = parseCompoundTag(compoundTags.get(0), true);
        return tag;
    }

    private static String getName(XML xml) {
        List<String> nameAttr = xml.xpath("@name");
        checkState(nameAttr.size() == 1, "name is required for tags");
        return nameAttr.get(0);
    }

    private static Tag parseTag(XML xml) {
        String type = xml.node().getNodeName();
        if (type.equals(COMPOUND)) {
            return parseCompoundTag(xml);
        } else if (type.equals(BYTE_ARR)) {
            return parseByteArray(xml);
        } else if (type.equals(BYTE)) {
            return parseByte(xml);
        } else if (type.equals(DOUBLE)) {
            return parseDouble(xml);
        } else if (type.equals(FLOAT)) {
            return parseFloat(xml);
        } else if (type.equals(INT_ARR)) {
            return parseIntArray(xml);
        } else if (type.equals(INT)) {
            return parseInt(xml);
        } else if (type.equals(LIST)) {
            return parseList(xml);
        } else if (type.equals(LONG)) {
            return parseLong(xml);
        } else if (type.equals(SHORT)) {
            return parseShort(xml);
        } else if (type.equals(STRING)) {
            return parseString(xml);
        } else {
            throw new UnsupportedOperationException(
                    "Don't know what a " + type + " is");
        }
    }

    private static CompoundTag parseCompoundTag(XML xml) {
        return parseCompoundTag(xml, false);
    }

    private static CompoundTag parseCompoundTag(XML xml, boolean root) {
        List<XML> items = xml.nodes("*");
        String name;
        if (root) {
            name = "";
        } else {
            name = getName(xml);
        }
        ImmutableMap.Builder<String, Tag> data = ImmutableMap.builder();
        for (XML mapItem : items) {
            Tag tag = parseTag(mapItem);
            data.put(tag.getName(), tag);
        }
        return new CompoundTag(name, data.build());
    }

    private static ByteArrayTag parseByteArray(XML xml) {
        String name = getName(xml);
        List<XML> bytes = xml.nodes(BYTE);
        byte[] value = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            value[i] = parseByte(bytes.get(i)).getValue();
        }
        return new ByteArrayTag(name, value);
    }

    private static ByteTag parseByte(XML xml) {
        String name = getName(xml);
        List<String> val = xml.xpath("text()");
        checkState(val.size() == 1, "need a value");
        return new ByteTag(name, Byte.parseByte(val.get(0)));
    }

    private static DoubleTag parseDouble(XML xml) {
        String name = getName(xml);
        List<String> val = xml.xpath("text()");
        checkState(val.size() == 1, "need a value");
        return new DoubleTag(name, Double.parseDouble(val.get(0)));
    }

    private static FloatTag parseFloat(XML xml) {
        String name = getName(xml);
        List<String> val = xml.xpath("text()");
        checkState(val.size() == 1, "need a value");
        return new FloatTag(name, Float.parseFloat(val.get(0)));
    }

    private static IntArrayTag parseIntArray(XML xml) {
        String name = getName(xml);
        List<XML> ints = xml.nodes(INT);
        int[] value =
                ints.stream().mapToInt(x -> parseInt(x).getValue()).toArray();
        return new IntArrayTag(name, value);
    }

    private static IntTag parseInt(XML xml) {
        String name = getName(xml);
        List<String> val = xml.xpath("text()");
        checkState(val.size() == 1, "need a value");
        return new IntTag(name, Integer.parseInt(val.get(0)));
    }

    private static ListTag parseList(XML xml) {
        String name = getName(xml);
        List<XML> tags = xml.nodes("*");
        List<Tag> value = FluentIterable.from(tags)
                .transform(XmlToNbt::parseTag).toList();
        // WTF java generics
        @SuppressWarnings("unchecked")
        List<Class<? extends Tag>> tagClasses =
                (List<Class<? extends Tag>>) (List<?>) value.stream()
                        .map(Object::getClass).distinct()
                        .collect(Collectors.toList());
        checkState(tagClasses.size() == 1,
                "multiple tag classes in one list: %s", tagClasses);
        return new ListTag(name, tagClasses.get(0), value);
    }

    private static LongTag parseLong(XML xml) {
        String name = getName(xml);
        List<String> val = xml.xpath("text()");
        checkState(val.size() == 1, "need a value");
        return new LongTag(name, Long.parseLong(val.get(0)));
    }

    private static ShortTag parseShort(XML xml) {
        String name = getName(xml);
        List<String> val = xml.xpath("text()");
        checkState(val.size() == 1, "need a value");
        return new ShortTag(name, Short.parseShort(val.get(0)));
    }

    private static StringTag parseString(XML xml) {
        String name = getName(xml);
        List<String> val = xml.xpath("text()");
        return new StringTag(name, String.join("", val));
    }

    private XmlToNbt() {
        throw new AssertionError();
    }

}
