package org.globsframework.core.metamodel.utils;

import org.globsframework.core.metamodel.*;
import org.globsframework.core.metamodel.annotations.FieldName_;
import org.globsframework.core.metamodel.annotations.FieldName;
import org.globsframework.core.metamodel.annotations.KeyField_;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.metamodel.fields.IntegerField;
import org.globsframework.core.metamodel.impl.DefaultGlobModel;
import org.globsframework.core.utils.TestUtils;
import org.globsframework.core.utils.exceptions.UnexpectedApplicationState;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class DefaultGlobTypeTest {
    private GlobType globType;
    private Field field;
    private GlobModel globModel;

    @Test
    public void testFields() {
        initGlobType();
        assertEquals("type", globType.getName());
        assertNotNull(globType.findField("field1"));
        assertNull(globType.findField("Field1"));
        assertNotNull(field);
        TestUtils.assertFails(() -> globType.getFields(), UnexpectedApplicationState.class);
    }

    @Test
    public void testFindFieldByAnnotation() {
        Field qty = GlobTypeUtils.findFieldWithAnnotation(TypeWithAnnotation.TYPE, FieldName.create("qty"));
        assertSame(qty, TypeWithAnnotation.F1);

        Field sku = GlobTypeUtils.findFieldWithAnnotation(TypeWithAnnotation.TYPE, FieldName.create("ean"));
        assertSame(sku, TypeWithAnnotation.F2);
    }

    public static class Type {
        public static GlobType TYPE;

        @KeyField_
        public static IntegerField FIELD1;
    }

    private void initGlobType() {
        Type.TYPE = null;
        final GlobTypeBuilder globTypeBuilder = GlobTypeBuilderFactory.create("type");
        globTypeBuilder.declareIntegerField("field1");
        globType = globTypeBuilder.build();
        field = globType.getField("field1");
        globModel = new DefaultGlobModel(globType);
    }


    public static class TypeWithAnnotation {
        public static GlobType TYPE;

        @FieldName_("qty")
        public static IntegerField F1;

        @FieldName_("ean")
        public static IntegerField F2;

        static {
            final GlobTypeBuilder globTypeBuilder = GlobTypeBuilderFactory.create("typeWithAnnotation");
            F1 = globTypeBuilder.declareIntegerField("F1", FieldName.create("qty"));
            F2 = globTypeBuilder.declareIntegerField("F2", FieldName.create("ean"));
            TYPE = globTypeBuilder.build();
        }
    }

}
