package me.kenzierocks.spongeschem;

import com.google.auto.value.AutoValue;

/**
 * This is declared as an interface so that projects like Sponge can take
 * advantage of <a href="https://github.com/SpongePowered/Mixin">Mixins</a> to
 * make the native vector behave like a {@link SchematicPosition}.
 */
public interface SchematicPosition {

    @AutoValue
    abstract class DefaultImpl implements SchematicPosition {

        public static SchematicPosition create(int x, int y, int z) {
            return new AutoValue_SchematicPosition_DefaultImpl(x, y, z);
        }

        DefaultImpl() {
        }

        @Override
        public final boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof SchematicPosition) {
                SchematicPosition that = (SchematicPosition) o;
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
     * Note: All {@link SchematicPosition} instances must return {@code true} if
     * {@code obj} is an instance of {@code SchematicPosition} and the
     * coordinate position is equal. See {@link DefaultImpl} for an example
     * {@link DefaultImpl#equals(Object) equals} implementation.
     * 
     * @see DefaultImpl#equals(Object)
     */
    @Override
    boolean equals(Object obj);

}
