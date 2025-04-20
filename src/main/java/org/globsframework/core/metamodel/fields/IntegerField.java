package org.globsframework.core.metamodel.fields;

import org.globsframework.core.model.FieldValuesAccessor;

import java.util.function.Function;

public interface IntegerField extends Field, Function<FieldValuesAccessor, Integer> {
    default Integer apply(FieldValuesAccessor glob) {
        return glob.get(this);
    }
}
