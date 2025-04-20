package org.globsframework.core.metamodel.fields;

import org.globsframework.core.model.FieldValuesAccessor;

import java.util.function.Function;

public interface LongField extends Field, Function<FieldValuesAccessor, Long> {
    default Long apply(FieldValuesAccessor data) {
        return data.get(this);
    }
}
