package org.globsframework.core.metamodel.fields;

import org.globsframework.core.model.FieldValuesAccessor;

import java.util.function.Function;

public interface BooleanArrayField extends Field, Function<FieldValuesAccessor, boolean[]> {
    default boolean[] apply(FieldValuesAccessor glob) {
        return glob.get(this);
    }
}
