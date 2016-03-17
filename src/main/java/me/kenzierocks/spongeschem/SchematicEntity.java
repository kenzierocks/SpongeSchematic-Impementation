package me.kenzierocks.spongeschem;

import java.util.Map;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;

@AutoValue
public abstract class SchematicEntity implements VersionedContentHolder {

    public static final SchematicEntity create(int contentVersion,
            ResourceLocation id, Schematic3PointD pos,
            Map<String, Object> data) {
        return new AutoValue_SchematicEntity(contentVersion, id, pos,
                ImmutableMap.copyOf(data));
    }

    SchematicEntity() {
    }

    public abstract ResourceLocation getId();

    public abstract Schematic3PointD getPosition();

    public abstract ImmutableMap<String, Object> getEntityData();

}
