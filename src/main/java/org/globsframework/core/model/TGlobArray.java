package org.globsframework.core.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public record TGlobArray<T>(Glob[] data) {

    public static <T> TGlobArray<T> of(Glob[] globs) {
        return new TGlobArray<>(globs);
    }

    public static <T> TGlobArray<T> of(Collection<Glob> globs) {
        return new TGlobArray<>(globs.toArray(new Glob[0]));
    }

    public Glob at(int i) {
        return data[i];
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

    public Glob[] notNull() {
        Objects.requireNonNull(data);
        return data;
    }

    public Stream<Glob> stream() {
        return Arrays.stream(data);
    }

    public Glob[] data(){
        return data;
    }

    public Optional<Glob[]> optional() {
        return Optional.ofNullable(data);
    }
}
