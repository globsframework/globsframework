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
}