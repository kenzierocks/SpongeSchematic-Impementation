package me.kenzierocks.spongeschem;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class SchematicEntity extends VersionedContentHolder {

    SchematicEntity() {
    }

    public abstract int getFoobar();

}
