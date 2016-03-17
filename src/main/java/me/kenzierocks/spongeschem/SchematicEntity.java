package me.kenzierocks.spongeschem;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class SchematicEntity extends VersionedContentHolder {

    public static final int CURRENT_CONTENT_VERSION = 1;

    public static final SchematicEntity create() {
        return create(CURRENT_CONTENT_VERSION);
    }

    public static final SchematicEntity create(int contentVersion) {
        return new AutoValue_SchematicEntity(contentVersion);
    }

    SchematicEntity() {
    }

}
