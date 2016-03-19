package me.kenzierocks.spongeschem.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

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
        try (
                NBTOutputStream s = new NBTOutputStream(out)) {
            s.writeTag(tag);
        }
        return out.toByteArray();
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

        Optional<ResourceLocation> block =
                blockData.getBlock(Schematic3PointI.ZERO);
        assertFalse(block.isPresent());
    }

}
