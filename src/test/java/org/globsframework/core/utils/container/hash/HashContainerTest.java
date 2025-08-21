package org.globsframework.core.utils.container.hash;


import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class HashContainerTest {

    @Test
    public void testName() throws Exception {
        HashContainer<Integer, Integer> container = HashContainer.EMPTY_INSTANCE;
        container = container.put(1, 1);
        assertEquals((int) 1, (int) container.remove(1));
        assertNull(container.get(1));
        container = container.put(1, 2);
        assertEquals((int) 2, (int) container.get(1));
        container = container.put(2, 1);
        assertEquals((int) 2, (int) container.get(1));
        assertEquals((int) 1, (int) container.get(2));
        container = container.put(3, 3);
        container = container.put(4, 4);
        assertEquals((int) 4, (int) container.get(4));
        assertEquals((int) 1, (int) container.get(2));
        container.remove(4);
        assertNull(container.get(4));
    }

    @Test
    public void testMany() throws Exception {
        for (int i = 0; i < 100; i++) {
            HashContainer<Integer, Integer> container = HashContainer.EMPTY_INSTANCE;
            Set<Integer> va = new HashSet<>();
            for (int j = i; j > 0; j--) {
                container = container.put(j, i * j);
            }
            Iterator<Integer> values = container.values();
            for (int j = i; j > 0; j--) {
                assertTrue(values.hasNext());
                va.add(values.next());
            }
            assertFalse(values.hasNext());
            for (int j = i; j > 0; j--) {
                assertTrue(va.contains(i * j));
            }

            final HashContainer<Integer, Integer> finalContainer = container;
            container.forEach(new HashContainer.Functor<Integer, Integer>() {
                public void apply(Integer key, Integer integer) {
                    assertEquals(finalContainer.get(key), integer);
                }
            });

            for (int j = i; j > 0; j--) {
                assertEquals(i * j, (int) container.get(j));
            }
        }
    }

    @Test
    public void testIterator() throws Exception {
        HashContainer<Integer, Integer> container = HashContainer.EMPTY_INSTANCE;
        container = container.put(1, 1);
        check(container, create(Arrays.asList(1), Arrays.asList(1)));
        container = container.put(1, 2);
        check(container, create(Arrays.asList(1), Arrays.asList(2)));
        container = container.put(2, 1);
        check(container, create(Arrays.asList(1, 2), Arrays.asList(2, 1)));
        container = container.put(3, 3);
        container = container.put(4, 4);
        check(container, create(Arrays.asList(1, 2, 3, 4), Arrays.asList(2, 1, 3, 4)));
        container.remove(4);
        check(container, create(Arrays.asList(1, 2, 3), Arrays.asList(2, 1, 3)));
        container = container.put(6, 3);
        container = container.put(7, 2);
        check(container, create(Arrays.asList(1, 2, 3, 6, 7), Arrays.asList(2, 1, 3, 3, 2)));
    }

    @Test
    public void testRemoveInApply() {
        HashContainer<Integer, Integer> container = HashContainer.EMPTY_INSTANCE;
        container = container.put(1, 1);
        container.applyAndRemoveIfTrue((key, integer) -> true);
        assertTrue(container.isEmpty());

        container = container.put(1, 1);
        container = container.put(2, 2);
        container.applyAndRemoveIfTrue((key, integer) -> key == 2);
        assertNull(container.get(2));
        assertNotNull(container.get(1));

    }

    private Map<Integer, Integer> create(List<Integer> keys, List<Integer> values) {
        Map<Integer, Integer> integerIntegerMap = new HashMap<>();
        Iterator<Integer> iterator = values.iterator();
        for (Integer integer : keys) {
            integerIntegerMap.put(integer, iterator.next());
        }
        return integerIntegerMap;
    }

    public void check(HashContainer<Integer, Integer> container, Map<Integer, Integer> values) {
        HashContainer.TwoElementIterator<Integer, Integer> integerIntegerTwoElementIterator = container.entryIterator();
        int i = 0;
        while (integerIntegerTwoElementIterator.next()) {
            assertEquals(values.get(integerIntegerTwoElementIterator.getKey()), integerIntegerTwoElementIterator.getValue());
            i++;
        }
        assertEquals(i, container.size());
    }
}
