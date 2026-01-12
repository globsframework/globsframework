package org.globsframework.core.metamodel.annotations;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.metamodel.fields.StringArrayField;
import org.globsframework.core.metamodel.impl.DefaultGlobTypeBuilder;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.Key;
import org.globsframework.core.model.KeyBuilder;
import org.globsframework.core.model.MutableGlob;

import java.util.Optional;

public class EnumAnnotation {
    public static final GlobType TYPE;

    public static final StringArrayField NAME;

    @InitUniqueKey
    public static final Key UNIQUE_KEY;

    static {
        GlobTypeBuilder typeBuilder = new DefaultGlobTypeBuilder("EnumAnnotation");
        NAME = typeBuilder.declareStringArrayField("values");
        TYPE = typeBuilder.build();
        UNIQUE_KEY = KeyBuilder.newEmptyKey(TYPE);
    }

    public static Glob create(EnumAnnotation_ nameAnnotation) {
        return create(nameAnnotation.value());
    }

    public static MutableGlob create(String[] value) {
        return TYPE.instantiate().set(NAME, value);
    }

    public static Optional<String[]> listEnums(Field field) {
        Glob annotation = field.findAnnotation(UNIQUE_KEY);
        if (annotation != null) {
            return Optional.of(annotation.get(NAME));
        }
        return Optional.empty();
    }
}
