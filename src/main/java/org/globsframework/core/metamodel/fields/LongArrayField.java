package org.globsframework.core.metamodel.fields;

import org.globsframework.core.model.FieldValuesAccessor;

import java.util.function.Function;

public interface LongArrayField extends Field, Function<FieldValuesAccessor, long[]> {
    default long[] apply(FieldValuesAccessor glob) {
        return glob.get(this);
    }
}
