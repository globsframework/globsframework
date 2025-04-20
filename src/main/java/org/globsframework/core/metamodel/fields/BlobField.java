package org.globsframework.core.metamodel.fields;

import org.globsframework.core.model.FieldValuesAccessor;

import java.util.function.Function;

public interface BlobField extends Field, Function<FieldValuesAccessor, byte[]> {
    default byte[] apply(FieldValuesAccessor glob) {
        return glob.get(this);
    }
}
