package me.kenzierocks.spongeschem;

import com.google.auto.value.AutoValue;

/**
 * This is declared as an interface so that projects like Sponge can take
 * advantage of <a href="https://github.com/SpongePowered/Mixin">Mixins</a> to
 * make the native vector behave like a {@link Schematic3PointD}.
 */
public interface Schematic3PointD {

    static final Schematic3PointD ZERO = DefaultImpl.create(0, 0, 0);

    @AutoValue
    abstract class DefaultImpl implements Schematic3PointD {

        public static Schematic3PointD create(double x, double y, double z) {
            return new AutoValue_Schematic3PointD_DefaultImpl(x, y, z);
        }

        DefaultImpl() {
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof Schematic3PointD) {
                Schematic3PointD that = (Schematic3PointD) o;
                return (Double.doubleToLongBits(this.getX()) == Double
                        .doubleToLongBits(that.getX()))
                        && (Double.doubleToLongBits(this.getY()) == Double
                                .doubleToLongBits(that.getY()))
                        && (Double.doubleToLongBits(this.getZ()) == Double
                                .doubleToLongBits(that.getZ()));
            }
            return false;
        }

    }

    double getX();

    double getY();

    double getZ();

    /**
     * Note: All {@link Schematic3PointD} instances must return {@code true} if
     * {@code obj} is an instance of {@code Schematic3PointD} and the coordinate
     * position is equal. See {@link DefaultImpl} for an example
     * {@link DefaultImpl#equals(Object) equals} implementation.
     * 
     * @see DefaultImpl#equals(Object)
     */
    @Override
    boolean equals(Object obj);

}
