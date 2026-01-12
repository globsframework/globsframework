package org.globsframework.core.metamodel;

import org.globsframework.core.metamodel.fields.DoubleField;
import org.globsframework.core.metamodel.fields.IntegerField;
import org.globsframework.core.model.MutableGlob;

public class DummyObjectInner {

    public static final GlobType TYPE;

    public static final IntegerField DATE;

    public static final DoubleField VALUE;

    static {
        final GlobTypeBuilder globTypeBuilder = GlobTypeBuilderFactory.create("DummyObjectInner");
        DATE = globTypeBuilder.declareIntegerField("date");
        VALUE = globTypeBuilder.declareDoubleField("value");
        TYPE = globTypeBuilder.build();
    }

    public static MutableGlob create(double value) {
        return TYPE.instantiate()
                .set(VALUE, value);
    }
}
