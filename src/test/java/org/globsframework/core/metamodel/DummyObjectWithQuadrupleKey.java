package org.globsframework.core.metamodel;

import org.globsframework.core.metamodel.annotations.KeyField;
import org.globsframework.core.metamodel.annotations.KeyField_;
import org.globsframework.core.metamodel.annotations.NamingField;
import org.globsframework.core.metamodel.annotations.NamingField_;
import org.globsframework.core.metamodel.fields.IntegerField;
import org.globsframework.core.metamodel.fields.StringField;

public class DummyObjectWithQuadrupleKey {

    public static GlobType TYPE;

    @KeyField_
    public static IntegerField ID1;
    @KeyField_
    public static IntegerField ID2;
    @KeyField_
    public static IntegerField ID3;
    @KeyField_
    public static IntegerField ID4;

    @NamingField_
    public static StringField NAME;

    static {
        GlobTypeBuilder builder = GlobTypeBuilderFactory.create("dummyObjectWithQuadrupleKey");
        ID1 = builder.declareIntegerField("id1", KeyField.ZERO);
        ID2 = builder.declareIntegerField("id2", KeyField.ONE);
        ID3 = builder.declareIntegerField("id3", KeyField.TWO);
        ID4 = builder.declareIntegerField("id4", KeyField.THREE);
        NAME = builder.declareStringField("name", NamingField.UNIQUE_GLOB);
        TYPE = builder.build();
    }
}
