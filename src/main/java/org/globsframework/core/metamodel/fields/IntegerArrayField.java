package org.globsframework.core.metamodel.fields;

import org.globsframework.core.model.FieldValuesAccessor;

import java.util.function.Function;

public interface IntegerArrayField extends Field, Function<FieldValuesAccessor, int[]> {
    default int[] apply(FieldValuesAccessor glob) {
        return glob.get(this);
    }
}
