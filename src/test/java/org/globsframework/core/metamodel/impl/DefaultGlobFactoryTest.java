package org.globsframework.core.metamodel.impl;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilderFactory;
import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.model.MutableGlob;
import org.globsframework.core.model.globaccessor.get.*;
import org.globsframework.core.model.globaccessor.set.*;
import org.globsframework.core.utils.exceptions.InvalidParameter;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
IA generated test.
 */

public class DefaultGlobFactoryTest {

    @Test
    public void testCreate() {
        GlobType type = GlobTypeBuilderFactory.create("test")
                .addIntegerField("int")
                .build();
        DefaultGlobFactory factory = new DefaultGlobFactory(type);
        MutableGlob glob = factory.create(null);
        assertNotNull(glob);
        assertEquals(type, glob.getType());
    }

    @Test
    public void testGettersAndSetters() {
        GlobType type = GlobTypeBuilderFactory.create("test")
                .addIntegerField("int")
                .addStringField("string")
                .addDoubleField("double")
                .addLongField("long")
                .addBooleanField("boolean")
                .addBigDecimalField("bigDecimal")
                .addDateField("date")
                .addDateTimeField("dateTime")
                .addBytesField("bytes")
                .addIntegerArrayField("intArray")
                .addDoubleArrayField("doubleArray")
                .addStringArrayField("stringArray")
                .addBooleanArrayField("booleanArray")
                .addLongArrayField("longArray")
                .addBigDecimalArrayField("bigDecimalArray")
                .build();

        DefaultGlobFactory factory = new DefaultGlobFactory(type);
        MutableGlob glob = factory.create(null);

        // Integer
        IntegerField intField = type.getTypedField("int");
        GlobSetIntAccessor setInt = (GlobSetIntAccessor) factory.getSetValueAccessor(intField);
        GlobGetIntAccessor getInt = (GlobGetIntAccessor) factory.getGetValueAccessor(intField);
        setInt.set(glob, 10);
        assertEquals(10, getInt.get(glob));
        assertTrue(getInt.isSet(glob));
        assertFalse(getInt.isNull(glob));
        assertEquals(10, getInt.get(glob, 0));
        assertEquals(10, getInt.getNative(glob));
        setInt.set(glob, null);
        assertNull(getInt.get(glob));
        assertTrue(getInt.isNull(glob));
        assertEquals(5, getInt.get(glob, 5));
        assertEquals(0, getNative(getInt, glob));

        // String
        StringField stringField = type.getTypedField("string");
        GlobSetStringAccessor setString = (GlobSetStringAccessor) factory.getSetValueAccessor(stringField);
        GlobGetStringAccessor getString = (GlobGetStringAccessor) factory.getGetValueAccessor(stringField);
        setString.set(glob, "hello");
        assertEquals("hello", getString.get(glob));
        setString.set(glob, null);
        assertNull(getString.get(glob));

        // Double
        DoubleField doubleField = type.getTypedField("double");
        GlobSetDoubleAccessor setDouble = (GlobSetDoubleAccessor) factory.getSetValueAccessor(doubleField);
        GlobGetDoubleAccessor getDouble = (GlobGetDoubleAccessor) factory.getGetValueAccessor(doubleField);
        setDouble.set(glob, 3.14);
        assertEquals(3.14, getDouble.get(glob));
        setDouble.setNative(glob, 2.71);
        assertEquals(2.71, getDouble.getNative(glob));

        // Long
        LongField longField = type.getTypedField("long");
        GlobSetLongAccessor setLong = (GlobSetLongAccessor) factory.getSetValueAccessor(longField);
        GlobGetLongAccessor getLong = (GlobGetLongAccessor) factory.getGetValueAccessor(longField);
        setLong.set(glob, 100L);
        assertEquals(100L, getLong.get(glob));
        setLong.setNative(glob, 200L);
        assertEquals(200L, getLong.getNative(glob));

        // Boolean
        BooleanField booleanField = type.getTypedField("boolean");
        GlobSetBooleanAccessor setBoolean = (GlobSetBooleanAccessor) factory.getSetValueAccessor(booleanField);
        GlobGetBooleanAccessor getBoolean = (GlobGetBooleanAccessor) factory.getGetValueAccessor(booleanField);
        setBoolean.set(glob, true);
        assertEquals(true, getBoolean.get(glob));
        setBoolean.setNative(glob, false);
        assertEquals(false, getBoolean.getNative(glob));

        // BigDecimal
        BigDecimalField bigDecimalField = type.getTypedField("bigDecimal");
        GlobSetBigDecimalAccessor setBigDecimal = (GlobSetBigDecimalAccessor) factory.getSetValueAccessor(bigDecimalField);
        GlobGetBigDecimalAccessor getBigDecimal = (GlobGetBigDecimalAccessor) factory.getGetValueAccessor(bigDecimalField);
        BigDecimal bd = new BigDecimal("123.45");
        setBigDecimal.set(glob, bd);
        assertEquals(bd, getBigDecimal.get(glob));

        // Date
        DateField dateField = type.getTypedField("date");
        GlobSetDateAccessor setDate = (GlobSetDateAccessor) factory.getSetValueAccessor(dateField);
        GlobGetDateAccessor getDate = (GlobGetDateAccessor) factory.getGetValueAccessor(dateField);
        LocalDate now = LocalDate.now();
        setDate.set(glob, now);
        assertEquals(now, getDate.get(glob));

        // DateTime
        DateTimeField dateTimeField = type.getTypedField("dateTime");
        GlobSetDateTimeAccessor setDateTime = (GlobSetDateTimeAccessor) factory.getSetValueAccessor(dateTimeField);
        GlobGetDateTimeAccessor getDateTime = (GlobGetDateTimeAccessor) factory.getGetValueAccessor(dateTimeField);
        ZonedDateTime dateTime = ZonedDateTime.now();
        setDateTime.set(glob, dateTime);
        assertEquals(dateTime, getDateTime.get(glob));

        // Bytes
        BytesField bytesField = type.getTypedField("bytes");
        GlobSetBytesAccessor setBytes = (GlobSetBytesAccessor) factory.getSetValueAccessor(bytesField);
        GlobGetBytesAccessor getBytes = (GlobGetBytesAccessor) factory.getGetValueAccessor(bytesField);
        byte[] bytes = new byte[]{1, 2, 3};
        setBytes.set(glob, bytes);
        assertArrayEquals(bytes, getBytes.get(glob));

        // IntegerArray
        IntegerArrayField intArrayField = type.getTypedField("intArray");
        GlobSetIntArrayAccessor setIntArray = (GlobSetIntArrayAccessor) factory.getSetValueAccessor(intArrayField);
        GlobGetIntArrayAccessor getIntArray = (GlobGetIntArrayAccessor) factory.getGetValueAccessor(intArrayField);
        int[] intArray = new int[]{1, 2};
        setIntArray.set(glob, intArray);
        assertArrayEquals(intArray, getIntArray.get(glob));

        // DoubleArray
        DoubleArrayField doubleArrayField = type.getTypedField("doubleArray");
        GlobSetDoubleArrayAccessor setDoubleArray = (GlobSetDoubleArrayAccessor) factory.getSetValueAccessor(doubleArrayField);
        GlobGetDoubleArrayAccessor getDoubleArray = (GlobGetDoubleArrayAccessor) factory.getGetValueAccessor(doubleArrayField);
        double[] doubleArray = new double[]{1.1, 2.2};
        setDoubleArray.set(glob, doubleArray);
        assertArrayEquals(doubleArray, getDoubleArray.get(glob));

        // StringArray
        StringArrayField stringArrayField = type.getTypedField("stringArray");
        GlobSetStringArrayAccessor setStringArray = (GlobSetStringArrayAccessor) factory.getSetValueAccessor(stringArrayField);
        GlobGetStringArrayAccessor getStringArray = (GlobGetStringArrayAccessor) factory.getGetValueAccessor(stringArrayField);
        String[] stringArray = new String[]{"a", "b"};
        setStringArray.set(glob, stringArray);
        assertArrayEquals(stringArray, getStringArray.get(glob));

        // BooleanArray
        BooleanArrayField booleanArrayField = type.getTypedField("booleanArray");
        GlobSetBooleanArrayAccessor setBooleanArray = (GlobSetBooleanArrayAccessor) factory.getSetValueAccessor(booleanArrayField);
        GlobGetBooleanArrayAccessor getBooleanArray = (GlobGetBooleanArrayAccessor) factory.getGetValueAccessor(booleanArrayField);
        boolean[] booleanArray = new boolean[]{true, false};
        setBooleanArray.set(glob, booleanArray);
        assertArrayEquals(booleanArray, getBooleanArray.get(glob));

        // LongArray
        LongArrayField longArrayField = type.getTypedField("longArray");
        GlobSetLongArrayAccessor setLongArray = (GlobSetLongArrayAccessor) factory.getSetValueAccessor(longArrayField);
        GlobGetLongArrayAccessor getLongArray = (GlobGetLongArrayAccessor) factory.getGetValueAccessor(longArrayField);
        long[] longArray = new long[]{1L, 2L};
        setLongArray.set(glob, longArray);
        assertArrayEquals(longArray, getLongArray.get(glob));

        // BigDecimalArray
        BigDecimalArrayField bigDecimalArrayField = type.getTypedField("bigDecimalArray");
        GlobSetBigDecimalArrayAccessor setBigDecimalArray = (GlobSetBigDecimalArrayAccessor) factory.getSetValueAccessor(bigDecimalArrayField);
        GlobGetBigDecimalArrayAccessor getBigDecimalArray = (GlobGetBigDecimalArrayAccessor) factory.getGetValueAccessor(bigDecimalArrayField);
        BigDecimal[] bigDecimalArray = new BigDecimal[]{new BigDecimal("1.1"), new BigDecimal("2.2")};
        setBigDecimalArray.set(glob, bigDecimalArray);
        assertArrayEquals(bigDecimalArray, getBigDecimalArray.get(glob));
    }

    private int getNative(GlobGetIntAccessor getInt, MutableGlob glob) {
        return getInt.getNative(glob);
    }

    @Test
    public void testGlobAndUnionAccessors() {
        GlobType innerType = GlobTypeBuilderFactory.create("inner").addIntegerField("val").build();
        GlobType type = GlobTypeBuilderFactory.create("test")
                .addGlobField("glob", java.util.Collections.emptyList(), () -> innerType)
                .addGlobArrayField("globArray", java.util.Collections.emptyList(), () -> innerType)
                .addUnionGlobField("union", java.util.Collections.emptyList(), new java.util.function.Supplier[]{() -> innerType})
                .addUnionGlobArrayField("unionArray", java.util.Collections.emptyList(), new java.util.function.Supplier[]{() -> innerType})
                .build();

        DefaultGlobFactory factory = new DefaultGlobFactory(type);
        MutableGlob glob = factory.create(null);
        MutableGlob inner = innerType.instantiate().set(innerType.getTypedField("val"), Integer.valueOf(1));

        // GlobField
        GlobField globField = type.getTypedField("glob");
        GlobSetGlobAccessor setGlob = (GlobSetGlobAccessor) factory.getSetValueAccessor(globField);
        GlobGetGlobAccessor getGlob = (GlobGetGlobAccessor) factory.getGetValueAccessor(globField);
        setGlob.set(glob, inner);
        assertEquals(inner, getGlob.get(glob));

        // GlobArrayField
        GlobArrayField globArrayField = type.getTypedField("globArray");
        GlobSetGlobArrayAccessor setGlobArray = (GlobSetGlobArrayAccessor) factory.getSetValueAccessor(globArrayField);
        GlobGetGlobArrayAccessor getGlobArray = (GlobGetGlobArrayAccessor) factory.getGetValueAccessor(globArrayField);
        MutableGlob[] inners = new MutableGlob[]{inner};
        setGlobArray.set(glob, inners);
        assertArrayEquals(inners, getGlobArray.get(glob));

        // GlobUnionField
        GlobUnionField unionField = type.getTypedField("union");
        GlobSetGlobAccessor setUnion = (GlobSetGlobAccessor) factory.getSetValueAccessor(unionField);
        GlobGetGlobAccessor getUnion = (GlobGetGlobAccessor) factory.getGetValueAccessor(unionField);
        setUnion.set(glob, inner);
        assertEquals(inner, getUnion.get(glob));

        // GlobArrayUnionField
        GlobArrayUnionField unionArrayField = type.getTypedField("unionArray");
        GlobSetGlobArrayAccessor setUnionArray = (GlobSetGlobArrayAccessor) factory.getSetValueAccessor(unionArrayField);
        GlobGetGlobArrayAccessor getUnionArray = (GlobGetGlobArrayAccessor) factory.getGetValueAccessor(unionArrayField);
        setUnionArray.set(glob, inners);
        assertArrayEquals(inners, getUnionArray.get(glob));
    }

    @Test
    public void testFieldCheck() {
        GlobType type1 = GlobTypeBuilderFactory.create("type1").addIntegerField("f1").build();
        GlobType type2 = GlobTypeBuilderFactory.create("type2").addIntegerField("f1").build();
        
        DefaultGlobFactory factory1 = new DefaultGlobFactory(type1);
        IntegerField f1InType2 = type2.getTypedField("f1");

        assertThrows(InvalidParameter.class, () -> {
            factory1.getGetValueAccessor(f1InType2);
        });

        assertThrows(InvalidParameter.class, () -> {
            factory1.getSetValueAccessor(f1InType2);
        });
    }
}
