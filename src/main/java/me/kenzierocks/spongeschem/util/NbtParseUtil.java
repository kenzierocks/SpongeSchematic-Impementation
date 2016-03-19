package me.kenzierocks.spongeschem.util;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collection;

import org.jnbt.ByteArrayTag;
import org.jnbt.IntTag;
import org.jnbt.ListTag;
import org.jnbt.ShortTag;
import org.jnbt.StringTag;
import org.jnbt.Tag;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;

/**
 * Parsing utilities for NBT data. Many of these methods will return
 * {@code null} when given {@code null}.
 */
public final class NbtParseUtil {

    public static String parseString(Tag tag) {
        if (tag instanceof StringTag) {
            return ((StringTag) tag).getValue();
        }
        return null;
    }

    public static Collection<String> parseRequiredModsData(Tag list) {
        if (!(list instanceof ListTag)) {
            return ImmutableList.of();
        }
        return FluentIterable.from(((ListTag) list).getValue())
                .transform(tag -> ((StringTag) tag).getValue()).toList();
    }

    public static ZonedDateTime parseDate(Tag value) {
        String data = parseString(value);
        if (data == null) {
            return null;
        }
        try {
            return DateTimeFormatter.ISO_ZONED_DATE_TIME.parse(data,
                    ZonedDateTime::from);
        } catch (DateTimeParseException badDate) {
            return null;
        }
    }

    public static int parseUnsignedShort(Tag value) {
        if (value instanceof ShortTag) {
            ShortTag shortTag = (ShortTag) value;
            return Short.toUnsignedInt(shortTag.getValue());
        }
        throw new IllegalStateException("unexpected non-ShortTag " + value);
    }

    public static int parseSignedInt(Tag value) {
        if (value instanceof IntTag) {
            IntTag intTag = (IntTag) value;
            return intTag.getValue();
        }
        throw new IllegalStateException("unexpected non-IntTag " + value);
    }

    public static byte[] parseByteArray(Tag value) {
        if (value instanceof ByteArrayTag) {
            return ((ByteArrayTag) value).getValue();
        }
        throw new IllegalStateException("unexpected non-ByteArrayTag " + value);
    }

    private NbtParseUtil() {
    }

}
