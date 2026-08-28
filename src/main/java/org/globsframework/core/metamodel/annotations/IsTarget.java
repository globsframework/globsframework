package org.globsframework.core.metamodel.annotations;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.impl.DefaultGlobTypeBuilder;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.Key;
import org.globsframework.core.model.KeyBuilder;

public class IsTarget {
    public static final GlobType TYPE;

    public static final Key KEY;

    public static final Glob INSTANCE;

    static {
        GlobTypeBuilder typeBuilder = new DefaultGlobTypeBuilder("IsTarget");
        TYPE = typeBuilder.build();
        KEY = KeyBuilder.newEmptyKey(TYPE);
        INSTANCE = TYPE.instantiate();
    }

    private static Glob getInstance() {
        return INSTANCE;
    }
}
