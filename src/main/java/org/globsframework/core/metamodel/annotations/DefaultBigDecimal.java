package org.globsframework.core.metamodel.annotations;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.GlobTypeBuilderFactory;
import org.globsframework.core.metamodel.fields.BigDecimalField;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.Key;
import org.globsframework.core.model.KeyBuilder;

import java.math.BigDecimal;

public class DefaultBigDecimal {

    public static final GlobType TYPE;

    public static final BigDecimalField VALUE;

    public static final Key KEY;

    public static Glob create(BigDecimal defaultBigDecimal) {
        return TYPE.instantiate().set(VALUE, defaultBigDecimal);
    }

    public static Glob create(String defaultBigDecimal) {
        return TYPE.instantiate().set(VALUE, new BigDecimal(defaultBigDecimal));
    }

    static {
        GlobTypeBuilder typeBuilder = GlobTypeBuilderFactory.create("DefaultBigDecimal");
        VALUE = typeBuilder.declareBigDecimalField("VALUE");
        TYPE = typeBuilder.build();
        KEY = KeyBuilder.newEmptyKey(TYPE);
    }
}
