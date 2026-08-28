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

    public static final Key KEY;

    public static Glob create(long value) {
        return TYPE.instantiate().set(VALUE, value);
    }

    static {
        GlobTypeBuilder typeBuilder = GlobTypeBuilderFactory.create("DefaultLong");
        VALUE = typeBuilder.declareLongField("value");
        TYPE = typeBuilder.build();
        KEY = KeyBuilder.newEmptyKey(TYPE);
    }
}
