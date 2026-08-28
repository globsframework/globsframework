package org.globsframework.core.metamodel.annotations;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.GlobTypeBuilderFactory;
import org.globsframework.core.metamodel.fields.BooleanField;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.Key;
import org.globsframework.core.model.KeyBuilder;

public class DefaultBoolean {
    public static final GlobType TYPE;

    public static final BooleanField VALUE;

    public static final Key KEY;

    public static final Glob OK;

    public static final Glob KO;

    public static Glob create(boolean value) {
        return value ? OK : KO;
    }

    static {
        GlobTypeBuilder typeBuilder = GlobTypeBuilderFactory.create("DefaultBoolean");
        VALUE = typeBuilder.declareBooleanField("VALUE");
        TYPE = typeBuilder.build();
        KEY = KeyBuilder.newEmptyKey(TYPE);
        OK = TYPE.instantiate().set(VALUE, true);
        KO = TYPE.instantiate().set(VALUE, false);
    }
}
