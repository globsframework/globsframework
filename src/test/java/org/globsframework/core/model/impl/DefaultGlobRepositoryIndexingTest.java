package org.globsframework.core.model.impl;

import org.globsframework.core.model.*;
import org.globsframework.core.metamodel.DummyObjectIndex;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.model.repository.LocalGlobRepository;
import org.globsframework.core.model.repository.LocalGlobRepositoryBuilder;
import org.globsframework.core.model.utils.GlobFunctor;
import org.globsframework.core.utils.TestUtils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.globsframework.core.model.FieldValue.value;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DefaultGlobRepositoryIndexingTest extends DefaultGlobRepositoryTestCase {

    @Test
    public void testUniqueIndex() throws Exception {
        init("<dummyObjectIndex id='1' uniqueName='obj1' name='a'/>" +
                "<dummyObjectIndex id='2' uniqueName='obj3' name='b'/>");
        assertTrue(repository.findByIndex(DummyObjectIndex.UNIQUE_NAME_INDEX, "obj2").isEmpty());
        assertEquals(1, findIDByNameIndex("obj1"));
        assertEquals(2, findIDByNameIndex("obj3"));
        Glob obj1 = findGlobByNameIndex("obj1");
        Glob obj3 = findGlobByNameIndex("obj3");
        repository.update(obj1.getKey(), DummyObjectIndex.UNIQUE_NAME, "obj3");
        repository.update(obj3.getKey(), DummyObjectIndex.UNIQUE_NAME, "obj1");
        assertEquals(2, findIDByNameIndex("obj1"));
        assertEquals(1, findIDByNameIndex("obj3"));
        repository.update(obj3.getKey(), DummyObjectIndex.UNIQUE_NAME, "obj4");
        assertEquals(2, findIDByNameIndex("obj4"));
        repository.delete(obj3);
        assertTrue(repository.findByIndex(DummyObjectIndex.UNIQUE_NAME_INDEX, "obj4").isEmpty());

        repository.create(DummyObjectIndex.TYPE,
                value(DummyObjectIndex.ID, 3),
                value(DummyObjectIndex.UNIQUE_NAME, "obj5"));
        assertEquals(3, findIDByNameIndex("obj5"));
    }

    @Test
    public void testNotUniqueIndex() throws Exception {
        init("<dummyObjectIndex id='1' uniqueName='obj1' date = '20010101' name='a'/>" +
                "<dummyObjectIndex id='2' uniqueName='obj3' date = '20010101' name='b'/>");
        assertTrue(repository.findByIndex(DummyObjectIndex.DATE_INDEX, 20020101).isEmpty());
        TestUtils.assertSetEquals(Arrays.asList(1, 2), findIDByDateIndex(20010101));
        Glob obj1 = findGlobByNameIndex("obj1");
        Glob obj3 = findGlobByNameIndex("obj3");
        repository.update(obj1.getKey(), DummyObjectIndex.DATE, 20030101);
        assertEquals(2, findIDByDateIndex(20010101).get(0).intValue());
        assertEquals(1, findIDByDateIndex(20030101).get(0).intValue());
        repository.delete(obj3);
        assertTrue(repository.findByIndex(DummyObjectIndex.DATE_INDEX, 20010101).isEmpty());

        repository.create(DummyObjectIndex.TYPE,
                value(DummyObjectIndex.ID, 3),
                value(DummyObjectIndex.NAME, "obj5"),
                value(DummyObjectIndex.DATE, 20040101));
        assertEquals(Arrays.asList(3), findIDByDateIndex(20040101));
    }

    @Test
    public void testMultifieldUniqueIndex() throws Exception {
        init("<dummyObjectIndex id='1' value1='2' value2='3' name='a' uniqueName='a'/>" +
                "<dummyObjectIndex id='2' value1='2' value2='3' name='b' uniqueName='b'/>" +
                "<dummyObjectIndex id='3' value1='2' value2='4' name='a' uniqueName='c'/>" +
                "<dummyObjectIndex id='4' value1='2' value2='5' name='a' uniqueName='d'/>" +
                "<dummyObjectIndex id='5' value1='3' value2='3' name='aaa' uniqueName='e'/>");
        checkId(1).of(2, 3, "a");
        checkId(1, 2).of(2, 3);
        checkId().of(2, 3, "c");
        checkId(3).of(2, 4, "a");
        checkId(1, 2, 3, 4).of(2);
        change(1, 2, 3, "c");
        checkId(1).of(2, 3, "c");
        change(2, 3, 3, "b");
        checkId(2, 5).of(3, 3);
        checkId(1, 3, 4).of(2);
        create(6, 2, 3, "d");
        checkId(1, 3, 4, 6).of(2);
        delete(1);
        checkId(3, 4, 6).of(2);
        change(3, 3, 3, "e");
        checkId(2, 3, 5).of(3, 3);
        checkId(4).of(2, 5, "a");
    }

    @Test
    public void testUniqueIndexWithDeleteCreate() throws Exception {
        init("<dummyObjectIndex id='3' uniqueName='obj1' name='a'/>" +
                "<dummyObjectIndex id='4' uniqueName='obj3' name='b'/>");
        Glob glob3 = repository.get(KeyBuilder.newKey(DummyObjectIndex.TYPE, 4));
        Glob glob4 = repository.get(KeyBuilder.newKey(DummyObjectIndex.TYPE, 3));
        LocalGlobRepositoryBuilder builder = LocalGlobRepositoryBuilder.init(repository);
        builder.copy(DummyObjectIndex.TYPE);
        LocalGlobRepository localGlobRepository = builder.get();
        localGlobRepository.update(glob4.getKey(), DummyObjectIndex.NAME, "obj4");
        localGlobRepository.create(KeyBuilder.newKey(DummyObjectIndex.TYPE, 5),
                FieldValue.value(DummyObjectIndex.NAME, "obj1"), FieldValue.value(DummyObjectIndex.UNIQUE_NAME, "a"));
        localGlobRepository.update(glob3.getKey(), DummyObjectIndex.NAME, "obj2");
        localGlobRepository.create(KeyBuilder.newKey(DummyObjectIndex.TYPE, 1),
                FieldValue.value(DummyObjectIndex.NAME, "obj3"));

        localGlobRepository.commitChanges(true);
    }

    @Test
    public void testMultiFieldNotUniqueIndex() throws Exception {
        init("<dummyObjectIndex id='1' value1='2' value2='3' name='a' uniqueName='a'/>" +
                "<dummyObjectIndex id='2' value1='2' value2='3' name='b' uniqueName='b'/>" +
                "<dummyObjectIndex id='3' value1='2' value2='4' name='a' uniqueName='c'/>" +
                "<dummyObjectIndex id='4' value1='2' value2='5' name='a' uniqueName='d'/>" +
                "<dummyObjectIndex id='5' value1='3' value2='3' name='aaa' uniqueName='e'/>");
        checkIdUnique(1, 2).of(2, 3);
        checkIdUnique(1, 2, 3, 4).of(2);
        change(2, 2, 4, "b");
        checkIdUnique(1).of(2, 3);
        checkIdUnique(2, 3).of(2, 4);
        checkIdUnique(1, 2, 3, 4).of(2);
        change(5, 2, 1, "b");
        checkIdUnique(1, 2, 3, 4, 5).of(2);
        checkIdUnique(5).of(2, 1);
    }


    @Test
    public void testVisitWithIndex() throws Exception {
        init("<dummyObjectIndex id='1' value1='2' value2='3' name='a' uniqueName='a'/>" +
                "<dummyObjectIndex id='2' value1='2' value2='3' name='b'  uniqueName='b'/>" +
                "<dummyObjectIndex id='3' value1='2' value2='4' name='a'  uniqueName='c'/>" +
                "<dummyObjectIndex id='4' value1='2' value2='5' name='a'  uniqueName='d'/>" +
                "<dummyObjectIndex id='5' value1='3' value2='3' name='aaa'  uniqueName='e'/>");
        LocalGlobRepository localGlobRepository = LocalGlobRepositoryBuilder
                .init(repository).copy(DummyObjectIndex.TYPE).get();
        localGlobRepository.startChangeSet();
        localGlobRepository.deleteGlobs(localGlobRepository.findByIndex(DummyObjectIndex.UNIQUE_NAME_INDEX, "e"));
        localGlobRepository.deleteGlobs(localGlobRepository.findByIndex(DummyObjectIndex.UNIQUE_NAME_INDEX, "b"));
        Key a = localGlobRepository.findByIndex(DummyObjectIndex.UNIQUE_NAME_INDEX, "a").get(0).getKey();
        localGlobRepository.update(a,
                FieldValue.value(DummyObjectIndex.UNIQUE_NAME, "b"));
        localGlobRepository.create(DummyObjectIndex.TYPE, FieldValue.value(DummyObjectIndex.UNIQUE_NAME, "a"),
                FieldValue.value(DummyObjectIndex.NAME, "q"));
        localGlobRepository.create(DummyObjectIndex.TYPE, FieldValue.value(DummyObjectIndex.UNIQUE_NAME, "e"));
        localGlobRepository.completeChangeSet();
        localGlobRepository.commitChanges(true);
    }


    private void create(int id, int value1, int value2, String name) {
        repository.create(DummyObjectIndex.TYPE,
                value(DummyObjectIndex.ID, id),
                value(DummyObjectIndex.VALUE_1, value1),
                value(DummyObjectIndex.VALUE_2, value2),
                value(DummyObjectIndex.NAME, name));
    }

    private void delete(int id) {
        repository.delete(KeyBuilder.newKey(DummyObjectIndex.TYPE, id));
    }

    private void change(int id, int value1, int value2, String name) {
        Key key = KeyBuilder.newKey(DummyObjectIndex.TYPE, id);
        repository.startChangeSet();
        update(key, DummyObjectIndex.VALUE_2, value2);
        update(key, DummyObjectIndex.VALUE_1, value1);
        update(key, DummyObjectIndex.NAME, name);
        repository.completeChangeSet();
    }

    private <T> void update(Key key, Field field, T value) {
        T oldValue = (T) repository.get(key).getValue(field);
        if (oldValue != value) {
            repository.update(key, field, value);
        }
    }

    private class CheckValues {
        private List<Integer> ids;

        public CheckValues(int[] ids) {
            this.ids = new ArrayList<Integer>();
            for (int id : ids) {
                this.ids.add(id);
            }
        }

        public void of(int value1, int value2) {
            GlobRepository.MultiFieldIndexed result =
                    repository
                            .findByIndex(DummyObjectIndex.VALUES_INDEX, DummyObjectIndex.VALUE_1, value1)
                            .findByIndex(DummyObjectIndex.VALUE_2, value2);
            checkIds(result);
        }

        public void of(int value1) {
            GlobRepository.MultiFieldIndexed result =
                    repository.findByIndex(DummyObjectIndex.VALUES_INDEX, DummyObjectIndex.VALUE_1, value1);
            checkIds(result);
        }

        private void checkIds(GlobRepository.MultiFieldIndexed result) {
            List<Glob> globs = result.getGlobs();
            assertEquals(ids.size(), globs.size());
            TestUtils.assertSetEquals(ids, globs.stream()
                    .map(DummyObjectIndex.ID)
                    .collect(Collectors.toSet()));
        }
    }

    private class CheckValueAndName {
        private List<Integer> ids;

        public CheckValueAndName(int[] ids) {
            this.ids = new ArrayList<Integer>();
            for (int id : ids) {
                this.ids.add(id);
            }
        }

        public void of(int value1, int value2, String name) {
            GlobRepository.MultiFieldIndexed result =
                    repository.findByIndex(DummyObjectIndex.VALUES_AND_NAME_INDEX, DummyObjectIndex.VALUE_1, value1)
                            .findByIndex(DummyObjectIndex.VALUE_2, value2).findByIndex(DummyObjectIndex.NAME, name);
            checkIds(result);

        }

        public void of(int value1, int value2) {
            GlobRepository.MultiFieldIndexed result =
                    repository.findByIndex(DummyObjectIndex.VALUES_AND_NAME_INDEX, DummyObjectIndex.VALUE_1, value1)
                            .findByIndex(DummyObjectIndex.VALUE_2, value2);
            checkIds(result);
        }

        public void of(int value1) {
            GlobRepository.MultiFieldIndexed result =
                    repository.findByIndex(DummyObjectIndex.VALUES_AND_NAME_INDEX, DummyObjectIndex.VALUE_1, value1);
            checkIds(result);

        }

        private void checkIds(GlobRepository.MultiFieldIndexed result) {
            Collection<Glob> globs = result.getGlobs();
            assertEquals(ids.size(), globs.size());
            TestUtils.assertSetEquals(ids, globs.stream().map(DummyObjectIndex.ID).collect(Collectors.toSet()));
            final List<Integer> actual = new ArrayList<Integer>();
            result.saveApply(new GlobFunctor() {
                public void run(Glob glob, GlobRepository repository) throws Exception {
                    actual.add(glob.get(DummyObjectIndex.ID));
                }
            }, repository);
            TestUtils.assertSetEquals(ids, actual);
        }
    }

    private CheckValueAndName checkId(int... id) {
        return new CheckValueAndName(id);
    }

    private CheckValues checkIdUnique(int... id) {
        return new CheckValues(id);
    }

    private int findIDByNameIndex(String value) {
        return findGlobByNameIndex(value).get(DummyObjectIndex.ID);
    }

    private Glob findGlobByNameIndex(String value) {
        return repository.findByIndex(DummyObjectIndex.UNIQUE_NAME_INDEX, value).get(0);
    }

    private List<Integer> findIDByDateIndex(int value) {
        List<Integer> val = new ArrayList<>();
        Collection<Glob> globByDateIndex = findGlobByDateIndex(value);
        for (Glob glob : globByDateIndex) {
            val.add(glob.get(DummyObjectIndex.ID));
        }
        return val;
    }

    private Collection<Glob> findGlobByDateIndex(int value) {
        return repository.findByIndex(DummyObjectIndex.DATE_INDEX, value);
    }

}
