package me.kenzierocks.spongeschem;

import static org.junit.Assert.*;

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
    public void spreadZeroBits() {
        ByteArrayBitSet.Mutable bitSet = ByteArrayBitSet.mutable();
        BitUtil.spreadBits(0, 0, 0, bitSet);
        assertFalse("bit spread occured", bitSet.get(0));
    }

    @Test
    public void spreadTenZeroBits() {
        ByteArrayBitSet.Mutable bitSet = ByteArrayBitSet.mutable();
        BitUtil.spreadBits(0, 0, 10, bitSet);
        for (int i = 0; i < 10; i++) {
            assertFalse("bit spread occured at " + i, bitSet.get(i));
        }
    }

    @Test
    public void spreadOneOneBit() {
        ByteArrayBitSet.Mutable bitSet = ByteArrayBitSet.mutable();
        BitUtil.spreadBits(1, 0, 1, bitSet);
        assertTrue("bit spread did not occur", bitSet.get(0));
    }

    @Test
    public void spreadTwoOneBits() {
        ByteArrayBitSet.Mutable bitSet = ByteArrayBitSet.mutable();
        BitUtil.spreadBits(1 | 2, 0, 2, bitSet);
        assertTrue("bit spread did not occur", bitSet.get(0));
        assertTrue("bit spread did not occur", bitSet.get(1));
    }

    @Test
    public void spreadOneOffsetOneBit() {
        ByteArrayBitSet.Mutable bitSet = ByteArrayBitSet.mutable();
        BitUtil.spreadBits(1, 9, 1, bitSet);
        for (int i = 0; i < 9; i++) {
            assertFalse("bit spread occured at " + i, bitSet.get(i));
        }
        assertTrue("bit spread did not occur", bitSet.get(9));
        assertFalse("bit spread occured", bitSet.get(10));
    }

    @Test
    public void spreadTwoOffsetOneBits() {
        ByteArrayBitSet.Mutable bitSet = ByteArrayBitSet.mutable();
        BitUtil.spreadBits(1 | 2, 9, 2, bitSet);
        for (int i = 0; i < 8; i++) {
            assertFalse("bit spread occured at " + i, bitSet.get(i));
        }
        assertTrue("bit spread did not occur", bitSet.get(9));
        assertTrue("bit spread did not occur", bitSet.get(10));
        assertFalse("bit spread occured", bitSet.get(11));
    }

    @Test
    public void spreadTheNumber42() {
        ByteArrayBitSet.Mutable expected = ByteArrayBitSet.mutable();
        // 42 = 0b101010 (012345)
        expected.set(0, true);
        expected.set(1, false);
        expected.set(2, true);
        expected.set(3, false);
        expected.set(4, true);
        expected.set(5, false);
        ByteArrayBitSet.Mutable bitSet = ByteArrayBitSet.mutable();
        BitUtil.spreadBits(42, 0, 6, bitSet);
        assertEquals("unequal bits", expected, bitSet);
    }

    @Test
    public void spreadTooManyBits() {
        try {
            BitUtil.spreadBits(0, 0, Integer.MAX_VALUE, null);
            fail("Spreading >32 bits should be impossible.");
        } catch (IllegalArgumentException expected) {
            // ok
        }
    }

    @Test
    public void collectReversesSpread() throws Exception {
        for (int i = 2; i < 100; i++) {
            ByteArrayBitSet.Mutable spreaded = ByteArrayBitSet.mutable();
            int bitLen = 32 - Integer.numberOfLeadingZeros(i - 1) + 1; // ???
            BitUtil.spreadBits(i, 0, bitLen, spreaded);
            int collected = BitUtil.collectBits(0, bitLen, spreaded);
            assertEquals("collect didn't reverse spread (spreaded: " + spreaded
                    + ", bitLen: " + bitLen + ")", i, collected);
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
