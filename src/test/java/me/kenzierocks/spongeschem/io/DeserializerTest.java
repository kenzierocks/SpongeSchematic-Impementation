package me.kenzierocks.spongeschem.io;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jnbt.CompoundTag;
import org.jnbt.NBTOutputStream;
import org.junit.Test;

import com.jcabi.xml.XMLDocument;

import me.kenzierocks.spongeschem.BlockData;
import me.kenzierocks.spongeschem.Palette;
import me.kenzierocks.spongeschem.ResourceLocation;
import me.kenzierocks.spongeschem.Schematic;
import me.kenzierocks.spongeschem.Schematic3PointI;
import me.kenzierocks.spongeschem.XmlToNbt;

public class DeserializerTest {

    private static CompoundTag getTag(String filename) throws IOException {
        InputStream data = DeserializerTest.class
                .getResourceAsStream("/" + filename + ".xml");
        XMLDocument doc = new XMLDocument(data);
        return XmlToNbt.transform(doc);
    }

    private static byte[] getTagBytes(String filename) throws Exception {
        CompoundTag tag = getTag(filename);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (NBTOutputStream s = new NBTOutputStream(out)) {
            s.writeTag(tag);
        }
        return out.toByteArray();
    }

    private static ResourceLocation[][][] getBlockArray(Schematic schematic) {
        BlockData data = schematic.getBlockData();
        Schematic3PointI dim = data.getDimensions();
        ResourceLocation[][][] result =
                new ResourceLocation[dim.getX()][dim.getY()][dim.getZ()];
        for (int i = 0; i < dim.getX(); i++) {
            for (int j = 0; j < dim.getY(); j++) {
                for (int k = 0; k < dim.getZ(); k++) {
                    result[i][j][k] =
                            data.getBlock(Schematic3PointI.create(i, j, k))
                                    .orElse(null);
                }
            }
        }
        return result;
    }

    private static void assertResEquals(ResourceLocation[][][] array, int x,
            int y, int z, String resLoc) {
        ResourceLocation res = null;
        if (x < array.length && y < array[0].length && z < array[0][0].length) {
            res = array[x][y][z];
        }
        String actual = res == null ? null : res.toString();
        assertEquals(String.format("differ at (%s, %s, %s)", x, y, z), resLoc,
                actual);
    }

    private static void assertResEquals(Schematic schematic, int x, int y,
            int z, String resLoc) {
        assertResEquals(getBlockArray(schematic), x, y, z, resLoc);
    }

    @Test
    public void deserializeEmptySchematic() throws Exception {
        byte[] tag = getTagBytes("emptyschem");
        Schematic schem = new InputStreamSchematicDeserializer()
                .decode(new ByteArrayInputStream(tag));

        BlockData blockData = schem.getBlockData();
        assertEquals(blockData.getDimensions(), Schematic3PointI.ZERO);

        Palette pallete = blockData.getPallete();
        assertEquals(0, pallete.getBitsPerBlock());
        assertEquals(0, pallete.getMax());

        assertResEquals(schem, 0, 0, 0, null);
    }

    @Test
    public void deserializeOneBlockSchematic() throws Exception {
        byte[] tag = getTagBytes("oneblockschem");
        Schematic schem = new InputStreamSchematicDeserializer()
                .decode(new ByteArrayInputStream(tag));

        BlockData blockData = schem.getBlockData();
        assertEquals(blockData.getDimensions(),
                Schematic3PointI.create(1, 1, 1));

        Palette pallete = blockData.getPallete();
        assertEquals(1, pallete.getBitsPerBlock());
        assertEquals(1, pallete.getMax());

        assertResEquals(schem, 0, 0, 0, "minecraft:air");
    }

    @Test
    public void deserializeEightBlockSchematic() throws Exception {
        byte[] tag = getTagBytes("eightblockschem");
        Schematic schem = new InputStreamSchematicDeserializer()
                .decode(new ByteArrayInputStream(tag));

        BlockData blockData = schem.getBlockData();
        assertEquals(blockData.getDimensions(),
                Schematic3PointI.create(2, 2, 2));

        Palette pallete = blockData.getPallete();
        assertEquals(1, pallete.getBitsPerBlock());
        assertEquals(1, pallete.getMax());

        ResourceLocation[][][] array = getBlockArray(schem);
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < 2; k++) {
                    assertResEquals(array, i, j, k, "minecraft:air");
                }
            }
        }
    }

}
