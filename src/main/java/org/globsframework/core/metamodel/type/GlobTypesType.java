package org.globsframework.core.metamodel.type;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.annotations.Target;
import org.globsframework.core.metamodel.fields.GlobArrayField;
import org.globsframework.core.metamodel.impl.DefaultGlobTypeBuilder;

public class GlobTypesType {
    public static final GlobType TYPE;

    @Target(GlobTypeType.class)
    public static final GlobArrayField types;

    static {
        GlobTypeBuilder typeBuilder = new DefaultGlobTypeBuilder("GlobTypes");
        types = typeBuilder.declareGlobArrayField("types", () -> GlobTypeType.TYPE);
        TYPE = typeBuilder.build();
    }
}
