package org.globsframework.core.metamodel;

import org.globsframework.core.metamodel.annotations.KeyField;
import org.globsframework.core.metamodel.annotations.KeyField_;
import org.globsframework.core.metamodel.annotations.NamingField;
import org.globsframework.core.metamodel.annotations.NamingField_;
import org.globsframework.core.metamodel.fields.DoubleField;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.metamodel.fields.IntegerField;
import org.globsframework.core.metamodel.fields.StringField;
import org.globsframework.core.metamodel.index.MultiFieldNotUniqueIndex;
import org.globsframework.core.metamodel.index.MultiFieldUniqueIndex;
import org.globsframework.core.metamodel.index.NotUniqueIndex;
import org.globsframework.core.metamodel.index.UniqueIndex;
import org.globsframework.core.metamodel.index.impl.DefaultMultiFieldNotUniqueIndex;

public class DummyObjectIndex {

    public static final GlobType TYPE;

    @KeyField_
    public static IntegerField ID;

    public static DoubleField VALUE;
    public static IntegerField VALUE_1;
    public static IntegerField VALUE_2;
    public static IntegerField DATE;

    @NamingField_
    public static StringField NAME;

    public static StringField UNIQUE_NAME;

    public static MultiFieldNotUniqueIndex VALUES_INDEX;
    public static MultiFieldUniqueIndex VALUES_AND_NAME_INDEX;
    public static UniqueIndex UNIQUE_NAME_INDEX;
    public static NotUniqueIndex DATE_INDEX;


    static {
        final GlobTypeBuilder globTypeBuilder = GlobTypeBuilderFactory.create("dummyObjectIndex");
        ID = globTypeBuilder.declareIntegerField("id", KeyField.ZERO);
        VALUE = globTypeBuilder.declareDoubleField("value");
        VALUE_1 = globTypeBuilder.declareIntegerField("value1");
        VALUE_2 = globTypeBuilder.declareIntegerField("value2");
        DATE = globTypeBuilder.declareIntegerField("date");
        NAME = globTypeBuilder.declareStringField("name", NamingField.UNIQUE_GLOB);

        UNIQUE_NAME = globTypeBuilder.declareStringField("uniqueName", NamingField.UNIQUE_GLOB);
        VALUES_INDEX = globTypeBuilder.addMultiFieldNotUniqueIndex("VALUES_INDEX", VALUE_1, VALUE_2);
        VALUES_AND_NAME_INDEX = globTypeBuilder.addMultiFieldUniqueIndex("VALUES_AND_NAME_INDEX", VALUE_1, VALUE_2, NAME);
        UNIQUE_NAME_INDEX = globTypeBuilder.addUniqueIndex("UNIQUE_NAME_INDEX", UNIQUE_NAME);
        DATE_INDEX = globTypeBuilder.addNotUniqueIndex("DATE_INDEX", DATE);
        TYPE = globTypeBuilder.build();
    }
}
