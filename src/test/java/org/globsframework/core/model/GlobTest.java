package org.globsframework.core.model;

import org.globsframework.core.metamodel.DummyObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GlobTest {

    @Test
    void testSame() {
        final MutableGlob set = DummyObject.TYPE.instantiate()
                .set(DummyObject.NAME, "name")
                .set(DummyObject.PRESENT, true);

        final MutableGlob duplicate = set.duplicate();
        assertNotSame(set, duplicate);
        assertTrue(set.same(duplicate));
        duplicate.set(DummyObject.NAME, "other");
        assertFalse(set.same(duplicate));
        set.set(DummyObject.NAME, "other");
        assertTrue(set.same(duplicate));
    }
}