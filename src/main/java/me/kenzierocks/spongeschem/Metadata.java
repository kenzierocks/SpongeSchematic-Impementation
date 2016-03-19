package me.kenzierocks.spongeschem;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collection;

import javax.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;

@AutoValue
public abstract class Metadata {

    public static final Metadata of(String name, String author,
            ZonedDateTime creationDate, Collection<String> requiredMods) {
        return new AutoValue_Metadata(name, author,
                ZonedDateTime.of(creationDate.toLocalDateTime(),
                        ZoneOffset.UTC),
                FluentIterable.from(requiredMods)
                        .toSortedList(String.CASE_INSENSITIVE_ORDER));
    }

    Metadata() {
    }

    @Nullable
    public abstract String getName();

    @Nullable
    public abstract String getAuthor();

    @Nullable
    public abstract ZonedDateTime getUTCCreationDate();

    public abstract ImmutableList<String> getRequiredMods();

}
