package org.globsframework.core.model.utils;

import org.globsframework.core.metamodel.DummyObject;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.Key;
import org.globsframework.core.model.KeyBuilder;
import org.globsframework.core.model.repository.DefaultGlobRepository;
import org.globsframework.core.utils.TestUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class GlobUtilsTest {

    @Test
    public void testDiff() throws Exception {
        Key k1 = KeyBuilder.newKey(DummyObject.TYPE, 1);
        Key k2 = KeyBuilder.newKey(DummyObject.TYPE, 2);
        Key k3 = KeyBuilder.newKey(DummyObject.TYPE, 3);
        Key k4 = KeyBuilder.newKey(DummyObject.TYPE, 4);

        DefaultGlobRepository repository = new DefaultGlobRepository();
        Glob g1 = repository.create(k1);
        Glob g2 = repository.create(k2);
        Glob g3 = repository.create(k3);
        Glob g4 = repository.create(k4);
        checkTwoWay(Arrays.asList(g1, g2, g3), Arrays.asList(g1, g2, g3));
        checkTwoWay(Arrays.asList(g1, g2), Arrays.asList(g1, g2, g3));
        checkTwoWay(Arrays.asList(), Arrays.asList(g1, g2, g3));
        checkTwoWay(Arrays.asList(), Arrays.asList());
        checkTwoWay(Arrays.asList(g1), Arrays.asList(g1, g2, g3));
        checkTwoWay(Arrays.asList(g1), Arrays.asList(g2, g1, g3));
        checkTwoWay(Arrays.asList(g1, g2, g3), Arrays.asList(g1, g2));
        checkTwoWay(Arrays.asList(g1, g3, g2), Arrays.asList(g1, g2, g3));
        checkTwoWay(Arrays.asList(g1), Arrays.asList(g2));
        checkTwoWay(Arrays.asList(g1, g2), Arrays.asList(g3, g4));
    }

    @Test
    public void testDiffWithNull() throws Exception {
        Key k1 = KeyBuilder.newKey(DummyObject.TYPE, 1);
        Key k2 = KeyBuilder.newKey(DummyObject.TYPE, 2);
        Key k3 = KeyBuilder.newKey(DummyObject.TYPE, 3);

        DefaultGlobRepository repository = new DefaultGlobRepository();
        Glob g1 = repository.create(k1);
        Glob g2 = repository.create(k2);
        Glob g3 = repository.create(k3);
        List<Glob> l1 = new ArrayList<>();
        l1.add(null);
        l1.add(g2);
        l1.add(g3);
        List<Glob> l2 = new ArrayList<>();
        l2.add(g2);
        l2.add(null);
        l2.add(g2);
        checkTwoWay(Arrays.asList(g1, g2, g3), l1);
        checkTwoWay(Arrays.asList(g1, g2), l2);
    }

    private void checkTwoWay(Collection<Glob> from, Collection<Glob> to) {
        check(from, to);
        check(to, from);
    }

    private void check(Collection<Glob> from, Collection<Glob> to) {
        final List<Glob> actual = new ArrayList<>(from);
        GlobUtils.diff(from, to, new GlobUtils.DiffFunctor<Glob>() {
            public void add(Glob glob, int index) {
                actual.add(index, glob);
            }

            public void remove(int index) {
                actual.remove(index);
            }

            public void move(int previousIndex, int newIndex) {
                Glob glob = actual.remove(previousIndex);
                actual.add(newIndex, glob);
            }
        });
        TestUtils.assertEquals(to, actual);
    }

    @Test
    public void testCopy() throws Exception {
        Key k1 = KeyBuilder.newKey(DummyObject.TYPE, 1);
        Key k2 = KeyBuilder.newKey(DummyObject.TYPE, 2);

        DefaultGlobRepository repository = new DefaultGlobRepository();
        Glob g1 = repository.create(k1);
        repository.update(g1.getKey(), DummyObject.NAME, "g1");
        repository.update(g1.getKey(), DummyObject.VALUE, 3.14);
        repository.update(g1.getKey(), DummyObject.PRESENT, false);
        Glob g2 = repository.create(k2);
        GlobUtils.copy(repository, g1, g2, DummyObject.NAME, DummyObject.VALUE);
        assertEquals("g1", g2.get(DummyObject.NAME));
        assertEquals(3.14, g2.get(DummyObject.VALUE), 0.0001);
        assertNull(g2.get(DummyObject.PRESENT));
    }
}
