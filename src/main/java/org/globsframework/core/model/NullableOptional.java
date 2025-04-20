package org.globsframework.core.model;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class NullableOptional<T> {
    static NullableOptional<?> empty = new NullableOptional<>(false, null);
    public final boolean isSet;
    public final T value;

    public NullableOptional(boolean isSet, T value) {
        this.isSet = isSet;
        this.value = value;
    }

    public void ifSet(Consumer<? super T> action) {
        if (isSet) {
            action.accept(value);
        }
    }

    public void ifPresent(Consumer<? super T> action) {
        if (isSet && value != null) {
            action.accept(value);
        }
    }

    public static <U> NullableOptional<U> empty() {
        return (NullableOptional<U>) empty;
    }

    public <U> NullableOptional<U> mapIfSet(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (isSet) {
            return new NullableOptional<>(true, mapper.apply(value));
        } else {
            return empty();
        }
    }

    public <U> Optional<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (isSet && value != null) {
            return Optional.ofNullable(mapper.apply(value));
        } else {
            return Optional.empty();
        }
    }

    public <U> NullableOptional<U> flatMapIfSet(Function<? super T, ? extends NullableOptional<? extends U>> mapper) {
        Objects.requireNonNull(mapper);
        if (isSet) {
            NullableOptional<U> r = (NullableOptional<U>) mapper.apply(value);
            return Objects.requireNonNull(r);
        } else {
            return empty();
        }
    }

    public Optional<T> getOpt() {
        return isSet && value != null ? Optional.of(value) : Optional.empty();
    }
}
