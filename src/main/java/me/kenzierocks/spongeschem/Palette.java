package me.kenzierocks.spongeschem;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.BitSet;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;

public abstract class Palette {

    public static Mutable mutable() {
        return new Mutable();
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

    public ResourceLocation getIdFromIndex(int index) {
        return this.blockIdMap.inverse().get(index);
    }

    public int getIndexFromId(ResourceLocation id) {
        return this.blockIdMap.get(id);
    }

    public int getLength() {
        return this.blockIdMap.size();
    }

    public int getBitsPerBlock() {
        // This is the `ceil(lg(length))` specified in the schematic spec
        // But way more optimized.
        return Integer.SIZE - Integer.numberOfLeadingZeros(getLength() - 1);
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

}
