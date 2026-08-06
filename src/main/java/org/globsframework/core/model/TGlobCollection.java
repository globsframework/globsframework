package org.globsframework.core.model;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

// experimental
public record TGlobCollection<T>(Collection<Glob> data) {

    public static <T> TGlobCollection<T> of(Glob[] globs) {
        return new TGlobCollection<>(List.of(globs));
    }

    public static <T> TGlobCollection<T> of(Collection<Glob> globs) {
        return new TGlobCollection<>(globs);
    }

    public boolean isPresent() {
        return data != null;
    }

    public boolean isNotNull() {
        return data != null;
    }

    public boolean isNull() {
        return data == null;
    }

    public Collection<Glob> notNull() {
        Objects.requireNonNull(data);
        return data;
    }

    public Stream<Glob> stream() {
        return data.stream();
    }

    public Collection<Glob> data() {
        return data;
    }

    public Optional<Collection<Glob>> optional() {
        return Optional.ofNullable(data);
    }
}
