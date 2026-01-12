package org.globsframework.core.metamodel.type;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.annotations.*;
import org.globsframework.core.metamodel.fields.GlobArrayUnionField;
import org.globsframework.core.metamodel.fields.StringField;
import org.globsframework.core.metamodel.impl.DefaultGlobTypeBuilder;
import org.globsframework.core.model.MutableGlob;

import java.util.function.Supplier;

public class StringFieldType {
    public static final GlobType TYPE;

    public static final StringField name;

    @Targets({KeyField.class, IsTarget.class, MaxSize.class, MultiLineText.class, Required.class,
            NamingField.class, FieldName.class, EnumAnnotation.class})
    public static final GlobArrayUnionField annotations;

    public static MutableGlob create(String name) {
        return TYPE.instantiate()
                .set(StringFieldType.name, name);
    }

    static {
        GlobTypeBuilder typeBuilder = new DefaultGlobTypeBuilder("String");
        name = typeBuilder.declareStringField(ConstantsName.NAME);
        annotations = typeBuilder.declareGlobUnionArrayField(ConstantsName.ANNOTATIONS,
                new Supplier[]{() -> KeyField.TYPE, () -> IsTarget.TYPE, () -> MaxSize.TYPE, () -> MultiLineText.TYPE, () -> Required.TYPE,
                        () -> NamingField.TYPE, () -> FieldName.TYPE, () -> EnumAnnotation.TYPE});
        TYPE = typeBuilder.build();
    }
}
