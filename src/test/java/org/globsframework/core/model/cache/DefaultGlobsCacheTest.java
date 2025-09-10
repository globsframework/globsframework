package org.globsframework.core.model.cache;

import org.globsframework.core.metamodel.DummyObject;
import org.globsframework.core.metamodel.DummyObjectInner;
import org.globsframework.core.metamodel.DummyObjectWithInner;
import org.globsframework.core.model.MutableGlob;
import org.globsframework.core.model.ReservationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DefaultGlobsCacheTest {

    @Test
    void newGlobReservesAndHasCorrectType() {
        GlobsCache cache = new DefaultGlobsCache(2);
        int id = 101;
        MutableGlob g = cache.newGlob(DummyObject.TYPE, id);
        assertNotNull(g);
        assertEquals(DummyObject.TYPE, g.getType());
        assertTrue(g.isReserved());
        assertTrue(g.isReservedBy(id));
        // cleanup
        cache.release(g, id);
    }

    @Test
    void releaseClearsFieldsAndReusesInstance() {
        GlobsCache cache = new DefaultGlobsCache(4);
        int id = 11;
        MutableGlob parent = cache.newGlob(DummyObjectWithInner.TYPE, id);

        // Populate fields (including nested globs and arrays)
        parent.set(DummyObjectWithInner.ID, 7);
        MutableGlob inner1 = cache.newGlob(DummyObjectInner.TYPE, id);
        inner1.set(DummyObjectInner.DATE, 20240101);
        inner1.set(DummyObjectInner.VALUE, 3.14);
        parent.set(DummyObjectWithInner.VALUE, inner1);

        MutableGlob inner2 = cache.newGlob(DummyObjectInner.TYPE, id);
        inner2.set(DummyObjectInner.DATE, 20240102);
        inner2.set(DummyObjectInner.VALUE, 2.71);
        parent.set(DummyObjectWithInner.VALUES, new MutableGlob[]{inner1, inner2});

        // Also set union fields with a DummyObject
        MutableGlob other = cache.newGlob(DummyObject.TYPE, id);
        other.set(DummyObject.ID, 999);
        parent.set(DummyObjectWithInner.VALUE_UNION, other);
        parent.set(DummyObjectWithInner.VALUES_UNION, new MutableGlob[]{inner2, other});

        // Release parent; should clear all fields and recursively release nested globs
        cache.release(parent, id);

        // Nested instances must not be reserved anymore
        assertFalse(inner1.isReserved());
        assertFalse(inner2.isReserved());
        assertFalse(other.isReserved());

        // Getting a new glob should reuse the cached parent instance (identity) and be reserved again
        MutableGlob reused = cache.newGlob(DummyObjectWithInner.TYPE, id);
        assertSame(parent, reused, "Expected the released instance to be reused from cache");
        assertTrue(reused.isReservedBy(id));

        // All fields should be unset on the reused instance
        assertFalse(reused.isSet(DummyObjectWithInner.ID));
        assertFalse(reused.isSet(DummyObjectWithInner.VALUE));
        assertFalse(reused.isSet(DummyObjectWithInner.VALUES));
        assertFalse(reused.isSet(DummyObjectWithInner.VALUE_UNION));
        assertFalse(reused.isSet(DummyObjectWithInner.VALUES_UNION));

        // cleanup
        cache.release(reused, id);
    }

    @Test
    void respectsMaxCapacity() {
        int capacity = 2;
        GlobsCache cache = new DefaultGlobsCache(capacity);
        int id = 55;

        // Create and release three instances; cache should keep at most 'capacity'
        MutableGlob g1 = cache.newGlob(DummyObject.TYPE, id);
        MutableGlob g2 = cache.newGlob(DummyObject.TYPE, id);
        MutableGlob g3 = cache.newGlob(DummyObject.TYPE, id);
        cache.release(g1, id);
        cache.release(g2, id);
        cache.release(g3, id);

        // Now, the next two requests should give back two of the released instances, third should be a fresh one
        MutableGlob r1 = cache.newGlob(DummyObject.TYPE, id);
        MutableGlob r2 = cache.newGlob(DummyObject.TYPE, id);
        MutableGlob r3 = cache.newGlob(DummyObject.TYPE, id);

        // r1 and r2 should be one of {g1,g2,g3} identities (two of them), r3 should be a different instance
        int reusedCount = 0;
        if (r1 == g1 || r1 == g2 || r1 == g3) reusedCount++;
        if (r2 == g1 || r2 == g2 || r2 == g3) reusedCount++;
        assertEquals(capacity, reusedCount, "Should have reused exactly 'capacity' instances from cache");

        // cleanup
        cache.release(r1, id);
        cache.release(r2, id);
        cache.release(r3, id);
    }

    @Test
    void wrongIdReleaseThrowsAndDoesNotCache() {
        GlobsCache cache = new DefaultGlobsCache(2);
        int id = 77;
        MutableGlob g = cache.newGlob(DummyObjectWithInner.TYPE, id);

        // Add a nested glob so that recursive release is exercised before the main release
        MutableGlob inner = cache.newGlob(DummyObjectInner.TYPE, id);
        g.set(DummyObjectWithInner.VALUE, inner);

        // Releasing with wrong id should throw
        assertThrows(ReservationException.class, () -> cache.release(g, id + 1));

        // The instance should still be reserved by original id
        assertTrue(g.isReservedBy(id));

        // Releasing with correct id should work and cache the instance
        cache.release(g, id);
        MutableGlob reused = cache.newGlob(DummyObjectWithInner.TYPE, id);
        assertSame(g, reused);

        // cleanup
        cache.release(reused, id);
    }

    @Test
    void doubleReleaseIsNoopAfterFirst() {
        GlobsCache cache = new DefaultGlobsCache(1);
        int id = 3;
        MutableGlob g = cache.newGlob(DummyObject.TYPE, id);
        cache.release(g, id);

        // Already released; releasing again should do nothing and not throw
        assertDoesNotThrow(() -> cache.release(g, id));

        // Fetch should reuse the same instance
        MutableGlob reused = cache.newGlob(DummyObject.TYPE, id);
        assertSame(g, reused);

        // cleanup
        cache.release(reused, id);
    }

}
