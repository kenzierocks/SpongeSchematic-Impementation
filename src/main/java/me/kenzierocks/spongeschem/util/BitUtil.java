package me.kenzierocks.spongeschem.util;

import static com.google.common.base.Preconditions.checkArgument;

import me.kenzierocks.spongeschem.ByteArrayBitSet;

public final class BitUtil {

    /**
     * Collect all bits from {@code index} to {@code index + length - 1} using
     * {@code bitAccess}.
     */
    public static int collectBits(int index, int length,
            ByteArrayBitSet bitAccess) {
        checkArgument(length <= Integer.SIZE, "too many bits!");
        int max = index + length;
        int collector = 0;
        for (int i = index; i < max; i++) {
            int bitAt = bitAccess.get(i) ? 1 : 0;
            int shiftAmount = (max - i - 1);
            int offsetBit = bitAt << shiftAmount;
            collector |= offsetBit;
        }
        return collector;
    }

    /**
     * Spread all bits from {@code data} over {@code index} to
     * {@code index + length - 1} using {@code bitAccess}.
     */
    public static void spreadBits(int data, int index, int length,
            ByteArrayBitSet.Mutable bitAccess) {
        checkArgument(length <= Integer.SIZE, "too many bits!");
        int max = index + length;
        for (int i = index; i < max; i++) {
            int shiftAmount = (max - i - 1);
            boolean bitAt = (data & (1 << shiftAmount)) != 0;
            bitAccess.set(i, bitAt);
        }
    }

    private BitUtil() {
        throw new AssertionError();
    }

}
