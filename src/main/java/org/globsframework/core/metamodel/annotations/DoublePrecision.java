package org.globsframework.core.metamodel.annotations;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.GlobTypeBuilderFactory;
import org.globsframework.core.metamodel.fields.IntegerField;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.Key;
import org.globsframework.core.model.KeyBuilder;

public class DoublePrecision {
    public static final GlobType TYPE;

    public static final IntegerField PRECISION;

    @InitUniqueKey
    public static final Key UNIQUE_KEY;

    public static Glob create(DoublePrecision_ precision) {
        return create(precision.value());
    }

    public static Glob create(int value) {
        return TYPE.instantiate().set(PRECISION, value);
    }

    static {
        GlobTypeBuilder typeBuilder = GlobTypeBuilderFactory.create("DoublePrecision");
        PRECISION = typeBuilder.declareIntegerField("precision");
        typeBuilder.register(GlobCreateFromAnnotation.class, annotation -> create((DoublePrecision_) annotation));
        TYPE = typeBuilder.build();
        UNIQUE_KEY = KeyBuilder.newEmptyKey(TYPE);
    }
}
