package org.globsframework.core.metamodel;

import org.globsframework.core.metamodel.annotations.KeyField;
import org.globsframework.core.metamodel.annotations.KeyField_;
import org.globsframework.core.metamodel.annotations.MaxSize;
import org.globsframework.core.metamodel.annotations.MaxSize_;
import org.globsframework.core.metamodel.annotations.NamingField;
import org.globsframework.core.metamodel.annotations.NamingField_;
import org.globsframework.core.metamodel.fields.IntegerField;
import org.globsframework.core.metamodel.fields.StringField;

public class DummyObjectWithMaxSizeString {
    public static GlobType TYPE;

    @KeyField_
    public static IntegerField ID;

    @NamingField_
    @MaxSize_(10)
    public static StringField TEXT;

    static {
        GlobTypeBuilder builder = GlobTypeBuilderFactory.create("dummyObjectWithMaxSizeString");
        ID = builder.declareIntegerField("id", KeyField.ZERO);
        TEXT = builder.declareStringField("text", NamingField.UNIQUE_GLOB, MaxSize.create(10));
        TYPE = builder.build();
    }
}
