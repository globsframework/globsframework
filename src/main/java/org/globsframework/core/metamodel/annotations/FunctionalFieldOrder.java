package org.globsframework.core.metamodel.annotations;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.fields.IntegerField;
import org.globsframework.core.metamodel.fields.StringField;
import org.globsframework.core.metamodel.impl.DefaultGlobTypeBuilder;
import org.globsframework.core.model.Key;
import org.globsframework.core.model.KeyBuilder;
import org.globsframework.core.model.MutableGlob;

public class FunctionalFieldOrder {
    public static final GlobType TYPE;

    public static final StringField NAME;

    public static final IntegerField ORDER;

    @InitUniqueKey
    public static final Key KEY;

    static {
        GlobTypeBuilder typeBuilder = new DefaultGlobTypeBuilder("FunctionalFieldOrder");

        NAME = typeBuilder.declareStringField("name");
        ORDER = typeBuilder.declareIntegerField("order");
        typeBuilder.register(GlobCreateFromAnnotation.class, annotation -> create((FunctionalFieldOrder_) annotation));
        TYPE = typeBuilder.build();
        KEY = KeyBuilder.newEmptyKey(TYPE);
    }

    private static MutableGlob create(FunctionalFieldOrder_ annotation) {
        return TYPE.instantiate()
                .set(ORDER, annotation.value())
                .set(NAME, annotation.name());
    }
}
