package org.globsframework.core.metamodel.fields;

import org.globsframework.core.model.FieldValuesAccessor;

import java.math.BigDecimal;
import java.util.function.Function;

public interface BigDecimalArrayField extends Field, Function<FieldValuesAccessor, BigDecimal[]> {
    default BigDecimal[] apply(FieldValuesAccessor glob) {
        return glob.get(this);
    }
}
