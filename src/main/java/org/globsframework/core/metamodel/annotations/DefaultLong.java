package org.globsframework.core.metamodel.annotations;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.GlobTypeBuilderFactory;
import org.globsframework.core.metamodel.fields.LongField;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.Key;
import org.globsframework.core.model.KeyBuilder;

public class DefaultLong {
    public static final GlobType TYPE;

    public static final LongField VALUE;

    @InitUniqueKey
    public static final Key KEY;

    public static Glob create(long value) {
        return TYPE.instantiate().set(VALUE, value);
    }

    public static Glob create(DefaultLong_ defaultLong) {
        return TYPE.instantiate().set(VALUE, defaultLong.value());
    }

    static {
        GlobTypeBuilder typeBuilder = GlobTypeBuilderFactory.create("DefaultLong");
        TYPE = typeBuilder.unCompleteType();
        VALUE = typeBuilder.declareLongField("value");
        typeBuilder.register(GlobCreateFromAnnotation.class, annotation -> create((DefaultLong_) annotation));
        typeBuilder.complete();
        KEY = KeyBuilder.newEmptyKey(TYPE);
//        GlobTypeLoader loader = GlobTypeLoaderFactory.create(DefaultLong.class, "DefaultLong");
//        loader.register(GlobCreateFromAnnotation.class, annotation -> create((DefaultLong_) annotation))
//                .load();
    }
}
