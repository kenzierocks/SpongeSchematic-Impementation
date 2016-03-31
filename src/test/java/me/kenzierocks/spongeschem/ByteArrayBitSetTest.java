package me.kenzierocks.spongeschem;

import static org.junit.Assert.*;

import org.junit.Test;

public class ByteArrayBitSetTest {

    @Test
    public void bytesToBitSet() {
        byte[] in = { 0b0000_0001, 0b0000_0001 };
        ByteArrayBitSet out = ByteArrayBitSet.immutable(in);
        String error = "bad bit conversion: " + out;
        assertTrue(error, out.get(7));
        assertTrue(error, out.get(7 + 8));
    }

    @Test
    public void emptyImmutable() {
        ByteArrayBitSet immutable = ByteArrayBitSet.immutable();
        assertEquals(0, immutable.length());
        assertFalse(immutable.get(0));
        assertArrayEquals(immutable.getData(), new byte[0]);
        assertEquals(immutable,
                ByteArrayBitSet.immutable(new byte[0]));
        assertEquals(immutable, immutable.toMutable());
    }

}
