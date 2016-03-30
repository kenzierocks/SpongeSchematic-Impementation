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
    protected final BiMap<ResourceLocation, Integer> blockIdMap;

    protected Palette(BiMap<ResourceLocation, Integer> blockIdMap) {
        this.blockIdMap = blockIdMap;
        this.blockIdMap.values().forEach(this.usedIndicies::set);
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
     * Verifies that all entries are {@code < (paletteMax - 1)}.
     */
    public void verifyMax(int paletteMax) {
        if (paletteMax == 1) {
            checkState(this.usedIndicies.isEmpty(),
                    "found more than 0 entries");
            return;
        }
        checkState(this.usedIndicies.nextSetBit(paletteMax - 1) == -1,
                "entry over %s", paletteMax - 1);
    }

}
