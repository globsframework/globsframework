package org.globsframework.core.model.utils;

import org.globsframework.core.metamodel.DummyObject;
import org.globsframework.core.metamodel.DummyObject2;
import org.globsframework.core.model.DummyChangeSetListener;
import org.globsframework.core.model.GlobChecker;
import org.globsframework.core.model.GlobRepository;
import org.globsframework.core.model.KeyBuilder;
import org.globsframework.core.model.repository.LocalGlobRepository;
import org.globsframework.core.model.repository.LocalGlobRepositoryBuilder;
import org.globsframework.core.utils.exceptions.InvalidState;
import org.junit.jupiter.api.Test;

import static org.globsframework.core.model.FieldValue.value;
import static org.junit.jupiter.api.Assertions.*;

public class LocalGlobRepositoryTest {
    private GlobChecker checker = new GlobChecker();

    @Test
    public void testStandardCase() throws Exception {
        GlobRepository source = checker.parse(
                "<dummyObject id='0' name='name'/>" +
                        "<dummyObject id='1' name='name' value='1.1'/>" +
                        "<dummyObject id='2' name='name' value='2.2'/>" +
                        "<dummyObject2 id='0'/>");

        LocalGlobRepository local = LocalGlobRepositoryBuilder.init(source).copy(DummyObject.TYPE).get();
        checker.assertEquals(local,
                "<dummyObject id='0' name='name'/>" +
                        "<dummyObject id='1' name='name' value='1.1'/>" +
                        "<dummyObject id='2' name='name' value='2.2'/>");

        local.create(DummyObject.TYPE,
                value(DummyObject.ID, 3),
                value(DummyObject.NAME, "obj3"));

        local.update(KeyBuilder.newKey(DummyObject.TYPE, 1), DummyObject.NAME, "newName");

        local.delete(KeyBuilder.newKey(DummyObject.TYPE, 2));

        checker.assertEquals(source,
                "<dummyObject id='0' name='name'/>" +
                        "<dummyObject id='1' name='name' value='1.1'/>" +
                        "<dummyObject id='2' name='name' value='2.2'/>" +
                        "<dummyObject2 id='0'/>");

        checker.assertChangesEqual(local.getCurrentChanges(),
                "<update type='dummyObject' id='1' name='newName' _name='name'/>" +
                        "<create type='dummyObject' id='3' name='obj3'/>" +
                        "<delete type='dummyObject' id='2' _name='name' _value='2.2'/>");

        local.commitChanges(false);

        checker.assertEquals(source,
                "<dummyObject id='0' name='name'/>" +
                        "<dummyObject id='1' name='newName' value='1.1'/>" +
                        "<dummyObject id='3' name='obj3'/>" +
                        "<dummyObject2 id='0'/>");

        assertTrue(local.getCurrentChanges().isEmpty());
    }

    @Test
    public void testDispose() throws Exception {
        GlobRepository source = checker.parse("<dummyObject id='0' name='name'/>");

        LocalGlobRepository local = LocalGlobRepositoryBuilder.init(source).copy(DummyObject.TYPE).get();
        assertTrue(local.contains(DummyObject.TYPE));

        local.dispose();
        try {
            local.getAll(DummyObject.TYPE);
            fail();
        } catch (InvalidState e) {
        }
    }

    @Test
    public void testIdGeneration() throws Exception {
        GlobRepository source = checker.parse(
                "<dummyObject name='name'/>" +
                        "<dummyObject name='name' value='1.1'/>" +
                        "<dummyObject2 id='0'/>");

        LocalGlobRepository local = LocalGlobRepositoryBuilder.init(source).copy(DummyObject.TYPE).get();
        local.create(DummyObject.TYPE, value(DummyObject.NAME, "new"));
        local.commitChanges(true);

        checker.assertEquals(source,
                "<dummyObject id='100' name='name'/>" +
                        "<dummyObject id='101' name='name' value='1.1'/>" +
                        "<dummyObject id='102' name='new'/>" +
                        "<dummyObject2 id='0'/>");
    }

    @Test
    public void testRollback() throws Exception {
        GlobRepository source = checker.parse(
                "<dummyObject id='0' name='name'/>" +
                        "<dummyObject id='1' name='name' value='1.1'/>" +
                        "<dummyObject id='2' name='name' value='2.2'/>" +
                        "<dummyObject2 id='0'/>" +
                        "<dummyObject2 id='1'/>");

        LocalGlobRepository local = LocalGlobRepositoryBuilder.init(source).copy(DummyObject.TYPE)
                .copy(source.get(KeyBuilder.newKey(DummyObject2.TYPE, 1))).get();
        checker.assertEquals(local,
                "<dummyObject id='0' name='name'/>" +
                        "<dummyObject id='1' name='name' value='1.1'/>" +
                        "<dummyObject id='2' name='name' value='2.2'/>" +
                        "<dummyObject2 id='1'/>");

        local.create(DummyObject.TYPE,
                value(DummyObject.ID, 3),
                value(DummyObject.NAME, "obj3"));

        local.update(KeyBuilder.newKey(DummyObject.TYPE, 1), DummyObject.NAME, "newName");

        local.delete(KeyBuilder.newKey(DummyObject.TYPE, 2));

        local.delete(KeyBuilder.newKey(DummyObject2.TYPE, 1));

        DummyChangeSetListener listener = new DummyChangeSetListener();
        local.addChangeListener(listener);
        local.rollback();
        listener.assertResetListEquals(DummyObject.TYPE, DummyObject2.TYPE);
        checker.assertEquals(local,
                "<dummyObject id='0' name='name'/>" +
                        "<dummyObject id='1' name='name' value='1.1'/>" +
                        "<dummyObject id='2' name='name' value='2.2'/>" +
                        "<dummyObject2 id='1'/>");

        assertFalse(local.containsChanges());
        assertTrue(local.getCurrentChanges().isEmpty());

        local.create(DummyObject.TYPE,
                value(DummyObject.ID, 3),
                value(DummyObject.NAME, "obj3"));

        local.update(KeyBuilder.newKey(DummyObject.TYPE, 1), DummyObject.NAME, "newName");

        local.delete(KeyBuilder.newKey(DummyObject.TYPE, 2));

        assertTrue(local.containsChanges());
        checker.assertChangesEqual(local.getCurrentChanges(),
                "<update type='dummyObject' id='1' name='newName' _name='name'/>" +
                        "<create type='dummyObject' id='3' name='obj3'/>" +
                        "<delete type='dummyObject' id='2' _name='name' _value='2.2'/>");

        local.commitChanges(false);

        checker.assertEquals(source,
                "<dummyObject id='0' name='name'/>" +
                        "<dummyObject id='1' name='newName' value='1.1'/>" +
                        "<dummyObject id='3' name='obj3'/>" +
                        "<dummyObject2 id='0'/>" +
                        "<dummyObject2 id='1'/>");

        assertTrue(local.getCurrentChanges().isEmpty());
    }

    @Test
    public void testRollbackRemoveLocalyCreated() throws Exception {
        GlobRepository source = checker.parse(
                "<dummyObject id='0' name='name'/>" +
                        "<dummyObject id='1' name='name' value='1.1'/>" +
                        "<dummyObject id='2' name='name' value='2.2'/>");

        LocalGlobRepository local = LocalGlobRepositoryBuilder.init(source).copy(DummyObject.TYPE).get();

        local.create(DummyObject2.TYPE,
                value(DummyObject2.ID, 3),
                value(DummyObject2.LABEL, "obj3"));

        local.rollback();
        checker.assertEquals(local,
                "<dummyObject id='0' name='name'/>" +
                        "<dummyObject id='1' name='name' value='1.1'/>" +
                        "<dummyObject id='2' name='name' value='2.2'/>");
    }
}
