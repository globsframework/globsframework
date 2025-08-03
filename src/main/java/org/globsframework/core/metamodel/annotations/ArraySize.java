package org.globsframework.core.metamodel.annotations;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.metamodel.impl.DefaultGlobTypeBuilder;
import org.globsframework.core.model.*;
import org.globsframework.core.utils.Strings;

import java.nio.charset.Charset;

public class ArraySize {
    static public final GlobType TYPE;

    static public final IntegerField VALUE;

    @InitUniqueKey
    static public final Key KEY;

    static {
        GlobTypeBuilder typeBuilder = new DefaultGlobTypeBuilder("ArraySize");
        TYPE = typeBuilder.unCompleteType();
        VALUE = typeBuilder.declareIntegerField("value");
        typeBuilder.register(GlobCreateFromAnnotation.class, annotation -> create((ArraySize_) annotation));
        typeBuilder.complete();
        KEY = KeyBuilder.newEmptyKey(TYPE);

//        GlobTypeLoader loader = GlobTypeLoaderFactory.create(MaxSize.class, "MaxSize");
//        loader.register(GlobCreateFromAnnotation.class, annotation -> create((MaxSize_) annotation))
//                .load();
    }


    public static Glob create(ArraySize_ size) {
        return create(size.value());
    }

    public static MutableGlob create(int maxSize) {
        return TYPE.instantiate().set(VALUE, maxSize);
    }
}
