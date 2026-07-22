package org.globsframework.core.model;

import java.util.Objects;
import java.util.Optional;

// experimental
public record TGlobArray<T>(Glob[] value) {
    public static <T> TGlobArray<T> of(Glob[] globs) {
        return new TGlobArray<>(globs);
    }

    public Glob at(int i) {
        return value[i];
    }

    public Glob[] getNotNull() {
        Objects.requireNonNull(value);
        return value;
    }

    public Optional<Glob[]> getOpt() {
        return Optional.ofNullable(value);
    }
}
