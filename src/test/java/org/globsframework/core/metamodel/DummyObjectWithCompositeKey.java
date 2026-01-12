package org.globsframework.core.metamodel;

import org.globsframework.core.metamodel.annotations.KeyField;
import org.globsframework.core.metamodel.annotations.KeyField_;
import org.globsframework.core.metamodel.annotations.NamingField;
import org.globsframework.core.metamodel.annotations.NamingField_;
import org.globsframework.core.metamodel.fields.IntegerField;
import org.globsframework.core.metamodel.fields.StringField;

public class DummyObjectWithCompositeKey {

    public static GlobType TYPE;

    @KeyField_
    public static IntegerField ID1;
    @KeyField_
    public static IntegerField ID2;

    @NamingField_
    public static StringField NAME;

    static {
        final GlobTypeBuilder globTypeBuilder = GlobTypeBuilderFactory.create("dummyObjectWithCompositeKey");
        ID1 = globTypeBuilder.declareIntegerField("id1", KeyField.ZERO);
        ID2 = globTypeBuilder.declareIntegerField("id2", KeyField.ONE);
        NAME = globTypeBuilder.declareStringField("name", NamingField.UNIQUE_GLOB);
        TYPE = globTypeBuilder.build();
    }
}
