package org.globsframework.core.model;

import java.util.Objects;
import java.util.Optional;

// experimental
public record TGlob<T>(Glob data) {

    public static <T> TGlob<T> of(Glob glob) {
        return new TGlob<>(glob);
    }

    public Glob notNull() {
        Objects.requireNonNull(data);
        return data;
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

    public Glob data() {
        return data;
    }

    public Glob get() {
        return data;
    }

    public Optional<Glob> optional() {
        return Optional.ofNullable(data);
    }
}
