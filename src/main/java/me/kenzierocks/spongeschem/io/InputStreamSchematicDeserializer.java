package me.kenzierocks.spongeschem.io;

import static com.google.common.base.Preconditions.checkState;
import static me.kenzierocks.spongeschem.util.NbtParseUtil.parseByteArray;
import static me.kenzierocks.spongeschem.util.NbtParseUtil.parseDate;
import static me.kenzierocks.spongeschem.util.NbtParseUtil.parseRequiredModsData;
import static me.kenzierocks.spongeschem.util.NbtParseUtil.parseSignedInt;
import static me.kenzierocks.spongeschem.util.NbtParseUtil.parseString;
import static me.kenzierocks.spongeschem.util.NbtParseUtil.parseUnsignedShort;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jnbt.CompoundTag;
import org.jnbt.IntTag;
import org.jnbt.ListTag;
import org.jnbt.NBTInputStream;
import org.jnbt.Tag;

import me.kenzierocks.spongeschem.BlockData;
import me.kenzierocks.spongeschem.ByteArrayBitSet;
import me.kenzierocks.spongeschem.Metadata;
import me.kenzierocks.spongeschem.Palette;
import me.kenzierocks.spongeschem.ResourceLocation;
import me.kenzierocks.spongeschem.Schematic;
import me.kenzierocks.spongeschem.Schematic.Builder;
import me.kenzierocks.spongeschem.Schematic3PointI;

public class InputStreamSchematicDeserializer {

    private static final String VERSION_TAG = "Version";

    private static final String METADATA_TAG = "Metadata";
    private static final String METADATA_NAME_KEY = "Name";
    private static final String METADATA_AUTHOR_KEY = "Author";
    private static final String METADATA_DATE_KEY = "Metadata";
    private static final String METADATA_REQUIRED_MODS_KEY = "Metadata";

    private static final String OFFSET_TAG = "Offset";

    private static final String WIDTH_TAG = "Width";
    private static final String HEIGHT_TAG = "Height";
    private static final String LENGTH_TAG = "Length";

    private static final String PALETTE_MAX_TAG = "PaletteMax";
    private static final String PALETTE_TAG = "Palette";

    private static final String BLOCK_DATA_TAG = "BlockData";

    public InputStreamSchematicDeserializer() {
    }

    private Schematic readCurrentSchematic(int version, CompoundTag base) {
        Schematic.Builder builder = Schematic.builder();
        builder.version(version);
        Map<String, Tag> data = base.getValue();
        addMetadata(builder, (CompoundTag) data.get(METADATA_TAG));
        addOffset(builder, (ListTag) data.get(OFFSET_TAG));
        // Read width, height, length.
        int width = parseUnsignedShort(data.get(WIDTH_TAG));
        int height = parseUnsignedShort(data.get(HEIGHT_TAG));
        int length = parseUnsignedShort(data.get(LENGTH_TAG));
        int paletteMax = parseSignedInt(data.get(PALETTE_MAX_TAG));
        // Complex code to transform
        // Map<String, Tag> to Map<ResourceLocation, Integer>
        Palette palette =
                Palette.immutable(((CompoundTag) data.get(PALETTE_TAG))
                        .getValue().entrySet().stream()
                        .collect(Collectors.toMap(
                                e -> ResourceLocation.fromString(e.getKey()),
                                e -> ((IntTag) e.getValue()).getValue())));
        palette.verifyMax(paletteMax);
        BlockData blockData = BlockData.immutable(
                Schematic3PointI.create(width, height, length),
                palette, ByteArrayBitSet
                        .immutable(parseByteArray(data.get(BLOCK_DATA_TAG))));
        builder.blockData(blockData);
        return builder.build();
    }

    private void addMetadata(Builder builder, CompoundTag metadataTag) {
        if (metadataTag != null) {
            Map<String, Tag> data = metadataTag.getValue();
            builder.metadata(
                    Metadata.of(parseString(data.get(METADATA_NAME_KEY)),
                            parseString(data.get(METADATA_AUTHOR_KEY)),
                            parseDate(data.get(METADATA_DATE_KEY)),
                            parseRequiredModsData(
                                    data.get(METADATA_REQUIRED_MODS_KEY))));
        }
    }

    private void addOffset(Builder builder, ListTag offsetTag) {
        if (offsetTag != null) {
            List<Tag> data = offsetTag.getValue();
            checkState(data.size() == 3, "bad offset tag");
            int[] offset = data.stream()
                    .mapToInt(tag -> ((IntTag) tag).getValue()).toArray();
            builder.offset(Schematic3PointI.create(offset[0],
                    offset[1], offset[2]));
        }
    }

    private Schematic upgradeSchematic(int version, CompoundTag base) {
        if (version == Schematic.CURRENT_SCHEMATIC_VERSION) {
            return readCurrentSchematic(version, base);
        }
        if (version > Schematic.CURRENT_SCHEMATIC_VERSION) {
            throw new UnsupportedOperationException("Schematic version "
                    + version + " is higher than the supported version ("
                    + Schematic.CURRENT_SCHEMATIC_VERSION + ")");
        }
        throw new UnsupportedOperationException("Don't know how to upgrade "
                + version + " to " + Schematic.CURRENT_SCHEMATIC_VERSION);
    }

    public Schematic decode(InputStream input) throws IOException {
        CompoundTag nbtBase;
        try (
                NBTInputStream nbt = new NBTInputStream(input)) {
            // All first tags are compoundtags
            nbtBase = (CompoundTag) nbt.readTag();
        }
        // Perform upgrade if required
        int version = ((IntTag) nbtBase.getValue().get(VERSION_TAG)).getValue();
        return upgradeSchematic(version, nbtBase);
    }

}
