package org.globsframework.core.metamodel;

import org.globsframework.core.metamodel.annotations.*;
import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.metamodel.type.DataType;
import org.globsframework.core.metamodel.utils.GlobTypeUtils;
import org.globsframework.core.utils.exceptions.InvalidParameter;
import org.globsframework.core.utils.exceptions.ItemAlreadyExists;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.dsig.keyinfo.KeyValue;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GlobTypeBuilderTest {

    @Test
    public void test() throws Exception {
        GlobType type = GlobTypeBuilderFactory.create("aType")
                .addIntegerField("id", KeyField.ZERO)
                .addStringField("string")
                .addIntegerField("int")
                .addLongField("long")
                .addDoubleField("double")
                .addBytesField("blob")
                .addBooleanField("boolean")
                .addBooleanArrayField("booleanArray")
                .addBigDecimalField("bigDecimal")
                .addBigDecimalArrayField("bigArrayDecimal")
                .addDoubleArrayField("doubleArray")
                .addIntegerArrayField("intArray")
                .addLongArrayField("longArray")
                .addDateField("date")
                .addDateTimeField("time")
                .build();

        assertEquals("aType", type.getName());

        Field[] keyFields = type.getKeyFields();
        assertEquals(1, keyFields.length);
        Field key = keyFields[0];
        assertInstanceOf(IntegerField.class, key);
        assertEquals("id", key.getName());

        checkField(type, "string", StringField.class, DataType.String);
        checkField(type, "int", IntegerField.class, DataType.Integer);
        checkField(type, "intArray", IntegerArrayField.class, DataType.IntegerArray);
        checkField(type, "long", LongField.class, DataType.Long);
        checkField(type, "longArray", LongArrayField.class, DataType.LongArray);
        checkField(type, "double", DoubleField.class, DataType.Double);
        checkField(type, "doubleArray", DoubleArrayField.class, DataType.DoubleArray);
        checkField(type, "blob", BytesField.class, DataType.Bytes);
        checkField(type, "boolean", BooleanField.class, DataType.Boolean);
        checkField(type, "booleanArray", BooleanArrayField.class, DataType.BooleanArray);
        checkField(type, "bigDecimal", BigDecimalField.class, DataType.BigDecimal);
        checkField(type, "bigArrayDecimal", BigDecimalArrayField.class, DataType.BigDecimalArray);
        checkField(type, "date", DateField.class, DataType.Date);
        checkField(type, "time", DateTimeField.class, DataType.DateTime);
    }

    private void checkField(GlobType type, String fieldName, Class<? extends Field> fieldClass, DataType dataType) {
        Field field = type.getField(fieldName);
        assertTrue(fieldClass.isAssignableFrom(field.getClass()));
        assertEquals(dataType, field.getDataType());
    }

    @Test
    public void testCannotUseTheSameNameTwice() throws Exception {
        try {
            GlobTypeBuilderFactory.create("aType")
                    .addIntegerField("id", KeyField.ZERO)
                    .addStringField("field")
                    .addIntegerField("field");
            fail();
        } catch (ItemAlreadyExists e) {
            assertEquals("Duplicate field 'field' in type 'aType'", e.getMessage());
        }
    }

    @Disabled
    @Test
    public void testAtLeastOneKeyMustBeDefined() throws Exception {
        try {
            GlobTypeBuilderFactory.create("type").build();
            fail();
        } catch (InvalidParameter e) {
            assertEquals("GlobType type has no key field", e.getMessage());
        }
    }

    @Test
    public void testNamingField() throws Exception {
        GlobType type = GlobTypeBuilderFactory.create("aType")
                .addIntegerField("id", KeyField.ZERO)
                .addStringField("name", NamingField.UNIQUE_GLOB)
                .build();

        StringField field = GlobTypeUtils.findNamingField(type);
        assertNotNull(field);
        assertEquals("name", field.getName());
    }

    @Test
    public void testWithAnnotations() throws Exception {
        GlobTypeBuilderFactory.create("aType")
                .addDoubleField("aDouble", DefaultDouble.create(2.2));

    }

    @Test
    void testGlobTypeAnnotations() {
        final GlobTypeBuilder typeBuilder = GlobTypeBuilderFactory.create("aType")
                .addAnnotations(List.of(DefaultDouble.create(2.2), DefaultString.create("aString")))
                .addAnnotation(DefaultLong.create(3));
        final GlobType type = typeBuilder.build();
        assertTrue(type.hasAnnotation(DefaultDouble.KEY));
        assertTrue(type.hasAnnotation(DefaultString.KEY));
        assertTrue(type.hasAnnotation(DefaultLong.KEY));
    }
}
