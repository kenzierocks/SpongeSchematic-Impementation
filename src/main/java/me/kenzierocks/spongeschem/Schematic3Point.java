package me.kenzierocks.spongeschem;

import com.google.auto.value.AutoValue;

/**
 * This is declared as an interface so that projects like Sponge can take
 * advantage of <a href="https://github.com/SpongePowered/Mixin">Mixins</a> to
 * make the native vector behave like a {@link Schematic3Point}.
 */
public interface Schematic3Point {

    static final Schematic3Point ZERO = DefaultImpl.create(0, 0, 0);

    @AutoValue
    abstract class DefaultImpl implements Schematic3Point {

        public static Schematic3Point create(int x, int y, int z) {
            return new AutoValue_Schematic3Point_DefaultImpl(x, y, z);
        }

        DefaultImpl() {
        }

        @Override
        public final boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof Schematic3Point) {
                Schematic3Point that = (Schematic3Point) o;
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
     * Note: All {@link Schematic3Point} instances must return {@code true} if
     * {@code obj} is an instance of {@code Schematic3Point} and the coordinate
     * position is equal. See {@link DefaultImpl} for an example
     * {@link DefaultImpl#equals(Object) equals} implementation.
     * 
     * @see DefaultImpl#equals(Object)
     */
    @Override
    boolean equals(Object obj);

}
