package org.globsframework.core.metamodel;

import org.globsframework.core.metamodel.annotations.KeyField_;
import org.globsframework.core.metamodel.annotations.Target;
import org.globsframework.core.metamodel.annotations.Targets;
import org.globsframework.core.metamodel.fields.*;

public class DummyObjectWithInner {

    public static GlobType TYPE;

    @KeyField_
    public static IntegerField ID;

    public static BytesField byteArrayData;

    @Target(value = DummyObjectInner.class)
    public static GlobField VALUE;

    @Target(value = DummyObjectInner.class)
    public static GlobArrayField VALUES;

    @Targets({DummyObjectInner.class, DummyObject.class})
    public static GlobUnionField VALUE_UNION;

    @Targets({DummyObjectInner.class, DummyObject.class})
    public static GlobArrayUnionField VALUES_UNION;


    static {
        GlobTypeLoaderFactory.create(DummyObjectWithInner.class).load();
    }
}
