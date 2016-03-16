package me.kenzierocks.spongeschem;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collection;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

@AutoValue
public abstract class Metadata {

    public static final Metadata of(String name, String author,
            ZonedDateTime creationDate, Collection<String> requiredMods) {
        return new AutoValue_Metadata(name, author,
                ZonedDateTime.of(creationDate.toLocalDateTime(),
                        ZoneOffset.UTC),
                ImmutableList.copyOf(requiredMods.stream()
                        .sorted(String.CASE_INSENSITIVE_ORDER)::iterator));
    }

    Metadata() {
    }

    public abstract String getName();

    public abstract String getAuthor();

    public abstract ZonedDateTime getUTCCreationDate();

    public abstract ImmutableList<String> getRequiredMods();

}
