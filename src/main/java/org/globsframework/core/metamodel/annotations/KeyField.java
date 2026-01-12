package org.globsframework.core.metamodel.annotations;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.IntegerField;
import org.globsframework.core.metamodel.impl.DefaultGlobTypeBuilder;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.Key;
import org.globsframework.core.model.KeyBuilder;

public class KeyField {
    public static final GlobType TYPE;

    public static final IntegerField INDEX;

    @InitUniqueKey
    public static final Key UNIQUE_KEY;

    public static final Glob UNINITIALIZED;

    public static final Glob ZERO;

    public static final Glob ONE;

    public static final Glob TWO;

    public static final Glob THREE;

    public static final Glob FOUR;

    public static Glob create(int indexOfKeyField) {
        switch (indexOfKeyField) {
            case 0:
                return ZERO;
            case 1:
                return ONE;
            case 2:
                return TWO;
            case 3:
                return THREE;
            case 4:
                return FOUR;
            default:
                return TYPE.instantiate().set(INDEX, indexOfKeyField);
        }
    }

    static {
        DefaultGlobTypeBuilder typeBuilder = new DefaultGlobTypeBuilder("KeyField");
        INDEX = typeBuilder.declareIntegerField("index");
        typeBuilder.register(GlobCreateFromAnnotation.class, annotation -> create(((KeyField_) annotation).value()));
        TYPE = typeBuilder.build();
        UNIQUE_KEY = KeyBuilder.newEmptyKey(TYPE);
        UNINITIALIZED = TYPE.instantiate().set(INDEX, -1);
        ZERO = TYPE.instantiate().set(INDEX, 0);
        ONE = TYPE.instantiate().set(INDEX, 1);
        TWO = TYPE.instantiate().set(INDEX, 2);
        THREE = TYPE.instantiate().set(INDEX, 3);
        FOUR = TYPE.instantiate().set(INDEX, 4);
    }
}
