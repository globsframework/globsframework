package org.globsframework.core.metamodel.fields;

import org.globsframework.core.model.FieldValuesAccessor;

import java.util.function.Function;

public interface DoubleArrayField extends Field, Function<FieldValuesAccessor, double[]> {
    default double[] apply(FieldValuesAccessor glob) {
        return glob.get(this);
    }
}
