package org.globsframework.core.metamodel.annotations;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.GlobTypeBuilderFactory;
import org.globsframework.core.metamodel.fields.StringField;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.Key;
import org.globsframework.core.model.KeyBuilder;

public class Comment {
    public static final GlobType TYPE;

    public static final StringField VALUE;

    @InitUniqueKey
    public static final Key UNIQUE_KEY;

    public static Glob create(Comment_ comment) {
        return TYPE.instantiate().set(VALUE, comment.value());
    }

    static {
        GlobTypeBuilder typeBuilder = GlobTypeBuilderFactory.create("Comment");
        VALUE = typeBuilder.declareStringField("VALUE");
        typeBuilder.register(GlobCreateFromAnnotation.class, annotation -> create((Comment_) annotation));
        TYPE = typeBuilder.build();
        UNIQUE_KEY = KeyBuilder.newEmptyKey(TYPE);
    }
}
