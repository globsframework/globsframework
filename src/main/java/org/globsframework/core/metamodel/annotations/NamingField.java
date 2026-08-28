package org.globsframework.core.metamodel.annotations;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.impl.DefaultGlobTypeBuilder;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.Key;
import org.globsframework.core.model.KeyBuilder;

public class NamingField {
    public static final GlobType TYPE;

    public static final Key KEY;

    public static final Glob UNIQUE_GLOB;

    static {
        GlobTypeBuilder typeBuilder = new DefaultGlobTypeBuilder("NamingField");
        TYPE = typeBuilder.build();
        UNIQUE_GLOB = TYPE.instantiate();
        KEY = KeyBuilder.newEmptyKey(TYPE);
    }

}
