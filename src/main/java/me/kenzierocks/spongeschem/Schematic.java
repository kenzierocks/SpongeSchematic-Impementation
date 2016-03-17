package me.kenzierocks.spongeschem;

import java.util.Collection;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

/**
 * An immutable schematic.
 */
@AutoValue
public abstract class Schematic {

    public static final int CURRENT_SCHEMATIC_VERSION = 1;

    public static final Builder builder() {
        return new AutoValue_Schematic.Builder()
                .version(CURRENT_SCHEMATIC_VERSION).offset(Schematic3PointI.ZERO)
                .entities(ImmutableList.of()).tileEntities(ImmutableList.of());
    }

    @AutoValue.Builder
    public static abstract class Builder {

        public abstract Builder version(int version);

        public abstract Builder metadata(Metadata metadata);

        public abstract Builder offset(Schematic3PointI offset);

        public abstract Builder blockData(BlockData blockData);

        public abstract Builder tileEntities(
                Collection<? extends SchematicTileEntity> blockData);

        public abstract Builder
                entities(Collection<? extends SchematicEntity> blockData);

        abstract Schematic autoValueBuild();

        public final Schematic build() {
            Schematic schem = autoValueBuild();
            // TODO: post-build checks
            return schem;
        }

    }

    Schematic() {
    }

    public abstract int getVersion();

    public abstract Metadata getMetadata();

    public abstract Schematic3PointI getOffset();

    public abstract BlockData getBlockData();

    public abstract ImmutableList<SchematicTileEntity> getTileEntities();

    public abstract ImmutableList<SchematicEntity> getEntities();

}
