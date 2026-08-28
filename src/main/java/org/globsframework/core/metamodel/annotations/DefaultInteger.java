package org.globsframework.core.metamodel.annotations;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.GlobTypeBuilderFactory;
import org.globsframework.core.metamodel.fields.IntegerField;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.Key;
import org.globsframework.core.model.KeyBuilder;

public class DefaultInteger {
    public static final GlobType TYPE;

    public static final IntegerField VALUE;

    public static final Key KEY;

    public static Glob create(int value) {
        return TYPE.instantiate().set(VALUE, value);
    }

    static {
        GlobTypeBuilder typeBuilder = GlobTypeBuilderFactory.create("DefaultInteger");
        VALUE = typeBuilder.declareIntegerField("VALUE");
        TYPE = typeBuilder.build();
        KEY = KeyBuilder.newEmptyKey(TYPE);
    }

}
