package org.globsframework.core.metamodel.annotations;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.GlobTypeBuilderFactory;
import org.globsframework.core.metamodel.fields.IntegerField;
import org.globsframework.core.metamodel.fields.StringField;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.Key;
import org.globsframework.core.model.KeyBuilder;

import java.lang.annotation.Annotation;

public class MultiLineText {
    public static final GlobType TYPE;

    public static final StringField mimeType;

    public static final IntegerField maxSize;

    @InitUniqueKey
    public static final Key UNIQUE_KEY;

    static {
        GlobTypeBuilder typeBuilder = GlobTypeBuilderFactory.create("MultiLineText");
        mimeType = typeBuilder.declareStringField("MIME_TYPE");
        maxSize = typeBuilder.declareIntegerField("MAX_SIZE");
        typeBuilder.register(GlobCreateFromAnnotation.class, MultiLineText::create);
        TYPE = typeBuilder.build();
        UNIQUE_KEY = KeyBuilder.newEmptyKey(TYPE);
    }

    public static Glob create(String mimeTypeVal, int maxSizeVal) {
        return TYPE.instantiate()
                .set(mimeType, mimeTypeVal)
                .set(maxSize, maxSizeVal);
    }

    public static Glob create() {
        return TYPE.instantiate()
                .set(mimeType, "text/plain")
                .set(maxSize, -1);
    }

    private static Glob create(Annotation annotation) {
        return create(((MultiLineText_) annotation).mimeType(), ((MultiLineText_) annotation).maxSize());
    }
}
