package org.globsframework.core.model;

import java.util.Objects;
import java.util.Optional;

// experimental
public record TGlob<T>(Glob value) {
    public static <T> TGlob<T> of(Glob glob) {
        return new TGlob<>(glob);
    }

    public Glob getNotNull() {
        Objects.requireNonNull(value);
        return value;
    }

    public Optional<Glob> getOpt() {
        return Optional.ofNullable(value);
    }
}
