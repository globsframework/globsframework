package org.globsframework.core.metamodel.fields;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.model.FieldValuesAccessor;
import org.globsframework.core.model.Glob;

import java.util.function.Function;

public interface GlobField extends Field, Function<FieldValuesAccessor, Glob> {
    GlobType getTargetType();

    default Glob apply(FieldValuesAccessor glob) {
        return glob.get(this);
    }
}
