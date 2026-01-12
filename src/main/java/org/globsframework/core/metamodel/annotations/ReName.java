package org.globsframework.core.metamodel.annotations;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.GlobTypeBuilderFactory;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.metamodel.fields.StringField;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.Key;
import org.globsframework.core.model.MutableGlob;

public class ReName {
    public static final GlobType TYPE;

    @KeyField_
    public static final StringField NAMESPACE;

    public static final StringField NAME;

    static {
        GlobTypeBuilder typeBuilder = GlobTypeBuilderFactory.create("ReName");
        NAMESPACE = typeBuilder.declareStringField("namespace");
        NAME = typeBuilder.declareStringField("name");
        TYPE = typeBuilder.build();
    }

    public static Glob create(ReName_ reName) {
        return create(reName.name(), reName.value());
    }

    public static MutableGlob create(String name, String value) {
        return TYPE.instantiate().set(NAMESPACE, name).set(NAME, value);
    }

    public static String getName(Key key, Field field) {
        if (key.getGlobType() != TYPE) {
            throw new RuntimeException("Key is of type " + key.toString() + " but " + NAMESPACE.getName() + " was expected.");
        }
        Glob annotation = field.findAnnotation(key);
        if (annotation != null) {
            return annotation.get(NAMESPACE);
        }
        return field.getName();
    }
}
