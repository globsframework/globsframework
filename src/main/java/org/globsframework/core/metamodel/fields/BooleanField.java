package org.globsframework.core.metamodel.fields;

import org.globsframework.core.model.FieldValuesAccessor;

import java.util.function.Function;
import java.util.function.Predicate;

public interface BooleanField extends Field, Function<FieldValuesAccessor, Boolean>, Predicate<FieldValuesAccessor> {
    default Boolean apply(FieldValuesAccessor glob) {
        return glob.get(this);
    }

    default boolean test(FieldValuesAccessor glob) {
        return glob.isTrue(this);
    }
}
