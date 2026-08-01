package org.globsframework.core.metamodel;

import org.globsframework.core.metamodel.annotations.*;
import org.globsframework.core.metamodel.fields.*;

public class DummyObjectWithDefaultValues {
    public static GlobType TYPE;

    public static IntegerField ID;

    public static IntegerField INTEGER;

    public static BigDecimalField BIG_DECIMAL;

    public static LongField LONG;

    public static DoubleField DOUBLE;

    public static BooleanField BOOLEAN;

    public static IntegerField LINK;

    public static StringField STRING;

    static {
        final GlobTypeBuilder globTypeBuilder = GlobTypeBuilderFactory.create("dummyObjectWithDefaultValues");
        ID = globTypeBuilder.declareIntegerField("id", KeyField.ZERO);
        INTEGER = globTypeBuilder.declareIntegerField("integer", DefaultInteger.create(7));
        BIG_DECIMAL = globTypeBuilder.declareBigDecimalField("BigDecimal",
                DefaultBigDecimal.create("1.61803398875"));
        LONG = globTypeBuilder.declareLongField("long", DefaultLong.create(5L));
        DOUBLE = globTypeBuilder.declareDoubleField("double", DefaultDouble.create(3.14159265));
        BOOLEAN = globTypeBuilder.declareBooleanField("boolean", DefaultBoolean.create(true));
        LINK = globTypeBuilder.declareIntegerField("link");
        STRING = globTypeBuilder.declareStringField("string", DefaultString.create("Hello"));
        TYPE = globTypeBuilder.build();
    }
}
