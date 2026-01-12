package org.globsframework.core.metamodel.annotations;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.GlobTypeBuilderFactory;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.Key;
import org.globsframework.core.model.KeyBuilder;

import java.lang.annotation.Annotation;

public class AutoIncrement {
    public static final GlobType TYPE;

    @InitUniqueKey
    public static final Key KEY;

    @InitUniqueGlob
    public static final Glob INSTANCE;


    static {
        GlobTypeBuilder typeBuilder = GlobTypeBuilderFactory.create("AutoIncrement");
        typeBuilder.register(GlobCreateFromAnnotation.class, AutoIncrement::create);
        TYPE = typeBuilder.build();
        KEY = KeyBuilder.newEmptyKey(TYPE);
        INSTANCE = TYPE.instantiate();
    }

    private static Glob create(Annotation annotation) {
        return INSTANCE;
    }
}
