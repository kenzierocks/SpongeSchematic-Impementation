package me.kenzierocks.spongeschem;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.util.BitSet;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Maps;

public abstract class Palette {

    public static Mutable mutable() {
        return new Mutable();
    }

    public static Mutable mutable(Map<ResourceLocation, Integer> source) {
        return new Mutable(source);
    }

    public static final class Mutable extends Palette {

        private Mutable() {
            super(HashBiMap.create());
        }

        private Mutable(Map<ResourceLocation, Integer> source) {
            super(HashBiMap.create(source));
        }

        private int findUnusedIndex() {
            return this.usedIndicies.nextClearBit(0);
        }

        public void add(ResourceLocation id) {
            put(id, findUnusedIndex());
        }

        public void put(ResourceLocation id, int index) {
            checkArgument(!this.usedIndicies.get(index),
                    "cannot use index %s, it is already present", index);
            this.usedIndicies.set(index);
            this.blockIdMap.put(id, index);
        }

        public void clear(ResourceLocation id) {
            Optional.ofNullable(this.blockIdMap.remove(id))
                    .ifPresent(index -> this.usedIndicies.clear(index));
        }

    }

    public static Immutable immutable() {
        return new Immutable();
    }

    public static Immutable immutable(Map<ResourceLocation, Integer> source) {
        return new Immutable(source);
    }

    public static final class Immutable extends Palette {

        private Immutable() {
            super(ImmutableBiMap.of());
        }

        private Immutable(Map<ResourceLocation, Integer> source) {
            super(ImmutableBiMap.copyOf(source));
        }

    }

    protected final BitSet usedIndicies = new BitSet();
    private transient final BiMap<ResourceLocation, Integer> viewOfBlockIdMap;
    protected final BiMap<ResourceLocation, Integer> blockIdMap;

    protected Palette(BiMap<ResourceLocation, Integer> blockIdMap) {
        this.blockIdMap = blockIdMap;
        this.blockIdMap.values().forEach(this.usedIndicies::set);
        this.viewOfBlockIdMap = Maps.unmodifiableBiMap(this.blockIdMap);
    }

    public Optional<ResourceLocation> getIdFromIndex(int index) {
        return Optional.ofNullable(this.blockIdMap.inverse().get(index));
    }

    public OptionalInt getIndexFromId(ResourceLocation id) {
        if (!this.blockIdMap.containsKey(id)) {
            return OptionalInt.empty();
        }
        return OptionalInt.of(this.blockIdMap.get(id));
    }

    public int getMax() {
        return this.usedIndicies.length();
    }

    public int getBitsPerBlock() {
        // This is the `ceil(lg(length))` specified in the schematic spec
        // But way more optimized.
        // Special case: empty values should be 0
        int max = getMax();
        if (max == 0) {
            return 0;
        }
        // Special case: with a max of 1, we still need 1 bit to tell us there's
        // "info"
        if (max == 1) {
            return 1;
        }
        return Integer.SIZE - Integer.numberOfLeadingZeros(max - 1);
    }

    public BiMap<ResourceLocation, Integer> getBlockIdMap() {
        return this.viewOfBlockIdMap;
    }

    public Immutable toImmutable() {
        if (this instanceof Immutable) {
            return (Immutable) this;
        }
        return new Immutable(this.blockIdMap);
    }

    public Mutable toMutable() {
        if (this instanceof Mutable) {
            return (Mutable) this;
        }
        return new Mutable(this.blockIdMap);
    }

    /**
     * Verifies that all entries are {@code < (paletteMax - 1)}. Also verifies
     * that our {@link #getMax()} is the same as {@code paletteMax}.
     */
    public void verifyMax(int paletteMax) {
        checkArgument(paletteMax == getMax(),
                "incorrect paletteMax %s, should be %s", paletteMax, getMax());
        if (paletteMax == 0) {
            checkState(this.usedIndicies.isEmpty(),
                    "found more than 0 entries");
            return;
        }
        checkState(this.usedIndicies.nextSetBit(paletteMax) == -1,
                "entry greater than or equal to %s", paletteMax);
    }

}
