package org.globsframework.core.metamodel;

import org.globsframework.core.metamodel.fields.DoubleField;
import org.globsframework.core.metamodel.fields.IntegerField;
import org.globsframework.core.model.MutableGlob;

public class DummyObjectInner {

    public static GlobType TYPE;

    public static IntegerField DATE;

    public static DoubleField VALUE;

    static {
        GlobTypeLoaderFactory.create(DummyObjectInner.class).load();
    }

    public static MutableGlob create(double value) {
        return TYPE.instantiate()
                .set(VALUE, value);
    }
}
