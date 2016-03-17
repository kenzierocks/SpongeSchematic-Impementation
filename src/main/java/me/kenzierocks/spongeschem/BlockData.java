package me.kenzierocks.spongeschem;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import me.kenzierocks.spongeschem.util.BitUtil;

public abstract class BlockData {

    public static Mutable mutable(Schematic3PointI dimensions) {
        return mutable(dimensions, Pallette.mutable(),
                ByteArrayBitSet.mutable());
    }

    public static Mutable mutable(Schematic3PointI dimensions,
            Pallette pallette, ByteArrayBitSet bitset) {
        return new Mutable(dimensions, pallette, bitset);
    }

    public static final class Mutable extends BlockData {

        private Mutable(Schematic3PointI dimensions, Pallette pallette,
                ByteArrayBitSet sourceSet) {
            super(dimensions, pallette.toMutable(),
                    ByteArrayBitSet.mutable(sourceSet.getData()));
        }

        public void setBlock(Schematic3PointI position, ResourceLocation id) {
            BitUtil.spreadBits(this.pallete.getIndexFromId(id),
                    computeBitIndex(position), this.pallete.getBitsPerBlock(),
                    this.blockData.toMutable());
        }

    }

    public static Immutable immutable(Schematic3PointI dimensions) {
        return new Immutable(dimensions, Pallette.immutable(),
                ByteArrayBitSet.immutable());
    }

    public static Immutable immutable(Schematic3PointI dimensions,
            Pallette pallette, ByteArrayBitSet bitset) {
        return new Immutable(dimensions, pallette, bitset);
    }

    public static final class Immutable extends BlockData {

        private Immutable(Schematic3PointI dimensions, Pallette pallette,
                ByteArrayBitSet sourceSet) {
            super(dimensions, pallette.toImmutable(), sourceSet.toImmutable());
        }

    }

    private final Schematic3PointI dimensions;
    protected final Pallette pallete;
    protected final ByteArrayBitSet blockData;

    private final int checkUShort(int ushort, String name) {
        final int USHORT_MAX_VALUE = 65535;
        checkState(ushort <= USHORT_MAX_VALUE, "%s must be an unsigned short",
                name);
        checkState(ushort >= 0, "%s must be an unsigned short", name);
        return ushort;
    }

    private final Schematic3PointI checkUShort(Schematic3PointI pt,
            String name) {
        checkUShort(pt.getX(), name + ".x");
        checkUShort(pt.getY(), name + ".y");
        checkUShort(pt.getZ(), name + ".z");
        return pt;
    }

    protected BlockData(Schematic3PointI dimensions, Pallette pallette,
            ByteArrayBitSet blockData) {
        this.dimensions = checkUShort(dimensions, "dimensions");
        this.pallete = checkNotNull(pallette);
        this.blockData = checkNotNull(blockData);
    }

    public Pallette getPallete() {
        return this.pallete;
    }

    protected int computeBitIndex(Schematic3PointI pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        int w = getDimensions().getX();
        int h = getDimensions().getY();
        checkArgument(x >= 0, "must be positive");
        checkArgument(y >= 0, "must be positive");
        checkArgument(z >= 0, "must be positive");
        return x + y * w + z * w * h;
    }

    public final ResourceLocation getBlock(Schematic3PointI pos) {
        int index = computeBitIndex(pos);
        int bpb = this.pallete.getBitsPerBlock();
        int palleteIndex = BitUtil.collectBits(index, bpb, this.blockData);
        return this.pallete.getIdFromIndex(palleteIndex);
    }

    public Schematic3PointI getDimensions() {
        return this.dimensions;
    }

}
