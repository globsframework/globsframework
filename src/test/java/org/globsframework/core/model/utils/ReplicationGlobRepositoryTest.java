package org.globsframework.core.model.utils;

import org.globsframework.core.metamodel.DummyObject;
import org.globsframework.core.metamodel.DummyObject2;
import org.globsframework.core.model.GlobChecker;
import org.globsframework.core.model.GlobRepository;
import org.globsframework.core.model.Key;
import org.globsframework.core.model.KeyBuilder;
import org.globsframework.core.model.repository.ReplicationGlobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.globsframework.core.model.FieldValue.value;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ReplicationGlobRepositoryTest {
    private GlobChecker checker = new GlobChecker();
    private GlobRepository source;
    private ReplicationGlobRepository repository;

    @BeforeEach
    public void setUp() throws Exception {
        source = checker.parse(
                "<dummyObject id='0' name='name'/>" +
                        "<dummyObject id='1' name='name' value='1.1'/>" +
                        "<dummyObject id='2' name='name' value='2.2'/>");

        repository = new ReplicationGlobRepository(source, DummyObject2.TYPE);
    }

    @Test
    public void testStandardCase() throws Exception {
        checker.assertEquals(repository,
                "<dummyObject id='0' name='name'/>" +
                        "<dummyObject id='1' name='name' value='1.1'/>" +
                        "<dummyObject id='2' name='name' value='2.2'/>");

        assertNull(repository.find(null));

        repository.create(DummyObject.TYPE,
                value(DummyObject.ID, 3),
                value(DummyObject.NAME, "obj3"));

        repository.update(KeyBuilder.newKey(DummyObject.TYPE, 1), DummyObject.NAME, "newName");

        repository.delete(KeyBuilder.newKey(DummyObject.TYPE, 2));

        repository.create(DummyObject2.TYPE,
                value(DummyObject2.ID, 1),
                value(DummyObject2.LABEL, "label 1"));

        repository.create(DummyObject2.TYPE,
                value(DummyObject2.ID, 2),
                value(DummyObject2.LABEL, "label 2"));


        checker.assertEquals(source,
                "<dummyObject id='0' name='name'/>" +
                        "<dummyObject id='1' name='newName' value='1.1'/>" +
                        "<dummyObject id='3' name='obj3'/>");

        new GlobChecker()
                .assertEquals(repository,
                        "<dummyObject id='0' name='name'/>" +
                                "<dummyObject id='1' name='newName' value='1.1'/>" +
                                "<dummyObject id='3' name='obj3'/>" +
                                "<dummyObject2 id='1' label='label 1'/>" +
                                "<dummyObject2 id='2' label='label 2'/>");

        repository.update(KeyBuilder.newKey(DummyObject2.TYPE, 1), DummyObject2.LABEL, "new label 1");
        repository.delete(KeyBuilder.newKey(DummyObject2.TYPE, 2));
        new GlobChecker()
                .assertEquals(repository,
                        "<dummyObject id='0' name='name'/>" +
                                "<dummyObject id='1' name='newName' value='1.1'/>" +
                                "<dummyObject id='3' name='obj3'/>" +
                                "<dummyObject2 id='1' label='new label 1'/>");

    }

    @Test
    public void testFindWithNullKeyReturnsNull() throws Exception {
        assertNull(repository.find(null));
        assertFalse(repository.contains((Key) null));
    }

}
