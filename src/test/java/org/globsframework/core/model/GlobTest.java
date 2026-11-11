package org.globsframework.core.model;

import org.globsframework.core.metamodel.*;
import org.globsframework.core.metamodel.fields.GlobField;
import org.globsframework.core.utils.exceptions.InvalidParameter;
import org.globsframework.core.utils.exceptions.ItemNotFound;
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

    @Test
    void testTypesGet() {
        final MutableGlob dummyObject = DummyObjectWithInner.TYPE.instantiate();
        dummyObject.set(DummyObjectWithInner.VALUE, DummyObjectInner.create(2.2));
        try {
            final TGlob<DummyObjectInner> t = dummyObject.getT(AType.loop);
            fail();
        } catch (InvalidParameter e) {
        }
    }

    @Test
    void testTypedSet() {
        final MutableGlob inner = DummyObjectInner.create(2.2);
        final MutableGlob source = DummyObjectWithInner.TYPE.instantiate()
                .set(DummyObjectWithInner.VALUE, inner);
        final TGlob<DummyObjectInner> t = source.getT(DummyObjectWithInner.VALUE);

        final MutableGlob target = DummyObjectWithInner.TYPE.instantiate()
                .set(DummyObjectWithInner.VALUE, t);
        assertTrue(target.isSet(DummyObjectWithInner.VALUE));
        assertSame(inner, target.get(DummyObjectWithInner.VALUE));
        assertEquals(2.2, target.getT(DummyObjectWithInner.VALUE).notNull().get(DummyObjectInner.VALUE));

        final MutableGlob withNull = DummyObjectWithInner.TYPE.instantiate()
                .set(DummyObjectWithInner.VALUE, TGlob.<DummyObjectInner>of(null));
        assertTrue(withNull.isSet(DummyObjectWithInner.VALUE));
        assertTrue(withNull.isNull(DummyObjectWithInner.VALUE));
        assertTrue(withNull.getT(DummyObjectWithInner.VALUE).isNull());
    }

    @Test
    void testTypedArraySet() {
        final Glob[] inners = {DummyObjectInner.create(1.1), DummyObjectInner.create(2.2)};
        final MutableGlob source = DummyObjectWithInner.TYPE.instantiate()
                .set(DummyObjectWithInner.VALUES, inners);
        final TGlobArray<DummyObjectInner> t = source.getT(DummyObjectWithInner.VALUES);

        final MutableGlob target = DummyObjectWithInner.TYPE.instantiate()
                .set(DummyObjectWithInner.VALUES, t);
        assertTrue(target.isSet(DummyObjectWithInner.VALUES));
        final TGlobArray<DummyObjectInner> read = target.getT(DummyObjectWithInner.VALUES);
        assertEquals(2, read.data().length);
        assertSame(inners[0], read.at(0));
        assertEquals(2.2, read.at(1).get(DummyObjectInner.VALUE));

        final MutableGlob withNull = DummyObjectWithInner.TYPE.instantiate()
                .set(DummyObjectWithInner.VALUES, TGlobArray.<DummyObjectInner>of((Glob[]) null));
        assertTrue(withNull.isSet(DummyObjectWithInner.VALUES));
        assertTrue(withNull.isNull(DummyObjectWithInner.VALUES));
        assertTrue(withNull.getT(DummyObjectWithInner.VALUES).isNull());
    }

    public static class AType {
        public static final GlobType TYPE;

        public static final GlobField<DummyObjectInner> loop;

        static {
            final GlobTypeBuilder typeBuilder = GlobTypeBuilderFactory.create("AType");
            loop = typeBuilder.declareGlobField("loop", () -> AType.TYPE);
            TYPE = typeBuilder.build();
        }
    }
}