package org.globsframework.core.functional.impl;

import org.globsframework.core.functional.FunctionalKey;
import org.globsframework.core.functional.FunctionalKeyBuilder;
import org.globsframework.core.functional.MutableFunctionalKey;
import org.globsframework.core.metamodel.DummyObject;
import org.globsframework.core.metamodel.DummyObject2;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.utils.exceptions.InvalidParameter;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ManyFieldsMutableKeyTest {

    // built directly: the factory answers Three/FourFunctionalKeyBuilder for 3 and 4 fields
    private static FunctionalKeyBuilder builder(Field... fields) {
        return new ManyFunctionalKeyBuilder(fields);
    }

    private static FunctionalKey key(FunctionalKeyBuilder builder, int id, String name, byte[] password) {
        return builder.create()
                .set(DummyObject.ID, id)
                .set(DummyObject.NAME, name)
                .set(DummyObject.PASSWORD, password)
                .create();
    }

    @Test
    void equalKeysWithArrayValueHaveSameHash() {
        FunctionalKeyBuilder builder = builder(DummyObject.ID, DummyObject.NAME, DummyObject.PASSWORD);
        FunctionalKey k1 = key(builder, 1, "a", new byte[]{1, 2, 3});
        FunctionalKey k2 = key(builder, 1, "a", new byte[]{1, 2, 3});
        assertEquals(k1, k2);
        assertEquals(k1.hashCode(), k2.hashCode());

        Map<FunctionalKey, String> map = new HashMap<>();
        map.put(k1, "found");
        assertEquals("found", map.get(k2));

        assertNotEquals(k1, key(builder, 1, "a", new byte[]{1, 2, 4}));
    }

    @Test
    void sameFieldsFromTwoBuildersAreEqual() {
        FunctionalKey k1 = key(builder(DummyObject.ID, DummyObject.NAME, DummyObject.PASSWORD), 1, "a", new byte[]{1});
        FunctionalKey k2 = key(builder(DummyObject.ID, DummyObject.NAME, DummyObject.PASSWORD), 1, "a", new byte[]{1});
        assertEquals(k1, k2);
        assertEquals(k1.hashCode(), k2.hashCode());
    }

    @Test
    void unsetAndNullAreDistinctAndHashIsStable() {
        FunctionalKeyBuilder builder = builder(DummyObject.ID, DummyObject.NAME, DummyObject.PASSWORD);
        MutableFunctionalKey unset = builder.create().set(DummyObject.ID, 1);
        MutableFunctionalKey nulls = builder.create().set(DummyObject.ID, 1)
                .set(DummyObject.NAME, null).set(DummyObject.PASSWORD, (byte[]) null);
        assertNotEquals(unset.create(), nulls.create());
        assertEquals(unset.create(), builder.create().set(DummyObject.ID, 1).create());

        // unset slots hash to a constant, not to NULL_VALUE's identity hash
        int expected = DummyObject.TYPE.getName().hashCode();
        expected = 31 * expected + Integer.valueOf(1).hashCode();
        expected = 31 * expected + 1;
        expected = 31 * expected + 1;
        assertEquals(expected, unset.create().hashCode());
    }

    @Test
    void keysOfDifferentSizeAreNotEqual() {
        FunctionalKey k3 = builder(DummyObject.ID, DummyObject.NAME, DummyObject.VALUE).create()
                .set(DummyObject.ID, 1).set(DummyObject.NAME, "a").create();
        FunctionalKey k4 = builder(DummyObject.ID, DummyObject.NAME, DummyObject.VALUE, DummyObject.COUNT).create()
                .set(DummyObject.ID, 1).set(DummyObject.NAME, "a").create();
        assertNotEquals(k3, k4);
        assertNotEquals(k4, k3);
    }

    @Test
    void fieldOfAnotherTypeIsRefused() {
        ManyFieldsMutableKey key = (ManyFieldsMutableKey) builder(DummyObject.ID, DummyObject.NAME, DummyObject.VALUE).create();
        assertThrows(InvalidParameter.class, () -> key.getValue(DummyObject2.ID));
        assertThrows(InvalidParameter.class, () -> key.isSet(DummyObject2.ID));
        assertThrows(InvalidParameter.class, () -> key.unset(DummyObject2.ID));
    }
}
