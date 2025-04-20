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

import java.util.Collections;
import java.util.List;

public class GlobUnionFieldType {
    public static GlobType TYPE;

    public static final StringField name;

    @IsTarget_
    public static final StringArrayField targetTypes;

    @Targets({})
    public static final GlobArrayUnionField annotations;

    static {
        GlobTypeBuilder typeBuilder = new DefaultGlobTypeBuilder("GlobUnion");
        TYPE = typeBuilder.unCompleteType();
        name = typeBuilder.declareStringField(ConstantsName.NAME);
        targetTypes = typeBuilder.declareStringArrayField(ConstantsName.TARGET_TYPE, IsTarget.INSTANCE);
        annotations = typeBuilder.declareGlobUnionArrayField(ConstantsName.ANNOTATIONS, Collections.emptyList());
        typeBuilder.complete();

//        GlobTypeLoaderFactory.create(GlobUnionFieldType.class).load();
    }

    public static MutableGlob create(String name) {
        return TYPE.instantiate().set(GlobUnionFieldType.name, name);
    }
}
