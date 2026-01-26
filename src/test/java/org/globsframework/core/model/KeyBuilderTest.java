package org.globsframework.core.model;

import org.globsframework.core.metamodel.DummyObject;
import org.globsframework.core.metamodel.DummyObject2;
import org.globsframework.core.metamodel.DummyObjectWithCompositeKey;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.model.impl.CompositeKey;
import org.globsframework.core.model.impl.SingleFieldKey;
import org.globsframework.core.model.utils.FieldValueGetter;
import org.globsframework.core.utils.exceptions.InvalidParameter;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.globsframework.core.model.KeyBuilder.newKey;
import static org.junit.jupiter.api.Assertions.*;

public class KeyBuilderTest {
    @Test
    public void testCompositeKeyUsingCreateFromValues() throws Exception {
        FieldValues values = FieldValuesBuilder
                .init(DummyObjectWithCompositeKey.ID1, 1)
                .set(DummyObjectWithCompositeKey.ID2, 2)
                .get();

        Key key = KeyBuilder.createFromValues(DummyObjectWithCompositeKey.TYPE, values);
        assertEquals(DummyObjectWithCompositeKey.TYPE, key.getGlobType());
        assertEquals("dummyObjectWithCompositeKey[id1=1, id2=2]", key.toString());
    }

    @Test
    public void testCompositeKeyUsingInit() throws Exception {
        Key key = KeyBuilder
                .init(DummyObjectWithCompositeKey.ID1, 1)
                .set(DummyObjectWithCompositeKey.ID2, 2)
                .get();
        assertEquals(DummyObjectWithCompositeKey.TYPE, key.getGlobType());
        assertEquals("dummyObjectWithCompositeKey[id1=1, id2=2]", key.toString());
    }

    @Test
    public void testAllFieldsMustUseTheSameType() throws Exception {
        try {
            KeyBuilder
                    .init(DummyObjectWithCompositeKey.ID1, 1)
                    .set(DummyObject.ID, 2);
            fail();
        } catch (InvalidParameter e) {
            assertTrue(e.getMessage().contains("dummyObject"));
            assertTrue(e.getMessage().contains("dummyObjectWithCompositeKey"));
        }
    }

    @Test
    public void testSingleKey() throws Exception {
        Key key = newKey(DummyObject.TYPE, 3);
        assertEquals("dummyObject[id=3]", key.toString());
        assertTrue(key instanceof SingleFieldKey);
    }

    @Test
    public void testEqualsAndHashCodeWithSingleFieldKey() throws Exception {
        Key key1a = newKey(DummyObject.TYPE, 1);
        Key key1b = newKey(DummyObject.TYPE, 1);
        Key key2 = newKey(DummyObject.TYPE, 2);
        Key key1_obj2 = newKey(DummyObject2.TYPE, 1);

        assertEquals(key1a, key1a);
        assertEquals(key1a, key1b);
        assertEquals(key1b, key1a);

        assertFalse(key1a.equals(key1_obj2));

        assertFalse(key1a.equals(key2));
        assertFalse(key1a.equals(null));
        assertFalse(key1a.equals("blah"));
    }

    @Test
    public void testEqualsAndHashCodeWithCompositeKey() throws Exception {
        FieldValues values = FieldValuesBuilder
                .init(DummyObjectWithCompositeKey.ID1, 1)
                .set(DummyObjectWithCompositeKey.ID2, 2)
                .get();
        Key key1 = KeyBuilder.createFromValues(DummyObjectWithCompositeKey.TYPE, values);
        Key key2 = KeyBuilder.createFromValues(DummyObjectWithCompositeKey.TYPE, values);

        FieldValues otherValues = FieldValuesBuilder
                .init(DummyObjectWithCompositeKey.ID1, 1)
                .set(DummyObjectWithCompositeKey.ID2, 3)
                .get();
        Key otherKey = KeyBuilder.createFromValues(DummyObjectWithCompositeKey.TYPE, otherValues);

        assertEquals(key1, key2);
        assertEquals(key2, key1);
        assertEquals(key1.hashCode(), key2.hashCode());

        assertFalse(key1.equals(otherKey));

        assertFalse(key1.equals(null));
        assertFalse(key1.equals("blah"));
    }

    @Test
    public void testSingleKeysCanBeUsedInMapsAndMixedWithCompositeKeys() throws Exception {
        SingleFieldKey singleKey = new SingleFieldKey(DummyObject.TYPE, 2);
        CompositeKey compositeKey = new CompositeKey(DummyObject.TYPE, new FieldValueGetter() {
            public boolean contains(Field field) {
                return true;
            }

            public Object get(Field field) {
                return 2;
            }
        });

        assertEquals(singleKey, compositeKey);
        assertEquals(compositeKey, singleKey);

        Map<Key, String> map = new HashMap<Key, String>();
        map.put(singleKey, "a");
        assertEquals("a", map.get(singleKey));
        assertEquals("a", map.get(compositeKey));

        map.put(compositeKey, "b");
        assertEquals("b", map.get(singleKey));
        assertEquals("b", map.get(compositeKey));
    }

    @Test
    public void testCreateSingleCheck() throws Exception {
        try {
            newKey(DummyObjectWithCompositeKey.TYPE, 3);
            fail();
        } catch (InvalidParameter e) {
            assertEquals("Cannot use a single field key for type dummyObjectWithCompositeKey - " +
                            "key fields=[dummyObjectWithCompositeKey.id1, dummyObjectWithCompositeKey.id2]",
                    e.getMessage());
        }
    }

    @Test
    public void testReturnsAnOptimizedStructureForASingleKeyType() throws Exception {
        Map<Field, Object> values = new HashMap<Field, Object>();
        values.put(DummyObject.ID, 2);
        values.put(DummyObject.NAME, "a");
        Key key = KeyBuilder.createFromValues(DummyObject.TYPE, values);
        assertTrue(key instanceof SingleFieldKey);
        assertEquals(Integer.valueOf(2), key.get(DummyObject.ID));
        try {
            key.get(DummyObject.NAME);
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("dummyObject.name is not the a key field for 'dummyObject'"));
        }
    }

    @Test
    public void testOnlyValidValuesAreAuthorized() throws Exception {
        try {
            newKey(DummyObjectWithCompositeKey.TYPE, "a");
            fail();
        } catch (InvalidParameter e) {
            assertEquals("Cannot use a single field key for type dummyObjectWithCompositeKey - " +
                            "key fields=[dummyObjectWithCompositeKey.id1, dummyObjectWithCompositeKey.id2]",
                    e.getMessage());
        }

        try {
            Map<Field, Object> values = new HashMap<Field, Object>();
            values.put(DummyObject.ID, "a");
            KeyBuilder.createFromValues(DummyObject.TYPE, values);
            fail();
        } catch (InvalidParameter e) {
            assertEquals("Value 'a' (java.lang.String) is not authorized for field: " +
                    DummyObject.ID.getName() + " (expected java.lang.Integer)", e.getMessage());
        }
    }
}
