package org.globsframework.core.metamodel.annotations;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.impl.DefaultGlobTypeBuilder;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.Key;
import org.globsframework.core.model.KeyBuilder;

// date et heure en nombre de millsecond depuis 01/01/1970

public class IsDateTime {
    public static final GlobType TYPE;

    public static final Key KEY;

    public static final Glob UNIQUE;

    static {
        GlobTypeBuilder typeBuilder = new DefaultGlobTypeBuilder("IsDateTime");
        TYPE = typeBuilder.build();
        KEY = KeyBuilder.newEmptyKey(TYPE);
        UNIQUE = TYPE.instantiate();
    }

    private static Glob getUnique() {
        return UNIQUE;
    }
}
