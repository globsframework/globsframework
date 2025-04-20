package org.globsframework.core.metamodel.type;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.annotations.Targets;
import org.globsframework.core.metamodel.fields.GlobArrayUnionField;
import org.globsframework.core.metamodel.fields.StringField;
import org.globsframework.core.metamodel.impl.DefaultGlobTypeBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GlobTypeType {
    public static final GlobType TYPE;

    public static final StringField kind;

    @Targets({
            BooleanFieldType.class, BooleanArrayFieldType.class,
            StringFieldType.class, StringArrayFieldType.class,
            DoubleFieldType.class, DoubleArrayFieldType.class,
            IntegerFieldType.class, IntegerArrayFieldType.class,
            LongFieldType.class, LongArrayFieldType.class,
            DateFieldType.class, DateTimeFieldType.class,
            BytesFieldType.class,
            BigDecimalFieldType.class, BigDecimalArrayFieldType.class,
            GlobFieldType.class, GlobArrayFieldType.class,
            GlobUnionFieldType.class, GlobUnionArrayFieldType.class
    })
    public static final GlobArrayUnionField fields;

    @Targets({})
    public static final GlobArrayUnionField annotations;

    static {
        GlobTypeBuilder typeBuilder = new DefaultGlobTypeBuilder("GlobType");
        TYPE = typeBuilder.unCompleteType();
        kind = typeBuilder.declareStringField("kind");
        fields = typeBuilder.declareGlobUnionArrayField("fields",
                Arrays.asList(
                        BooleanFieldType.TYPE, BooleanArrayFieldType.TYPE,
                        StringFieldType.TYPE, StringArrayFieldType.TYPE,
                        DoubleFieldType.TYPE, DoubleArrayFieldType.TYPE,
                        IntegerFieldType.TYPE, IntegerArrayFieldType.TYPE,
                        LongFieldType.TYPE, LongArrayFieldType.TYPE,
                        DateFieldType.TYPE, DateTimeFieldType.TYPE,
                        BytesFieldType.TYPE,
                        BigDecimalFieldType.TYPE, BigDecimalArrayFieldType.TYPE,
                        GlobFieldType.TYPE, GlobArrayFieldType.TYPE,
                        GlobUnionFieldType.TYPE, GlobUnionArrayFieldType.TYPE
                ));
        annotations = typeBuilder.declareGlobUnionArrayField("annotations", Collections.emptyList());
        typeBuilder.complete();
//        GlobTypeLoaderFactory.create(GlobTypeType.class).load();
    }
}
