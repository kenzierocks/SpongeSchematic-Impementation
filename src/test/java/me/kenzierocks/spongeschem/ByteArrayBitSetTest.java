package me.kenzierocks.spongeschem;

import static org.junit.Assert.assertTrue;

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

}
