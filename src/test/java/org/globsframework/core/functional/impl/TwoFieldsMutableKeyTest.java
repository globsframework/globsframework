package org.globsframework.core.functional.impl;

import org.globsframework.core.functional.FunctionalKey;
import org.globsframework.core.metamodel.DummyObjectWithCompositeKey;
import org.globsframework.core.metamodel.fields.BytesField;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.metamodel.fields.FieldValueVisitor;
import org.globsframework.core.metamodel.fields.IntegerField;
import org.globsframework.core.model.FieldValue;
import org.globsframework.core.model.FieldValues;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TwoFieldsMutableKeyTest {

    private final IntegerField ID1 = DummyObjectWithCompositeKey.ID1;
    private final IntegerField ID2 = DummyObjectWithCompositeKey.ID2;
    private final Field NAME = DummyObjectWithCompositeKey.NAME; // non-key field

    @Test
    public void testSetGetContainsSize() {
        TwoFunctionalKeyBuilder builder = new TwoFunctionalKeyBuilder(ID1, ID2);
        TwoFieldsMutableKey key = new TwoFieldsMutableKey(builder);

        // initially values are null
        assertNull(key.get(ID1));
        assertNull(key.get(ID2));

        // contains and size
        assertTrue(key.contains(ID1));
        assertTrue(key.contains(ID2));
        assertFalse(key.contains(NAME));
        assertEquals(2, key.size());

        // set and get
        key.set(ID1, 11);
        key.set(ID2, 22);
        assertEquals(Integer.valueOf(11), key.get(ID1));
        assertEquals(Integer.valueOf(22), key.get(ID2));
    }

    @Test
    public void testAcceptAndApplyOrderAndValues() throws Exception {
        TwoFunctionalKeyBuilder builder = new TwoFunctionalKeyBuilder(ID1, ID2);
        TwoFieldsMutableKey key = new TwoFieldsMutableKey(builder);
        key.set(ID1, 1).set(ID2, 2);

        // accept
        List<String> visited = new ArrayList<>();
        FieldValueVisitor visitor = new FieldValueVisitor() {
            public void visitDouble(org.globsframework.core.metamodel.fields.DoubleField field, Double value) {}
            public void visitInteger(IntegerField field, Integer value) { visited.add(field.getName() + ":" + value); }
            public void visitString(org.globsframework.core.metamodel.fields.StringField field, String value) { visited.add(field.getName() + ":" + value); }
            public void visitBoolean(org.globsframework.core.metamodel.fields.BooleanField field, Boolean value) {}
            public void visitLong(org.globsframework.core.metamodel.fields.LongField field, Long value) {}
            public void visitLongArray(org.globsframework.core.metamodel.fields.LongArrayField field, long[] value) {}
            public void visitBigDecimal(org.globsframework.core.metamodel.fields.BigDecimalField field, java.math.BigDecimal value) {}
            public void visitBigDecimalArray(org.globsframework.core.metamodel.fields.BigDecimalArrayField field, java.math.BigDecimal[] value) {}
            public void visitBytes(BytesField field, byte[] value) {}
            public void visitDoubleArray(org.globsframework.core.metamodel.fields.DoubleArrayField field, double[] value) {}
            public void visitIntegerArray(org.globsframework.core.metamodel.fields.IntegerArrayField field, int[] value) {}
            public void visitStringArray(org.globsframework.core.metamodel.fields.StringArrayField field, String[] value) {}
            public void visitBooleanArray(org.globsframework.core.metamodel.fields.BooleanArrayField field, boolean[] value) {}
            public void visitDate(org.globsframework.core.metamodel.fields.DateField field, java.time.LocalDate value) {}
            public void visitDateTime(org.globsframework.core.metamodel.fields.DateTimeField field, java.time.ZonedDateTime value) {}
            public void visitGlob(org.globsframework.core.metamodel.fields.GlobField field, org.globsframework.core.model.Glob value) {}
            public void visitGlobArray(org.globsframework.core.metamodel.fields.GlobArrayField field, org.globsframework.core.model.Glob[] values) {}
            public void visitUnionGlob(org.globsframework.core.metamodel.fields.GlobUnionField field, org.globsframework.core.model.Glob value) {}
            public void visitUnionGlobArray(org.globsframework.core.metamodel.fields.GlobArrayUnionField field, org.globsframework.core.model.Glob[] values) {}
        };
        key.accept(visitor);
        assertEquals(Arrays.asList(ID1.getName() + ":1", ID2.getName() + ":2"), visited);

        // apply
        List<String> applied = new ArrayList<>();
        FieldValues.Functor functor = (field, value) -> applied.add(field.getName() + ":" + value);
        key.apply(functor);
        assertEquals(Arrays.asList(ID1.getName() + ":1", ID2.getName() + ":2"), applied);
    }

    @Test
    public void testToArrayAndBuilder() {
        TwoFunctionalKeyBuilder builder = new TwoFunctionalKeyBuilder(ID1, ID2);
        TwoFieldsMutableKey key = new TwoFieldsMutableKey(builder);
        key.set(ID1, 5).set(ID2, 8);

        FieldValue[] array = key.toArray();
        assertEquals(2, array.length);
        // Order must be field1 then field2
        assertSame(ID1, array[0].getField());
        assertEquals(5, array[0].getValue());
        assertSame(ID2, array[1].getField());
        assertEquals(8, array[1].getValue());

        assertSame(builder, key.getBuilder());
    }

    @Test
    public void testEqualsHashCodeAndCreateClone() {
        TwoFunctionalKeyBuilder builder = new TwoFunctionalKeyBuilder(ID1, ID2);
        TwoFieldsMutableKey key1 = new TwoFieldsMutableKey(builder);
        key1.set(ID1, 7).set(ID2, 9);

        // Same builder, same values => equal and same hash
        TwoFieldsMutableKey key2 = new TwoFieldsMutableKey(builder);
        key2.set(ID1, 7).set(ID2, 9);
        assertEquals(key1, key2);
        assertEquals(key1.hashCode(), key2.hashCode());

        // Different builder instance (same fields) => equal
        TwoFunctionalKeyBuilder otherBuilder = new TwoFunctionalKeyBuilder(ID1, ID2);
        TwoFieldsMutableKey key3 = new TwoFieldsMutableKey(otherBuilder);
        key3.set(ID1, 7).set(ID2, 9);
        assertEquals(key1, key3);

        // Different values => NOT equal
        TwoFieldsMutableKey key4 = new TwoFieldsMutableKey(builder);
        key4.set(ID1, 7).set(ID2, 10);
        assertNotEquals(key1, key4);

        // create() must clone values and keep builder
        TwoFieldsMutableKey copy = (TwoFieldsMutableKey) key1.create();
        assertEquals(key1, copy);
        assertNotSame(key1, copy);
        assertSame(builder, copy.getBuilder());
    }

    @Test
    public void testIsSetBehavior() {
        TwoFunctionalKeyBuilder builder = new TwoFunctionalKeyBuilder(ID1, ID2);
        TwoFieldsMutableKey key = new TwoFieldsMutableKey(builder);

        // According to current implementation, null is considered set (not equal to NULL_VALUE sentinel)
        assertFalse(key.isSet(ID1));
        assertFalse(key.isSet(ID2));

        key.set(ID1, null).set(ID2, 3);
        assertTrue(key.isSet(ID1));
        assertTrue(key.isSet(ID2));

        key.unset(ID1);
        final FunctionalKey newKey = builder.create(key);
        assertFalse(newKey.isSet(ID1));
        assertTrue(newKey.isSet(ID2));
    }
}
