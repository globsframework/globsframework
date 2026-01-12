package org.globsframework.core.metamodel.annotations;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.GlobTypeBuilderFactory;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.metamodel.fields.StringField;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.Key;
import org.globsframework.core.model.KeyBuilder;
import org.globsframework.core.model.MutableGlob;

public class FieldName {
    public static final GlobType TYPE;

    public static final StringField NAME;

    public static final Key UNIQUE_KEY;

    public static Glob create(FieldName_ nameAnnotation) {
        return create(nameAnnotation.value());
    }

    public static MutableGlob create(String value) {
        return TYPE.instantiate().set(NAME, value);
    }

    static {
        GlobTypeBuilder globTypeBuilder = GlobTypeBuilderFactory.create("FieldName");
        NAME = globTypeBuilder.declareStringField("name");
        globTypeBuilder.register(GlobCreateFromAnnotation.class, annotation -> create((FieldName_) annotation));
        TYPE = globTypeBuilder.build();
        UNIQUE_KEY = KeyBuilder.newEmptyKey(TYPE);
    }

    public static String getName(Field field) {
        Glob annotation = field.findAnnotation(UNIQUE_KEY);
        if (annotation != null) {
            return annotation.get(NAME);
        }
        return field.getName();
    }
}
