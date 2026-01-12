package org.globsframework.core.metamodel.type;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.annotations.IsTarget;
import org.globsframework.core.metamodel.annotations.IsTarget_;
import org.globsframework.core.metamodel.annotations.Targets;
import org.globsframework.core.metamodel.fields.GlobArrayUnionField;
import org.globsframework.core.metamodel.fields.StringArrayField;
import org.globsframework.core.metamodel.fields.StringField;
import org.globsframework.core.metamodel.impl.DefaultGlobTypeBuilder;
import org.globsframework.core.model.MutableGlob;

import java.util.function.Supplier;

public class GlobUnionArrayFieldType {
    public static final GlobType TYPE;

    public static final StringField name;

    @IsTarget_()
    public static final StringArrayField targetTypes;

    @Targets({})
    public static final GlobArrayUnionField annotations;

    static {
        GlobTypeBuilder typeBuilder = new DefaultGlobTypeBuilder("GlobUnionArray");
        name = typeBuilder.declareStringField(ConstantsName.NAME);
        targetTypes = typeBuilder.declareStringArrayField(ConstantsName.TARGET_TYPE, IsTarget.INSTANCE);
        annotations = typeBuilder.declareGlobUnionArrayField(ConstantsName.ANNOTATIONS, new Supplier[0]);
        TYPE = typeBuilder.build();
    }

    public static MutableGlob create(String name) {
        return TYPE.instantiate()
                .set(GlobUnionArrayFieldType.name, name);
    }
}
