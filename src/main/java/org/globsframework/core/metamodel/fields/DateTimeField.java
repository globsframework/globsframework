package org.globsframework.core.metamodel.fields;

import org.globsframework.core.model.FieldValuesAccessor;

import java.time.ZonedDateTime;
import java.util.function.Function;

public interface DateTimeField extends Field, Function<FieldValuesAccessor, ZonedDateTime> {
    default ZonedDateTime apply(FieldValuesAccessor glob) {
        return glob.get(this);
    }
}
