package org.globsframework.core.metamodel.type;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.annotations.IsTarget;
import org.globsframework.core.metamodel.annotations.IsTarget_;
import org.globsframework.core.metamodel.annotations.Targets;
import org.globsframework.core.metamodel.fields.GlobArrayUnionField;
import org.globsframework.core.metamodel.fields.StringField;
import org.globsframework.core.metamodel.impl.DefaultGlobTypeBuilder;
import org.globsframework.core.model.MutableGlob;

import java.util.function.Supplier;

public class GlobFieldType {
    public static final GlobType TYPE;

    public static final StringField name;

    @IsTarget_
    public static final StringField targetType;

    @Targets({})
    public static final GlobArrayUnionField annotations;

    static {
        GlobTypeBuilder typeBuilder = new DefaultGlobTypeBuilder("Glob");
        name = typeBuilder.declareStringField(ConstantsName.NAME);
        targetType = typeBuilder.declareStringField(ConstantsName.TARGET_TYPE, IsTarget.INSTANCE);
        annotations = typeBuilder.declareGlobUnionArrayField(ConstantsName.ANNOTATIONS, new Supplier[0]);
        TYPE = typeBuilder.build();
    }

    public static MutableGlob create(String name) {
        return GlobFieldType.TYPE.instantiate()
                .set(GlobFieldType.name, name);
    }
}
