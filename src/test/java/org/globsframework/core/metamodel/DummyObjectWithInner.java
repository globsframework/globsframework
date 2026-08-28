package org.globsframework.core.metamodel;

import org.globsframework.core.metamodel.annotations.KeyField;
import org.globsframework.core.metamodel.fields.*;

import java.util.function.Supplier;

public class DummyObjectWithInner {

    public static final GlobType TYPE;

    public static final IntegerField ID;

    public static final BytesField byteArrayData;

    public static final GlobField<DummyObjectInner> VALUE;

    public static final GlobArrayField<DummyObjectInner> VALUES;

    public static final GlobUnionField VALUE_UNION;

    public static final GlobArrayUnionField VALUES_UNION;

    public static final StringArrayField STRS;

    static {
        final GlobTypeBuilder globTypeBuilder = GlobTypeBuilderFactory.create("dummyObjectWithInner");
        ID = globTypeBuilder.declareIntegerField("id", KeyField.ZERO);
        byteArrayData = globTypeBuilder.declareBytesField("byteArrayData");
        VALUE = globTypeBuilder.declareGlobField("value", () -> DummyObjectInner.TYPE);
        VALUES = globTypeBuilder.declareGlobArrayField("values", () -> DummyObjectInner.TYPE);
        VALUE_UNION = globTypeBuilder.declareGlobUnionField("valueUnion",
                new Supplier[]{() -> DummyObjectInner.TYPE, () -> DummyObject.TYPE});
        VALUES_UNION = globTypeBuilder.declareGlobUnionArrayField("valuesUnion",
                new Supplier[]{() -> DummyObjectInner.TYPE, () -> DummyObject.TYPE});
        STRS = globTypeBuilder.declareStringArrayField("strs");
        TYPE = globTypeBuilder.build();
    }
}
