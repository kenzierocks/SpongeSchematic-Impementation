package me.kenzierocks.spongeschem;

import java.util.Arrays;
import java.util.BitSet;

/**
 * Because {@link BitSet} is too complex and not immutable.
 */
public abstract class ByteArrayBitSet {

    private static final byte[] EMPTY_BYTE = {};

    private static int getIndex(int bitIndex) {
        return bitIndex / 8;
    }

    private static int getBitOffset(int bitIndex) {
        // Swap the direction to account for endianess
        return 7 - (bitIndex % 8);
    }

    public static Mutable mutable() {
        return mutable(EMPTY_BYTE);
    }

    public static Mutable mutable(byte[] init) {
        return new Mutable(init);
    }

    public static final class Mutable extends ByteArrayBitSet {

        private byte[] data;
        private transient String toStringCache;

        private Mutable(byte[] init) {
            this.data = init.clone();
        }

        // growth code shamelessly stolen from ArrayList
        private void ensureCapacity(int minCapacity) {
            // Convert minCapacity to the bytes needed
            int off = minCapacity % 8;
            minCapacity /= Byte.SIZE;
            if (off > 0) {
                minCapacity += 8;
            }
            // overflow-conscious code
            if (minCapacity - this.data.length > 0) {
                grow(minCapacity);
            }
        }

        /**
         * The maximum size of array to allocate. Some VMs reserve some header
         * words in an array. Attempts to allocate larger arrays may result in
         * OutOfMemoryError: Requested array size exceeds VM limit
         */
        private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

        /**
         * Increases the capacity to ensure that it can hold at least the number
         * of elements specified by the minimum capacity argument.
         *
         * @param minCapacity
         *            the desired minimum capacity
         */
        private void grow(int minCapacity) {
            // overflow-conscious code
            int oldCapacity = this.data.length;
            int newCapacity = oldCapacity + (oldCapacity >> 1);
            if (newCapacity - minCapacity < 0)
                newCapacity = minCapacity;
            if (newCapacity - MAX_ARRAY_SIZE > 0)
                newCapacity = hugeCapacity(minCapacity);
            // minCapacity is usually close to size, so this is a win:
            this.data = Arrays.copyOf(this.data, newCapacity);
        }

        private static int hugeCapacity(int minCapacity) {
            if (minCapacity < 0) // overflow
                throw new OutOfMemoryError();
            return (minCapacity > MAX_ARRAY_SIZE) ? Integer.MAX_VALUE
                    : MAX_ARRAY_SIZE;
        }

        public void set(int index, boolean on) {
            ensureCapacity(index + 1);
            boolean prev = get(index);
            int shiftedBit = 1 << getBitOffset(index);
            if (on) {
                this.data[getIndex(index)] |= shiftedBit;
            } else {
                this.data[getIndex(index)] &= ~shiftedBit;
            }
            if (prev != on) {
                this.toStringCache = null;
            }
        }

        @Override
        protected byte[] getDataNoCopy() {
            return this.data;
        }

        @Override
        public String toString() {
            return this.toStringCache == null
                    ? this.toStringCache = super.toString()
                    : this.toStringCache;
        }

    }

    public static Immutable immutable() {
        return immutable(EMPTY_BYTE);
    }

    public static Immutable immutable(byte[] data) {
        return new Immutable(data);
    }

    public static final class Immutable extends ByteArrayBitSet {

        private final byte[] data;
        private transient String toStringCache;

        private Immutable(byte[] data) {
            this.data = data.clone();
        }

        @Override
        protected byte[] getDataNoCopy() {
            return this.data;
        }

        @Override
        public String toString() {
            return this.toStringCache == null
                    ? this.toStringCache = super.toString()
                    : this.toStringCache;
        }

    }

    public boolean get(int index) {
        byte[] data = getDataNoCopy();
        int arrIndex = getIndex(index);
        return arrIndex < data.length
                && (data[arrIndex] & (1 << getBitOffset(index))) != 0;
    }

    public int length() {
        return getDataNoCopy().length;
    }

    public byte[] getData() {
        return getDataNoCopy().clone();
    }

    protected abstract byte[] getDataNoCopy();

    public final Mutable toMutable() {
        return (this instanceof Mutable) ? (Mutable) this
                : mutable(getDataNoCopy());
    }

    public final Immutable toImmutable() {
        return (this instanceof Immutable) ? (Immutable) this
                : immutable(getDataNoCopy());
    }

    @Override
    public String toString() {
        byte[] data = getDataNoCopy();
        StringBuilder b = new StringBuilder(data.length * 8);
        b.append('{');
        for (int i = 0; i < data.length; i++) {
            for (int subI = 7; subI >= 0; subI--) {
                b.append(((data[i] & (1 << subI)) != 0) ? '1' : '0');
            }
            if ((i + 1) < data.length) {
                b.append(',');
            }
        }
        b.append('}');
        return b.toString();
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(getDataNoCopy());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ByteArrayBitSet)) {
            return false;
        }
        ByteArrayBitSet other = (ByteArrayBitSet) obj;
        return Arrays.equals(other.getDataNoCopy(), getDataNoCopy());
    }

}
