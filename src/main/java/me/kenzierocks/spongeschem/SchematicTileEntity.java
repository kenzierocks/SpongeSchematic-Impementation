package me.kenzierocks.spongeschem;

import java.util.Map;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;

@AutoValue
public abstract class SchematicTileEntity implements VersionedContentHolder {

    public static final SchematicTileEntity create(int contentVersion,
            ResourceLocation id, Schematic3PointI pos,
            Map<String, Object> data) {
        return new AutoValue_SchematicTileEntity(contentVersion, id, pos,
                ImmutableMap.copyOf(data));
    }

    SchematicTileEntity() {
    }

    public abstract ResourceLocation getId();

    public abstract Schematic3PointI getPosition();

    public abstract ImmutableMap<String, Object> getEntityData();

}
