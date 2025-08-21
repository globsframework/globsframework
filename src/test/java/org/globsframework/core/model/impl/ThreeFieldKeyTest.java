package org.globsframework.core.model.impl;

import org.globsframework.core.metamodel.DummyObjectWithTripleKey;
import org.globsframework.core.model.Key;
import org.globsframework.core.model.KeyBuilder;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ThreeFieldKeyTest {

    private ThreeFieldKey k1a = new ThreeFieldKey(DummyObjectWithTripleKey.ID1, 1,
            DummyObjectWithTripleKey.ID2, 2,
            DummyObjectWithTripleKey.ID3, 3);
    private ThreeFieldKey k1b = new ThreeFieldKey(DummyObjectWithTripleKey.ID2, 2,
            DummyObjectWithTripleKey.ID1, 1,
            DummyObjectWithTripleKey.ID3, 3);
    private ThreeFieldKey k2 = new ThreeFieldKey(DummyObjectWithTripleKey.ID2, 1,
            DummyObjectWithTripleKey.ID1, 2,
            DummyObjectWithTripleKey.ID3, 3);

    @Test
    public void test() throws Exception {
        assertEquals(k1a, k1b);
        assertNotEquals(k1a, k2);
        assertNotEquals(k1b, k2);

        assertEquals(k1a.hashCode(), k1b.hashCode());
        assertTrue(k1a.hashCode() != k2.hashCode());
        assertTrue(k1b.hashCode() != k2.hashCode());
    }

    @Test
    public void testComparingWithAnotherType() throws Exception {
        Key other = KeyBuilder.init(DummyObjectWithTripleKey.TYPE)
                .set(DummyObjectWithTripleKey.ID1, 1)
                .set(DummyObjectWithTripleKey.ID2, 2)
                .set(DummyObjectWithTripleKey.ID3, 3)
                .get();

        assertEquals(k1a, other);
        assertEquals(k1b, other);
        assertFalse(other.equals(k2));
        assertFalse(k2.equals(other));

        Map<ThreeFieldKey, String> map = new HashMap<ThreeFieldKey, String>();
        map.put(k1a, "other");
        map.put(k1b, "k1b");
        map.put(k2, "k2");

        assertEquals("k1b", map.get(k1a));
    }
}
