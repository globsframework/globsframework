package org.globsframework.core.model;

import org.globsframework.core.metamodel.DummyObjectInner;
import org.globsframework.core.metamodel.DummyObjectWithInner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MutableGlobTest {
    @Test
    void testMutableAcces() {
        Glob test = DummyObjectWithInner.TYPE.instantiate()
                .set(DummyObjectWithInner.VALUES, new Glob[]{DummyObjectInner.TYPE.instantiate()
                        .set(DummyObjectInner.VALUE, 3.14)});
        final MutableGlob[] mutable = ((MutableGlob) test).getMutable(DummyObjectWithInner.VALUES);
        mutable[0].set(DummyObjectInner.VALUE, 6.28);
        Assertions.assertEquals(test.get(DummyObjectWithInner.VALUES)[0].get(DummyObjectInner.VALUE), 6.28);

        final MutableGlob cloned = test.duplicate();
        final MutableGlob[] clonedSub = cloned.getMutable(DummyObjectWithInner.VALUES);
        clonedSub[0].set(DummyObjectInner.VALUE, 32);

        Assertions.assertEquals(cloned.get(DummyObjectWithInner.VALUES)[0].get(DummyObjectInner.VALUE), 32);

        Assertions.assertEquals(test.get(DummyObjectWithInner.VALUES)[0].get(DummyObjectInner.VALUE), 6.28);
    }

    @Test
    void addOnMutable() {
        final MutableGlob instantiate = DummyObjectWithInner.TYPE.instantiate();
        final MutableGlob d1 = DummyObjectInner.create(2.2);
        final MutableGlob d2 = DummyObjectInner.create(3.3);
        instantiate.add(DummyObjectWithInner.VALUES, d1, d2);
        final Glob[] globs = instantiate.get(DummyObjectWithInner.VALUES);
        Assertions.assertEquals(2, globs.length);
        Assertions.assertSame(d1, globs[0]);
        Assertions.assertSame(d2, globs[1]);
    }

    @Test
    void addOnString() {
        final MutableGlob instantiate = DummyObjectWithInner.TYPE.instantiate();
        instantiate.add(DummyObjectWithInner.STRS, "2");
        instantiate.add(DummyObjectWithInner.STRS, "1", "3");
        final String[] data = instantiate.get(DummyObjectWithInner.STRS);
        Assertions.assertSame(3, data.length);
        Assertions.assertEquals("2", data[0]);
        Assertions.assertEquals("1", data[1]);
        Assertions.assertEquals("3", data[2]);
    }
}