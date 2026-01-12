package org.globsframework.core.metamodel.type;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.annotations.Targets;
import org.globsframework.core.metamodel.fields.GlobArrayUnionField;
import org.globsframework.core.metamodel.fields.StringField;
import org.globsframework.core.metamodel.impl.DefaultGlobTypeBuilder;
import org.globsframework.core.model.MutableGlob;

import java.util.function.Supplier;

public class BooleanArrayFieldType {
    public static final GlobType TYPE;

    public static final StringField name;

    @Targets({})
    public static final GlobArrayUnionField annotations;

    static {
        GlobTypeBuilder typeBuilder = new DefaultGlobTypeBuilder("BooleanArray");
        name = typeBuilder.declareStringField("name");
        annotations = typeBuilder.declareGlobUnionArrayField("annotations", new Supplier[0]);
        TYPE = typeBuilder.build();
    }

    public static MutableGlob create(String name) {
        return TYPE.instantiate()
                .set(BooleanArrayFieldType.name, name);
    }
}
