package me.kenzierocks.spongeschem;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Offset {

    public static final Offset ZERO = to(0, 0, 0);

    public static final Offset to(int x, int y, int z) {
        return new AutoValue_Offset(x, y, z);
    }

    Offset() {
    }

    public abstract int getX();

    public abstract int getY();

    public abstract int getZ();

}
