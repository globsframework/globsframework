package org.globsframework.core.model;

import org.globsframework.core.metamodel.DummyObjectInner;
import org.globsframework.core.metamodel.DummyObjectWithInner;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TGlobArrayTest {

    @Test
    void getTGlobArray() {
        final MutableGlob data = DummyObjectWithInner.TYPE.instantiate();
        data.set(DummyObjectWithInner.VALUES, new Glob[]{DummyObjectInner.create(1.1), DummyObjectInner.create(2.2)});
        final TGlobArray<DummyObjectInner> values = data.getT(DummyObjectWithInner.VALUES);
        assertNotNull(values);
        assertEquals(1.1, values.value()[0].get(DummyObjectInner.VALUE));
    }
}