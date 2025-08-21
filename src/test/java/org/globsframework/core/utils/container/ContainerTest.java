package org.globsframework.core.utils.container;

import org.globsframework.core.utils.Utils;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

public class ContainerTest {

    @Test
    public void test() throws Exception {
        Container<Integer, Integer> container = Container.EMPTY_INSTANCE;
        container = container.put(1, 3);
        assertEquals(3, container.get(1).intValue());
        assertNull(container.get(3));
        container = container.put(4, 2);
        assertEquals(3, container.get(1).intValue());
        assertEquals(2, container.get(4).intValue());
        assertNull(container.get(3));
        container = container.put(6, 1);
        assertEquals(1, container.get(6).intValue());
        assertNull(container.get(3));
        for (int i = 10; i < 20; i++) {
            container = container.put(i, i * 2);
            assertEquals(i * 2, container.get(i).intValue());
        }
    }

    public void testTwoContainer() throws Exception {
        Container<Integer, Integer> container = Container.EMPTY_INSTANCE;
        container = container.put(2, 22);
        container = container.put(1, 11);
        assertEquals(11, container.first().intValue());
        Iterator<Integer> values = container.values();
        assertEquals(11, values.next().intValue());
        assertEquals(22, values.next().intValue());
        assertFalse(values.hasNext());
        container.remove(1);
        assertEquals(22, container.first().intValue());
        values = container.values();
        values.next();
        assertFalse(values.hasNext());
        container.put(1, 44);
        assertEquals(44, container.first().intValue());
        container.put(2, 66);
        assertEquals(44, container.first().intValue());
        values = container.values();
        assertEquals(44, values.next().intValue());
        assertEquals(66, values.next().intValue());
        container.remove(2);
        container.remove(1);
        assertFalse(values.hasNext());
    }

    public void test2() throws Exception {
        Container<Integer, Double> container = Container.EMPTY_INSTANCE;
        container = container.put(1, 3.1);
        container = container.put(2, 4.1);
        container = container.put(0, 2.1);
        assertEquals(3.1, container.get(1));
        assertEquals(4.1, container.get(2));
        assertEquals(2.1, container.get(0));
        container.remove(0);
        container.remove(1);
        container.remove(2);
        container = container.put(1, 3.1);
        container = container.put(1, 5.1);
        container = container.put(1, 6.1);
        container = container.put(0, 2.1);
        container = container.put(2, 5.1);
        container = container.put(2, 4.1);
        assertEquals(6.1, container.get(1));
        assertEquals(4.1, container.get(2));
        assertEquals(2.1, container.get(0));
        assertEquals(3, container.size());

        container = container.put(null, 10.);
        assertEquals(10., container.get(null));
        container.put(null, 11.);
        assertEquals(11., container.get(null));
        assertEquals(4, container.size());
    }

    public void testTreeComparator() throws Exception {
        TreeMap treeMap = new TreeMap(new Utils.NullAwareComparator());
        treeMap.put(2, 3);
        treeMap.put(null, 4);
        treeMap.put(4, 2);
        assertNull(treeMap.firstKey());
    }

    public void testAddNullValue() throws Exception {
        Container<Integer, Integer> container = Container.EMPTY_INSTANCE;

        for (int i = 0; i < 1000; i++) {
            if (i % 10 == 0) {
                container = container.put(null, 4);
                assertEquals(Integer.valueOf(4), container.get(null));
            } else {
                container = container.put(i, i);
            }
        }

        for (int i = 0; i < 1000; i++) {
            if (i % 10 == 0) {
                assertEquals(Integer.valueOf(4), container.get(null));
            } else {
                assertEquals(Integer.valueOf(i), container.get(i));
            }
        }

    }

    public void testAddRemove() throws Exception {
        Container<Integer, Integer> container = Container.EMPTY_INSTANCE;
        for (int i = 0; i < 15; i++) {
            container = container.put(i, i * 2);
            assertEquals(i + 1, container.size());
            check(container, i);
        }
    }

    public void testSameTwice() throws Exception {
    }

    private void check(Container<Integer, Integer> container, int max) {
        checkRange(container, 0, max);
        int i = 0;
        for (Iterator<Integer> iterator = container.values(); iterator.hasNext(); ) {
            Integer val = iterator.next();
            assertNotNull(val);
            assertEquals(i * 2, val.intValue(), container.getClass().getName());
            i++;
        }

        i = 0;
        for (Iterator<Integer> iterator = container.values(); iterator.hasNext(); ) {
            Integer value = iterator.next();
            iterator.remove();
            i++;
            checkRange(container, i, max);
            assertEquals(max - i + 1, container.size());
        }

        for (int j = 0; j <= max; j++) {
            container = container.put(j, j * 2);
            checkRange(container, 0, j);
            assertEquals(j + 1, container.size());
        }

        checkRange(container, 0, max);

        for (int j = 0; j <= max; j++) {
            container.remove(j);
            checkRange(container, j + 1, max);
            assertEquals(max - j, container.size());
        }
        for (int j = 0; j <= max; j++) {
            container = container.put(j, j * 2);
            checkRange(container, 0, j);
            assertEquals(j + 1, container.size());
        }
    }

    private void checkRange(Container<Integer, Integer> container, int start, int max) {
        for (int i = start; i <= max; i++) {
            assertNotNull(container.get(i), container.getClass().getName());
            assertEquals(i * 2, container.get(i).intValue(), container.getClass().getName());
        }
    }
}
