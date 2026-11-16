package org.globsframework.core.functional.impl;

import org.globsframework.core.functional.FunctionalKey;
import org.globsframework.core.functional.FunctionalKeyBuilder;
import org.globsframework.core.functional.FunctionalKeyBuilderFactory;
import org.globsframework.core.functional.MutableFunctionalKey;
import org.globsframework.core.metamodel.DummyObject;
import org.globsframework.core.metamodel.DummyObject2;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.model.FieldValue;
import org.globsframework.core.model.MutableGlob;
import org.globsframework.core.utils.exceptions.InvalidParameter;
import org.globsframework.core.utils.exceptions.ItemNotFound;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ThreeFourFieldsMutableKeyTest {

    private static final Field[] THREE = {DummyObject.ID, DummyObject.NAME, DummyObject.PASSWORD};
    private static final Field[] FOUR = {DummyObject.ID, DummyObject.NAME, DummyObject.PASSWORD, DummyObject.VALUE};

    interface Check {
        void run(Field[] fields) throws Exception;
    }

    private static void both(Check check) throws Exception {
        check.run(THREE);
        check.run(FOUR);
    }

    private static FunctionalKeyBuilder builder(Field[] fields) {
        FunctionalKeyBuilderFactory factory = FunctionalKeyBuilderFactory.create(DummyObject.TYPE);
        for (Field field : fields) {
            factory.add(field);
        }
        return factory.create();
    }

    private static MutableGlob glob(int id, String name, byte[] password, Double value) {
        return DummyObject.TYPE.instantiate()
                .set(DummyObject.ID, id).set(DummyObject.NAME, name)
                .set(DummyObject.PASSWORD, password).set(DummyObject.VALUE, value);
    }

    private static void check_factoryPicksTheUnrolledImplementation(Field[] fields) throws Exception {
        FunctionalKeyBuilder builder = builder(fields);
        assertEquals(fields.length == 3 ? ThreeFunctionalKeyBuilder.class : FourFunctionalKeyBuilder.class, builder.getClass());
        assertEquals(fields.length == 3 ? ThreeFieldsMutableKey.class : FourFieldsMutableKey.class, builder.create().getClass());
        assertArrayEquals(fields, builder.getFields());
        assertSame(DummyObject.TYPE, builder.getType());
    }

    private static void check_setGetUnsetContains(Field[] fields) throws Exception {
        MutableFunctionalKey mutable = builder(fields).create();
        FunctionalKey key = mutable.getShared();
        assertEquals(fields.length, key.size());
        for (Field field : fields) {
            assertTrue(key.contains(field));
            assertFalse(key.isSet(field));
            assertNull(key.getValue(field));
        }
        assertFalse(key.contains(DummyObject.COUNT));

        mutable.set(DummyObject.ID, 7).set(DummyObject.NAME, "n").set(DummyObject.PASSWORD, new byte[]{1});
        if (fields.length == 4) {
            mutable.set(DummyObject.VALUE, 2.5);
            assertEquals(2.5, key.get(DummyObject.VALUE));
        }
        assertEquals(7, key.get(DummyObject.ID));
        assertEquals("n", key.get(DummyObject.NAME));
        assertArrayEquals(new byte[]{1}, key.get(DummyObject.PASSWORD));

        mutable.set(DummyObject.NAME, null);
        assertTrue(key.isSet(DummyObject.NAME));
        assertTrue(key.isNull(DummyObject.NAME));
        mutable.unset(DummyObject.NAME);
        assertFalse(key.isSet(DummyObject.NAME));

        assertThrows(ItemNotFound.class, () -> mutable.set(DummyObject.COUNT, 1));
        assertThrows(ItemNotFound.class, () -> key.getValue(DummyObject.COUNT));
        assertThrows(ItemNotFound.class, () -> key.isSet(DummyObject.COUNT));
        assertThrows(ItemNotFound.class, () -> mutable.unset(DummyObject.COUNT));
        assertThrows(ItemNotFound.class, () -> key.getValue(DummyObject2.ID));
        assertThrows(InvalidParameter.class, () -> mutable.unset(DummyObject2.ID));
    }

    private static void check_createFromGlobAndVisitSetFieldsInOrder(Field[] fields) throws Exception {
        FunctionalKeyBuilder builder = builder(fields);
        MutableGlob glob = DummyObject.TYPE.instantiate().set(DummyObject.ID, 3).set(DummyObject.PASSWORD, new byte[]{9});
        FunctionalKey key = builder.create(glob);
        assertTrue(key.isSet(DummyObject.ID));
        assertFalse(key.isSet(DummyObject.NAME));
        assertTrue(key.isSet(DummyObject.PASSWORD));

        List<String> applied = new ArrayList<>();
        key.apply((field, value) -> applied.add(field.getName()));
        assertEquals(List.of("id", "password"), applied);

        FieldValue[] array = key.toArray();
        assertEquals(2, array.length);
        assertSame(DummyObject.ID, array[0].getField());
        assertSame(DummyObject.PASSWORD, array[1].getField());

        assertEquals(key, builder.proxy(glob));
    }

    private static void check_equalsAndHashCode(Field[] fields) throws Exception {
        FunctionalKeyBuilder builder = builder(fields);
        FunctionalKey k1 = builder.create(glob(1, "a", new byte[]{1, 2}, 1.0));
        FunctionalKey k2 = builder.create(glob(1, "a", new byte[]{1, 2}, 1.0));
        assertEquals(k1, k2);
        assertEquals(k1.hashCode(), k2.hashCode());
        Map<FunctionalKey, String> map = new HashMap<>();
        map.put(k1, "found");
        assertEquals("found", map.get(k2));

        assertNotEquals(k1, builder.create(glob(1, "a", new byte[]{1, 3}, 1.0)));
        assertNotEquals(k1, builder.create(glob(2, "a", new byte[]{1, 2}, 1.0)));

        // same fields, another builder
        FunctionalKey other = builder(fields).create(glob(1, "a", new byte[]{1, 2}, 1.0));
        assertEquals(k1, other);
        assertEquals(k1.hashCode(), other.hashCode());

        // unset differs from set to null
        MutableGlob withNull = glob(1, "a", new byte[]{1, 2}, 1.0);
        withNull.set(DummyObject.NAME, null);
        MutableGlob withUnset = DummyObject.TYPE.instantiate()
                .set(DummyObject.ID, 1).set(DummyObject.PASSWORD, new byte[]{1, 2}).set(DummyObject.VALUE, 1.0);
        assertNotEquals(builder.create(withNull), builder.create(withUnset));
    }

    /**
     * globs-shared writes hashCode() into its off-heap hash files: a 3 or 4 field key must hash as the
     * ManyFieldsMutableKey that used to be built for it did, or files written before are no longer found.
     */
    private static void check_hashIsTheOneOfManyFieldsMutableKey(Field[] fields) throws Exception {
        FunctionalKeyBuilder unrolled = builder(fields);
        FunctionalKeyBuilder many = new ManyFunctionalKeyBuilder(Arrays.copyOf(fields, fields.length));
        for (MutableGlob glob : List.of(
                glob(1, "a", new byte[]{1, 2}, 1.0),
                glob(-5, null, null, null),
                DummyObject.TYPE.instantiate().set(DummyObject.ID, 42),
                DummyObject.TYPE.instantiate())) {
            assertEquals(many.create(glob).hashCode(), unrolled.create(glob).hashCode(), glob.toString());
        }
    }

    private static void check_createCopiesTheValues(Field[] fields) throws Exception {
        MutableFunctionalKey shared = builder(fields).create();
        shared.set(DummyObject.ID, 1);
        FunctionalKey copy = shared.create();
        shared.set(DummyObject.ID, 2);
        assertEquals(1, copy.get(DummyObject.ID));
        assertEquals(2, shared.getShared().get(DummyObject.ID));
    }

    @Test
    void factoryPicksTheUnrolledImplementation() throws Exception {
        both(ThreeFourFieldsMutableKeyTest::check_factoryPicksTheUnrolledImplementation);
    }

    @Test
    void setGetUnsetContains() throws Exception {
        both(ThreeFourFieldsMutableKeyTest::check_setGetUnsetContains);
    }

    @Test
    void createFromGlobAndVisitSetFieldsInOrder() throws Exception {
        both(ThreeFourFieldsMutableKeyTest::check_createFromGlobAndVisitSetFieldsInOrder);
    }

    @Test
    void equalsAndHashCode() throws Exception {
        both(ThreeFourFieldsMutableKeyTest::check_equalsAndHashCode);
    }

    @Test
    void hashIsTheOneOfManyFieldsMutableKey() throws Exception {
        both(ThreeFourFieldsMutableKeyTest::check_hashIsTheOneOfManyFieldsMutableKey);
    }

    @Test
    void createCopiesTheValues() throws Exception {
        both(ThreeFourFieldsMutableKeyTest::check_createCopiesTheValues);
    }
}
