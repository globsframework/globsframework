package org.globsframework.core.metamodel;

import org.globsframework.core.metamodel.annotations.KeyField;
import org.globsframework.core.metamodel.annotations.Required;
import org.globsframework.core.metamodel.fields.IntegerField;
import org.globsframework.core.metamodel.fields.StringField;

public class DummyObjectWithRequiredFields {
    public static GlobType TYPE;

    public static IntegerField ID;

    public static IntegerField VALUE;

    public static StringField NAME;

    static {
        GlobTypeBuilder builder = GlobTypeBuilderFactory.create("dummyObjectWithRequiredFields");
        ID = builder.declareIntegerField("id", KeyField.ZERO);
        VALUE = builder.declareIntegerField("value", Required.UNIQUE_GLOB);
        NAME = builder.declareStringField("name", Required.UNIQUE_GLOB);
        TYPE = builder.build();
    }
}
