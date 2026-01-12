package org.globsframework.core.metamodel.fields;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.model.FieldValuesAccessor;
import org.globsframework.core.model.Glob;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public non-sealed interface GlobArrayUnionField extends Field, Function<FieldValuesAccessor, Glob[]> {
    Collection<GlobType> getTargetTypes();

    GlobType getTargetType(String name);

    default Glob[] apply(FieldValuesAccessor glob) {
        return glob.get(this);
    }

    default Stream<Glob> stream(FieldValuesAccessor fieldValuesAccessor) {
        return fieldValuesAccessor.stream(this);
    }

    /*
        Only possible is the field was initializez without any types.
        Use in GlobTypeToGlob.
         */
    void __add__(GlobType type);
}
