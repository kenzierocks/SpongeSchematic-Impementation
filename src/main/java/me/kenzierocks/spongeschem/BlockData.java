package me.kenzierocks.spongeschem;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import me.kenzierocks.spongeschem.util.BitUtil;

public abstract class BlockData {

    public static Mutable mutable(int width, int height, int length) {
        return new Mutable(width, height, length, Pallette.mutable(),
                ByteArrayBitSet.mutable());
    }

    public static Mutable mutable(int width, int height, int length,
            Pallette pallette, ByteArrayBitSet bitset) {
        return new Mutable(width, height, length, pallette, bitset);
    }

    public static final class Mutable extends BlockData {

        private Mutable(int width, int height, int length, Pallette pallette,
                ByteArrayBitSet sourceSet) {
            super(width, height, length, pallette.toMutable(),
                    ByteArrayBitSet.mutable(sourceSet.getData()));
        }

        public void setBlock(int x, int y, int z, String id) {
            BitUtil.spreadBits(this.pallete.getIndexFromId(id),
                    computeBitIndex(x, y, z), this.pallete.getBitsPerBlock(),
                    this.blockData.toMutable());
        }

    }

    public static Immutable immutable(int width, int height, int length) {
        return new Immutable(width, height, length, Pallette.immutable(),
                ByteArrayBitSet.immutable());
    }

    public static Immutable immutable(int width, int height, int length,
            Pallette pallette, ByteArrayBitSet bitset) {
        return new Immutable(width, height, length, pallette, bitset);
    }

    public static final class Immutable extends BlockData {

        private Immutable(int width, int height, int length, Pallette pallette,
                ByteArrayBitSet sourceSet) {
            super(width, height, length, pallette.toImmutable(),
                    sourceSet.toImmutable());
        }

    }

    private final int width;
    private final int height;
    private final int length;
    protected final Pallette pallete;
    protected final ByteArrayBitSet blockData;

    private final int checkUShort(int ushort, String name) {
        final int USHORT_MAX_VALUE = 65535;
        checkState(ushort <= USHORT_MAX_VALUE, "%s must be an unsigned short",
                name);
        checkState(ushort >= 0, "%s must be an unsigned short", name);
        return ushort;
    }

    protected BlockData(int width, int height, int length, Pallette pallette,
            ByteArrayBitSet blockData) {
        this.width = checkUShort(width, "width");
        this.height = checkUShort(height, "height");
        this.length = checkUShort(length, "length");
        this.pallete = checkNotNull(pallette);
        this.blockData = checkNotNull(blockData);
    }

    public Pallette getPallete() {
        return this.pallete;
    }

    protected int computeBitIndex(int x, int y, int z) {
        checkArgument(x >= 0, "must be positive");
        checkArgument(y >= 0, "must be positive");
        checkArgument(z >= 0, "must be positive");
        return x + y * getWidth() + z * getWidth() * getHeight();
    }

    public final String getBlock(int x, int y, int z) {
        int index = computeBitIndex(x, y, z);
        int bpb = this.pallete.getBitsPerBlock();
        int palleteIndex = BitUtil.collectBits(index, bpb, this.blockData);
        return this.pallete.getIdFromIndex(palleteIndex);
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getLength() {
        return this.length;
    }

}
