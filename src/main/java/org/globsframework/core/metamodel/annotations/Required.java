package org.globsframework.core.metamodel.annotations;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.impl.DefaultGlobTypeBuilder;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.Key;
import org.globsframework.core.model.KeyBuilder;

public class Required {
    public static final GlobType TYPE;

    public static final Key UNIQUE_KEY;

    public static final Glob UNIQUE_GLOB;

    static {
        GlobTypeBuilder globTypeBuilder = new DefaultGlobTypeBuilder("Required");
        TYPE = globTypeBuilder.build();
        UNIQUE_KEY = KeyBuilder.newEmptyKey(TYPE);
        UNIQUE_GLOB = TYPE.instantiate();
    }

}
