package org.globsframework.core.model.impl;

import org.globsframework.core.metamodel.DummyObjectWithQuadrupleKey;
import org.globsframework.core.model.Key;
import org.globsframework.core.model.KeyBuilder;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class FourFieldKeyTest {

    private FourFieldKey k1a = new FourFieldKey(DummyObjectWithQuadrupleKey.ID1, 1,
            DummyObjectWithQuadrupleKey.ID2, 2,
            DummyObjectWithQuadrupleKey.ID3, 3,
            DummyObjectWithQuadrupleKey.ID4, 4);
    private FourFieldKey k1b = new FourFieldKey(DummyObjectWithQuadrupleKey.ID2, 2,
            DummyObjectWithQuadrupleKey.ID1, 1,
            DummyObjectWithQuadrupleKey.ID4, 4,
            DummyObjectWithQuadrupleKey.ID3, 3);
    private FourFieldKey k2 = new FourFieldKey(DummyObjectWithQuadrupleKey.ID2, 4,
            DummyObjectWithQuadrupleKey.ID1, 1,
            DummyObjectWithQuadrupleKey.ID4, 2,
            DummyObjectWithQuadrupleKey.ID3, 3);

    @Test
    public void test() throws Exception {
        assertEquals(k1a, k1b);
        assertNotEquals(k1a, k2);
        assertNotEquals(k1b, k2);

        assertEquals(k1a.hashCode(), k1b.hashCode());
        assertTrue(k1a.hashCode() != k2.hashCode());
        assertTrue(k1b.hashCode() != k2.hashCode());

        Map<FourFieldKey, String> map = new HashMap<FourFieldKey, String>();
        map.put(k1a, "k1a");
        map.put(k1b, "k1b");
        map.put(k2, "k2");

        assertTrue(map.containsKey(k1a));
        assertTrue(map.containsKey(k1b));
        assertTrue(map.containsKey(k2));

        assertEquals("k1b", map.get(k1a));
        assertEquals("k1b", map.get(k1b));
        assertEquals("k2", map.get(k2));
    }

    @Test
    public void testComparingWithAnotherType() throws Exception {
        Key other = KeyBuilder.init(DummyObjectWithQuadrupleKey.TYPE)
                .set(DummyObjectWithQuadrupleKey.ID1, 1)
                .set(DummyObjectWithQuadrupleKey.ID2, 2)
                .set(DummyObjectWithQuadrupleKey.ID3, 3)
                .set(DummyObjectWithQuadrupleKey.ID4, 4)
                .get();

        assertEquals(k1a, other);
        assertEquals(k1b, other);
        assertFalse(other.equals(k2));
        assertFalse(k2.equals(other));

        Map<FourFieldKey, String> map = new HashMap<FourFieldKey, String>();
        map.put(k1a, "other");
        map.put(k1b, "k1b");
        map.put(k2, "k2");
    }
}
