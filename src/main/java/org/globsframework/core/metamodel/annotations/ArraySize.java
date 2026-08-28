package org.globsframework.core.metamodel.annotations;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.metamodel.impl.DefaultGlobTypeBuilder;
import org.globsframework.core.model.*;

public class ArraySize {
    static public final GlobType TYPE;

    static public final IntegerField VALUE;

    static public final Key KEY;

    static {
        GlobTypeBuilder typeBuilder = new DefaultGlobTypeBuilder("ArraySize");
        VALUE = typeBuilder.declareIntegerField("value");
        TYPE = typeBuilder.build();
        KEY = KeyBuilder.newEmptyKey(TYPE);
    }

    public static MutableGlob create(int maxSize) {
        return TYPE.instantiate().set(VALUE, maxSize);
    }
}
