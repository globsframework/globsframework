package org.globsframework.core.metamodel.fields;

import org.globsframework.core.model.FieldValuesAccessor;

import java.util.function.Function;

public interface StringField extends Field, Function<FieldValuesAccessor, String> {
    default String apply(FieldValuesAccessor glob) {
        return glob.get(this);
    }
}
