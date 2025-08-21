package org.globsframework.core.utils;

import org.globsframework.core.utils.collections.MapOfMaps;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MapOfMapsTest {

    @Test
    public void test() throws Exception {
        MapOfMaps<Integer, Integer, String> map = new MapOfMaps<Integer, Integer, String>();
        map.put(1, 1, "1");
        map.put(3, 3, "3");
        map.put(3, 4, "4");
        map.put(5, 5, "5");
        assertEquals("1", map.get(1, 1));
        assertEquals("5", map.get(5, 5));

        TestUtils.assertSetEquals(map.values(), "1", "3", "4", "5");
        TestUtils.assertSetEquals(map.iterator(), "1", "3", "4", "5");

        assertTrue(map.containsKey(1, 1));
        assertFalse(map.containsKey(1, 0));
        assertFalse(map.containsKey(0, 1));

        map.remove(3, 3);
        TestUtils.assertSetEquals(map.iterator(), "1", "4", "5");

        map.put(1, 2, "1bis");
        TestUtils.assertSetEquals(map.values(1), "1", "1bis");
        TestUtils.assertSetEquals(map.iterator(), "1", "1bis", "4", "5");

        map.removeAll(1);
        TestUtils.assertSetEquals(map.values(), "4", "5");

        assertFalse(map.isEmpty());
        map.removeAll(3);
        map.removeAll(5);
        assertTrue(map.isEmpty());
    }
}
