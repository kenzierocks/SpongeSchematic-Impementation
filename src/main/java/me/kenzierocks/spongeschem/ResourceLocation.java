package me.kenzierocks.spongeschem;

import java.util.List;

import javax.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;

public interface ResourceLocation {

    static ResourceLocation fromString(String domainAndName) {
        if (domainAndName.indexOf(':') > -1) {
            List<String> split = Splitter.on(':').splitToList(domainAndName);
            return at(split.get(0), split.get(1));
        } else {
            return at("minecraft", domainAndName);
        }
    }

    static ResourceLocation at(@Nullable String domain, String name) {
        domain = MoreObjects.firstNonNull(domain, "minecraft");
        return new AutoValue_ResourceLocation_DefaultImpl(domain, name);
    }

    @AutoValue
    abstract class DefaultImpl implements ResourceLocation {

        DefaultImpl() {
        }

        @Override
        public String toString() {
            return getDomain() + ":" + getName();
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof ResourceLocation) {
                ResourceLocation that = (ResourceLocation) o;
                return (this.getDomain().equals(that.getDomain()))
                        && (this.getName().equals(that.getName()));
            }
            return false;
        }

    }

    String getDomain();

    String getName();

    /**
     * Must return {@link #getDomain()} + {@code ":"} + {@link #getName()}.
     */
    @Override
    String toString();

}
