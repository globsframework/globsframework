package org.globsframework.core.metamodel.fields;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.model.FieldValuesAccessor;
import org.globsframework.core.model.Glob;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public non-sealed interface GlobUnionField extends Field, Function<FieldValuesAccessor, Glob> {
    Collection<GlobType> getTargetTypes();

    GlobType getTargetType(String name);

    default Glob apply(FieldValuesAccessor glob) {
        return glob.get(this);
    }

}
