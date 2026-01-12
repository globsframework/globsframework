package org.globsframework.core.metamodel;

import org.globsframework.core.metamodel.annotations.KeyField;
import org.globsframework.core.metamodel.annotations.KeyField_;
import org.globsframework.core.metamodel.fields.StringField;

public class DummyObjectWithStringKey {
    public static GlobType TYPE;

    @KeyField_
    public static StringField ID;

    static {
        GlobTypeBuilder builder = GlobTypeBuilderFactory.create("dummyObjectWithStringKey");
        ID = builder.declareStringField("id", KeyField.ZERO);
        TYPE = builder.build();
    }
}
