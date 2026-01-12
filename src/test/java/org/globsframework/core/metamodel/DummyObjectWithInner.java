package org.globsframework.core.metamodel;

import org.globsframework.core.metamodel.annotations.KeyField;
import org.globsframework.core.metamodel.annotations.KeyField_;
import org.globsframework.core.metamodel.annotations.Target;
import org.globsframework.core.metamodel.annotations.Targets;
import org.globsframework.core.metamodel.fields.*;

import java.util.function.Supplier;

public class DummyObjectWithInner {

    public static final GlobType TYPE;

    @KeyField_
    public static final IntegerField ID;

    public static final BytesField byteArrayData;

    @Target(DummyObjectInner.class)
    public static final GlobField VALUE;

    @Target(DummyObjectInner.class)
    public static final GlobArrayField VALUES;

    @Targets({DummyObjectInner.class, DummyObject.class})
    public static final GlobUnionField VALUE_UNION;

    @Targets({DummyObjectInner.class, DummyObject.class})
    public static final GlobArrayUnionField VALUES_UNION;

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
        TYPE = globTypeBuilder.build();
    }
}
