package me.kenzierocks.spongeschem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.IntPredicate;

import org.junit.Test;

import com.google.common.base.Throwables;

import me.kenzierocks.spongeschem.util.BitUtil;

public class BitUtilTest {

    private static ByteArrayBitSet preset(int size, IntPredicate test) {
        ByteArrayBitSet.Mutable set = ByteArrayBitSet.mutable();
        for (int i = 0; i < size; i++) {
            set.set(i, test.test(i));
        }
        return set;
    }

    @Test
    public void collectZeroBits() {
        int bits = BitUtil.collectBits(0, 0, preset(0, i -> false));
        assertEquals("unequal bits", 0, bits);
    }

    @Test
    public void collectTenZeroBits() {
        int bits = BitUtil.collectBits(0, 10, preset(10, i -> false));
        assertEquals("unequal bits", 0, bits);
    }

    @Test
    public void collectOneOneBit() {
        int bits = BitUtil.collectBits(0, 1, preset(1, i -> true));
        assertEquals("unequal bits", 1, bits);
    }

    @Test
    public void collectTwoOneBits() {
        int bits = BitUtil.collectBits(0, 2, preset(2, i -> true));
        assertEquals("unequal bits", 1 | 2, bits);
    }

    @Test
    public void collectOneOffsetOneBit() {
        int bits = BitUtil.collectBits(9, 1, preset(10, i -> true));
        assertEquals("unequal bits", 1, bits);
    }

    @Test
    public void collectTwoOffsetOneBits() {
        int bits = BitUtil.collectBits(8, 2, preset(10, i -> true));
        assertEquals("unequal bits", 1 | 2, bits);
    }

    @Test
    public void collectTheNumber42() {
        ByteArrayBitSet.Mutable bitSet = ByteArrayBitSet.mutable();
        // 42 = 0b101010 (012345)
        bitSet.set(0, true);
        bitSet.set(1, false);
        bitSet.set(2, true);
        bitSet.set(3, false);
        bitSet.set(4, true);
        bitSet.set(5, false);
        int bits = BitUtil.collectBits(0, 6, bitSet);
        assertEquals("unequal bits", 42, bits);
    }

    @Test
    public void collectTooManyBits() {
        try {
            BitUtil.collectBits(0, Integer.MAX_VALUE, null);
            fail("Collecting >32 bits should be impossible.");
        } catch (IllegalArgumentException expected) {
            // ok
        }
    }

    @Test
    public void unconstructable() throws Throwable {
        try {
            try {
                Constructor<BitUtil> bitUtilCons =
                        BitUtil.class.getDeclaredConstructor();
                bitUtilCons.setAccessible(true);
                BitUtil bitUtil = bitUtilCons.newInstance();
                fail("Was able to create a bit util " + bitUtil);
            } catch (NoSuchMethodException | InstantiationException
                    | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException ex) {
                if (ex instanceof InvocationTargetException) {
                    InvocationTargetException wrapper =
                            (InvocationTargetException) ex;
                    throw wrapper.getCause();
                }
                throw Throwables.propagate(ex);
            }
        } catch (AssertionError expected) {
            // ok
        }
    }

}
