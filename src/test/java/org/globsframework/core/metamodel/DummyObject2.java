package org.globsframework.core.metamodel;

import org.globsframework.core.metamodel.annotations.DoublePrecision;
import org.globsframework.core.metamodel.annotations.KeyField;
import org.globsframework.core.metamodel.fields.DoubleField;
import org.globsframework.core.metamodel.fields.IntegerField;
import org.globsframework.core.metamodel.fields.StringField;

public class DummyObject2 {

    public static final GlobType TYPE;

    public static final IntegerField ID;

    public static final StringField LABEL;

    public static final DoubleField VALUE;

    static {
        GlobTypeBuilder globTypeBuilder = GlobTypeBuilderFactory.create("dummyObject2");
        ID = globTypeBuilder.declareIntegerField("id", KeyField.ZERO);
        LABEL = globTypeBuilder.declareStringField("label");
        VALUE = globTypeBuilder.declareDoubleField("value", DoublePrecision.create(4));
        TYPE = globTypeBuilder.build();
    }
}
