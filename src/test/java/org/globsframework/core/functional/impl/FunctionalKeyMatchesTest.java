package org.globsframework.core.functional.impl;

import org.globsframework.core.functional.FunctionalKey;
import org.globsframework.core.functional.FunctionalKeyBuilder;
import org.globsframework.core.functional.FunctionalKeyBuilderFactory;
import org.globsframework.core.metamodel.DummyObject;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.model.FieldValues;
import org.globsframework.core.model.MutableGlob;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** matches(other) of every key implementation (0 to 5 fields) agrees with the loop it replaces in globs-shared. */
class FunctionalKeyMatchesTest {
    private static final Field[] ALL = {DummyObject.ID, DummyObject.NAME, DummyObject.PASSWORD, DummyObject.VALUE, DummyObject.COUNT};

    private static boolean reference(FunctionalKey key, FieldValues other) {
        for (Field field : key.getBuilder().getFields()) {
            if (!field.valueEqual(key.getValue(field), other.getValue(field))) {
                return false;
            }
        }
        return true;
    }

    private static List<MutableGlob> globs() {
        List<MutableGlob> globs = new ArrayList<>();
        globs.add(DummyObject.TYPE.instantiate());
        globs.add(DummyObject.TYPE.instantiate().set(DummyObject.ID, 1));
        globs.add(DummyObject.TYPE.instantiate().set(DummyObject.ID, 1).set(DummyObject.NAME, "a")
                .set(DummyObject.PASSWORD, new byte[]{1, 2}).set(DummyObject.VALUE, 1.5).set(DummyObject.COUNT, 3));
        globs.add(DummyObject.TYPE.instantiate().set(DummyObject.ID, 1).set(DummyObject.NAME, "a")
                .set(DummyObject.PASSWORD, new byte[]{1, 2}).set(DummyObject.VALUE, 1.5).set(DummyObject.COUNT, 4));
        globs.add(DummyObject.TYPE.instantiate().set(DummyObject.ID, 1).set(DummyObject.NAME, "b")
                .set(DummyObject.PASSWORD, new byte[]{1, 3}).set(DummyObject.VALUE, 1.5).set(DummyObject.COUNT, 3));
        globs.add(DummyObject.TYPE.instantiate().set(DummyObject.ID, 2).set(DummyObject.NAME, null)
                .set(DummyObject.PASSWORD, null).set(DummyObject.VALUE, null).set(DummyObject.COUNT, null));
        return globs;
    }

    @Test
    void agreesWithTheLoopForEveryKeySize() {
        List<MutableGlob> globs = globs();
        for (int size = 0; size <= ALL.length; size++) {
            FunctionalKeyBuilderFactory factory = FunctionalKeyBuilderFactory.create(DummyObject.TYPE);
            for (Field field : Arrays.copyOf(ALL, size)) {
                factory.add(field);
            }
            FunctionalKeyBuilder builder = factory.create();
            for (MutableGlob keySource : globs) {
                FunctionalKey key = builder.create(keySource);
                assertTrue(key.matches(keySource), size + " " + key);
                for (MutableGlob other : globs) {
                    assertEquals(reference(key, other), key.matches(other), size + " " + key + " vs " + other);
                }
            }
        }
    }

    @Test
    void contentOfArraysIsCompared() {
        FunctionalKeyBuilder builder = FunctionalKeyBuilderFactory.create(DummyObject.TYPE)
                .add(DummyObject.ID).add(DummyObject.NAME).add(DummyObject.PASSWORD).create();
        FunctionalKey key = builder.create().set(DummyObject.ID, 1).set(DummyObject.NAME, "a")
                .set(DummyObject.PASSWORD, new byte[]{1, 2}).create();
        MutableGlob glob = DummyObject.TYPE.instantiate().set(DummyObject.ID, 1).set(DummyObject.NAME, "a")
                .set(DummyObject.PASSWORD, new byte[]{1, 2});
        assertTrue(key.matches(glob));
        glob.set(DummyObject.PASSWORD, new byte[]{1, 3});
        assertFalse(key.matches(glob));
    }

    @Test
    void unsetInTheKeyMatchesNullOrUnset() {
        FunctionalKey key = FunctionalKeyBuilderFactory.create(DummyObject.TYPE)
                .add(DummyObject.ID).add(DummyObject.NAME).add(DummyObject.VALUE).create()
                .create().set(DummyObject.ID, 1).create();
        assertTrue(key.matches(DummyObject.TYPE.instantiate().set(DummyObject.ID, 1)));
        assertTrue(key.matches(DummyObject.TYPE.instantiate().set(DummyObject.ID, 1).set(DummyObject.NAME, null)));
        assertFalse(key.matches(DummyObject.TYPE.instantiate().set(DummyObject.ID, 1).set(DummyObject.NAME, "x")));
    }
}
