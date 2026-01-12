package org.globsframework.core.metamodel;

import org.globsframework.core.metamodel.annotations.*;
import org.globsframework.core.metamodel.fields.*;

public class DummyObjectWithDefaultValues {
    public static GlobType TYPE;

    @KeyField_
    @AutoIncrement_
    public static IntegerField ID;

    @DefaultInteger_(7)
    public static IntegerField INTEGER;

    @DefaultBigDecimal_("1.61803398875")
    public static BigDecimalField BIG_DECIMAL;

    @DefaultLong_(5L)
    public static LongField LONG;

    @DefaultDouble_(3.14159265)
    public static DoubleField DOUBLE;

    @DefaultBoolean_(true)
    public static BooleanField BOOLEAN;

    public static IntegerField LINK;

    @DefaultString_("Hello")
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
