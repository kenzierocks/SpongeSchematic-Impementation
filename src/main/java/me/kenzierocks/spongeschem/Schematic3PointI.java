package me.kenzierocks.spongeschem;

import com.google.auto.value.AutoValue;

/**
 * This is declared as an interface so that projects like Sponge can take
 * advantage of <a href="https://github.com/SpongePowered/Mixin">Mixins</a> to
 * make the native vector behave like a {@link Schematic3PointI}.
 */
public interface Schematic3PointI {

    static final Schematic3PointI ZERO = create(0, 0, 0);

    static Schematic3PointI create(int x, int y, int z) {
        return new AutoValue_Schematic3PointI_DefaultImpl(x, y, z);
    }

    @AutoValue
    abstract class DefaultImpl implements Schematic3PointI {

        DefaultImpl() {
        }

        @Override
        public final boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof Schematic3PointI) {
                Schematic3PointI that = (Schematic3PointI) o;
                return (this.getX() == that.getX())
                        && (this.getY() == that.getY())
                        && (this.getZ() == that.getZ());
            }
            return false;
        }

    }

    int getX();

    int getY();

    int getZ();

    /**
     * Note: All {@link Schematic3PointI} instances must return {@code true} if
     * {@code obj} is an instance of {@code Schematic3PointI} and the coordinate
     * position is equal. See {@link DefaultImpl} for an example
     * {@link DefaultImpl#equals(Object) equals} implementation.
     * 
     * @see DefaultImpl#equals(Object)
     */
    @Override
    boolean equals(Object obj);

}
