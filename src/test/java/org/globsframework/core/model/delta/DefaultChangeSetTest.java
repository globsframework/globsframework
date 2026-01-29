package org.globsframework.core.model.delta;

import org.globsframework.core.metamodel.*;
import org.globsframework.core.model.*;
import org.globsframework.core.model.utils.DefaultChangeSetVisitor;
import org.globsframework.core.utils.TestUtils;
import org.globsframework.core.utils.exceptions.InvalidState;
import org.globsframework.core.xml.XmlChangeSetParser;
import org.globsframework.core.xml.XmlChangeSetWriter;
import org.globsframework.core.xml.tests.XmlTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;

import static org.globsframework.core.model.KeyBuilder.newKey;
import static org.junit.jupiter.api.Assertions.*;

public class DefaultChangeSetTest {

    private GlobChecker checker = new GlobChecker();
    private Key key1;
    private DefaultChangeSet changeSet = new DefaultChangeSet();
    private DefaultChangeSet newChangeSet = new DefaultChangeSet();
    private FieldValues creationValues;

    @BeforeEach
    public void setUp() throws Exception {
        key1 = newKey(DummyObject.TYPE, 1);
        creationValues =
                FieldValuesBuilder
                        .init(DummyObject.ID, 1)
                        .set(DummyObject.NAME, "name1")
                        .set(DummyObject.PRESENT, true)
                        .get();
    }

    @Test
    public void testSequenceStartingWithACreation() throws Exception {
        changeSet.processCreation(DummyObject.TYPE, creationValues);
        checker.assertChangesEqual(changeSet,
                "<create type='dummyObject' id='1' name='name1' present='true'/>");
        changeSet.processUpdate(key1, DummyObject.VALUE, 1.1, null);
        checker.assertChangesEqual(changeSet,
                "<create type='dummyObject' id='1' name='name1' present='true' value='1.1'/>");
        changeSet.processDeletion(key1, FieldValues.EMPTY);
        checker.assertChangesEqual(changeSet, "");
    }

    @Test
    public void testChangesAnalysisForEmptyChangeSet() throws Exception {
        assertFalse(changeSet.containsChanges(DummyObject.TYPE));
        assertFalse(changeSet.containsCreationsOrDeletions(DummyObject.TYPE));
        assertFalse(changeSet.containsUpdates(DummyObject.NAME));
        assertEquals(0, changeSet.size());
    }

    @Test
    public void testChangesAnalysisForCreation() throws Exception {
        assertFalse(changeSet.containsChanges(key1));

        changeSet.processCreation(DummyObject.TYPE, creationValues);
        assertTrue(changeSet.isCreated(key1));
        assertFalse(changeSet.isDeleted(key1));
        assertTrue(changeSet.containsChanges(key1));
        assertTrue(changeSet.containsChanges(key1, DummyObject.ID));
        assertTrue(changeSet.containsChanges(DummyObject.TYPE));
        assertTrue(changeSet.containsCreationsOrDeletions(DummyObject.TYPE));
        assertFalse(changeSet.containsUpdates(DummyObject.NAME));
        assertFalse(changeSet.containsChanges(DummyObject2.TYPE));
        assertEquals(1, changeSet.size());

        TestUtils.assertEquals(changeSet.getCreated(DummyObject.TYPE), key1);
        assertTrue(changeSet.getUpdated(DummyObject.TYPE).isEmpty());
        assertTrue(changeSet.getUpdated(DummyObject.NAME).isEmpty());
        assertTrue(changeSet.getDeleted(DummyObject.TYPE).isEmpty());
        assertTrue(changeSet.getCreated(DummyObject2.TYPE).isEmpty());

        TestUtils.assertEquals(changeSet.getChangedTypes(), DummyObject.TYPE);
    }

    @Test
    public void testChangeAnalysisForUpdate() throws Exception {
        assertFalse(changeSet.containsChanges(key1));

        changeSet.processUpdate(key1, DummyObject.VALUE, 1.1, null);
        assertTrue(changeSet.containsChanges(key1));
        assertTrue(changeSet.containsChanges(key1, DummyObject.VALUE));
        assertFalse(changeSet.containsChanges(key1, DummyObject.NAME));
        TestUtils.assertEquals(changeSet.getUpdated(DummyObject.TYPE), key1);
        assertTrue(changeSet.containsChanges(DummyObject.TYPE));
        assertFalse(changeSet.containsCreationsOrDeletions(DummyObject.TYPE));
        assertFalse(changeSet.isCreated(key1));
        assertFalse(changeSet.isDeleted(key1));
        assertTrue(changeSet.containsUpdates(DummyObject.VALUE));
        assertFalse(changeSet.containsUpdates(DummyObject.NAME));
        assertFalse(changeSet.containsChanges(DummyObject2.TYPE));
        assertEquals(1, changeSet.size());

        TestUtils.assertEquals(changeSet.getUpdated(DummyObject.TYPE), key1);
        TestUtils.assertEquals(changeSet.getUpdated(DummyObject.VALUE), key1);
        assertTrue(changeSet.getCreated(DummyObject.TYPE).isEmpty());
        assertTrue(changeSet.getDeleted(DummyObject.TYPE).isEmpty());
        assertTrue(changeSet.getUpdated(DummyObject2.TYPE).isEmpty());

        TestUtils.assertEquals(changeSet.getUpdated(DummyObject.VALUE), key1);
        TestUtils.assertEmpty(changeSet.getUpdated(DummyObject.NAME));

        TestUtils.assertEquals(changeSet.getChangedTypes(), DummyObject.TYPE);
    }

    @Test
    public void testUpdatedFieldsDoNoContainKeys() throws Exception {
        changeSet.processUpdate(key1, DummyObject.VALUE, 1.1, null);
        changeSet.accept(DummyObject.TYPE, new DefaultChangeSetVisitor() {
            public void visitUpdate(Key key, FieldsValueWithPreviousScanner values) throws Exception {
                assertFalse(TestUtils.contains(values, DummyObject.ID));
            }
        });
    }

    @Test
    public void testChangesAnalysisForDeletion() throws Exception {
        assertFalse(changeSet.containsChanges(key1));

        changeSet.processDeletion(key1, FieldValues.EMPTY);
        assertTrue(changeSet.containsChanges(key1));
        assertFalse(changeSet.isCreated(key1));
        assertTrue(changeSet.isDeleted(key1));
        assertTrue(changeSet.containsChanges(DummyObject.TYPE));
        assertTrue(changeSet.containsChanges(key1, DummyObject.NAME));
        assertTrue(changeSet.containsCreationsOrDeletions(DummyObject.TYPE));
        assertFalse(changeSet.containsUpdates(DummyObject.NAME));
        assertEquals(1, changeSet.size());

        TestUtils.assertEquals(changeSet.getDeleted(DummyObject.TYPE), key1);
        assertTrue(changeSet.getCreated(DummyObject.TYPE).isEmpty());
        assertTrue(changeSet.getUpdated(DummyObject.TYPE).isEmpty());
        assertTrue(changeSet.getUpdated(DummyObject2.TYPE).isEmpty());

        TestUtils.assertEquals(changeSet.getChangedTypes(), DummyObject.TYPE);
    }

    @Test
    public void testDoubleCreationError() throws Exception {
        changeSet.processCreation(DummyObject.TYPE, creationValues);
        try {
            changeSet.processCreation(DummyObject.TYPE, creationValues);
            fail();
        } catch (InvalidState e) {
            assertEquals("Object dummyObject[id=1] already exists", e.getMessage());
        }
    }

    @Test
    public void testSequenceStartingWithAnUpdate() throws Exception {
        changeSet.processUpdate(key1, DummyObject.VALUE, 1.1, null);
        checker.assertChangesEqual(changeSet,
                "<update type='dummyObject' id='1' value='1.1' _value='(null)'/>");

        changeSet.processDeletion(key1, FieldValuesBuilder.createEmpty(DummyObject.TYPE, false));
        checker.assertChangesEqual(changeSet,
                "<delete type='dummyObject' id='1'/>");
        assertEquals(1, changeSet.size());
    }

    @Test
    public void testUpdateToNull() throws Exception {
        changeSet.processCreation(DummyObject.TYPE, creationValues);
        changeSet.processUpdate(key1, DummyObject.NAME, null, null);
        checker.assertChangesEqual(changeSet,
                "<create type='dummyObject' id='1' present='true'/>");
    }

    @Test
    public void testUpdateStateErrorTransitions() throws Exception {
        changeSet.processUpdate(key1, DummyObject.VALUE, 1.1, null);
        try {
            changeSet.processCreation(DummyObject.TYPE, creationValues);
            fail();
        } catch (InvalidState e) {
            assertEquals("Object dummyObject[id=1] already exists", e.getMessage());
        }
    }

    @Test
    public void testSequenceStartingWithADeletion() throws Exception {
        changeSet.processDeletion(key1, FieldValuesBuilder.createEmpty(DummyObject.TYPE, false));
        checker.assertChangesEqual(changeSet,
                "<delete type='dummyObject' id='1'/>");

        changeSet.processCreation(DummyObject.TYPE, creationValues);
        checker.assertChangesEqual(changeSet,
                "<update type='dummyObject' id='1' " +
                        "  name='name1' _name='(null)'" +
                        "  present='true' _present='(null)'/>");

        changeSet.processUpdate(key1, DummyObject.VALUE, 1.1, null);
        checker.assertChangesEqual(changeSet,
                "<update type='dummyObject' id='1' " +
                        "  name='name1' _name='(null)'" +
                        "  present='true' _present='(null)'" +
                        "  value='1.1' _value='(null)'/>");
    }

    @Test
    public void testDeletionClearsAllValues() throws Exception {
        changeSet.processUpdate(key1, DummyObject.VALUE, 1.1, null);
        changeSet.processDeletion(key1, FieldValuesBuilder.createEmpty(DummyObject.TYPE, false));
        changeSet.processCreation(DummyObject.TYPE, creationValues);
        checker.assertChangesEqual(changeSet,
                "<update type='dummyObject' id='1' " +
                        "  name='name1' _name='(null)'" +
                        "  present='true' _present='(null)'" +
                        "  value='1.1' _value='(null)'/>");
    }

    @Test
    public void testDeleteStateErrorTransitions() throws Exception {
        changeSet.processDeletion(key1, FieldValues.EMPTY);
        try {
            changeSet.processUpdate(key1, DummyObject.DATE, null, null);
            fail();
        } catch (InvalidState e) {
            assertEquals("Object dummyObject[id=1] was deleted and cannot be updated", e.getMessage());
        }
        try {
            changeSet.processDeletion(key1, FieldValues.EMPTY);
            fail();
        } catch (InvalidState e) {
            assertEquals("Object dummyObject[id=1] was already deleted", e.getMessage());
        }
    }

    @Test
    public void testContainsChangesForType() throws Exception {
        assertFalse(changeSet.containsChanges(DummyObject.TYPE));

        changeSet.processDeletion(key1, FieldValues.EMPTY);
        assertTrue(changeSet.containsChanges(DummyObject.TYPE));

        assertFalse(changeSet.containsChanges(DummyObject2.TYPE));
    }

    @Test
    public void testChangedGlobTypesList() throws Exception {
        changeSet.processCreation(newKey(DummyObject.TYPE, 2), FieldValues.EMPTY);
        changeSet.processUpdate(newKey(DummyObject2.TYPE, 1), DummyObject2.LABEL, "name", null);
        changeSet.processDeletion(newKey(DummyObjectWithLinks.TYPE, 0), FieldValues.EMPTY);

        TestUtils.assertSetEquals(changeSet.getChangedTypes(),
                DummyObject.TYPE, DummyObject2.TYPE, DummyObjectWithLinks.TYPE);
    }

    @Test
    public void testApplyingReverseChangesClearsTheChangeSet() throws Exception {
        final MutableChangeSet changeSet = XmlChangeSetParser.parse(DummyModel.get(), new StringReader(
                "<changes>" +
                        "  <create type='dummyObject' id='1' name='name1'/>" +
                        "  <update type='dummyObject' id='2' name='newName2' _name='name2'/>" +
                        "  <delete type='dummyObject' id='3' _name='name3'/>" +
                        "</changes>"
        ));

        final ChangeSet reverse = changeSet.reverse();

        reverse.accept(new ChangeSetVisitor() {
            public void visitCreation(Key key, FieldsValueScanner values) throws Exception {
                changeSet.processCreation(key, values);
            }

            public void visitUpdate(Key key, FieldsValueWithPreviousScanner values) throws Exception {
                changeSet.processUpdate(key, values);
            }

            public void visitDeletion(Key key, FieldsValueScanner previousValues) throws Exception {
                changeSet.processDeletion(key, previousValues);
            }
        });

        assertEquals(0, changeSet.getChangeCount());
    }

    @Test
    public void testMergeOnDifferentKeys() throws Exception {
        changeSet.processCreation(key1, FieldValues.EMPTY);
        newChangeSet.processCreation(newKey(DummyObject.TYPE, 2), FieldValues.EMPTY);
        newChangeSet.processUpdate(newKey(DummyObject.TYPE, 3), DummyObject.NAME, "name", null);
        newChangeSet.processDeletion(newKey(DummyObject.TYPE, 4), FieldValues.EMPTY);

        checkMerge(
                "<create type='dummyObject' id='1'/>" +
                        "<update type='dummyObject' id='3' name='name' _name='(null)'/>" +
                        "<create type='dummyObject' id='2'/>" +
                        "<delete type='dummyObject' id='4'/>");
        assertEquals(4, changeSet.size());
    }

    @Test
    public void testMergeCreationAndDeletion() throws Exception {
        changeSet.processCreation(key1, FieldValues.EMPTY);
        assertFalse(changeSet.isEmpty());

        newChangeSet.processDeletion(key1, FieldValues.EMPTY);
        checkMerge("");
        assertTrue(changeSet.isEmpty());
        assertEquals(0, changeSet.size());
    }

    @Test
    public void testMergeWithDoubleCreationError() throws Exception {
        changeSet.processCreation(key1, FieldValues.EMPTY);
        newChangeSet.processCreation(key1, FieldValues.EMPTY);
        try {
            changeSet.merge(newChangeSet);
            fail();
        } catch (InvalidState e) {
            assertEquals("Object dummyObject[id=1] already exists", e.getMessage());
        }
    }

    private void checkMerge(String xml) {
        changeSet.merge(newChangeSet);
        checker.assertChangesEqual(changeSet, xml);
    }

    @Test
    public void testClear() throws Exception {
        changeSet.processUpdate(key1, DummyObject.VALUE, 1.1, null);
        changeSet.processDeletion(newKey(DummyObject.TYPE, 10), FieldValues.EMPTY);
        changeSet.processCreation(newKey(DummyObject2.TYPE, 11), FieldValues.EMPTY);
        changeSet.processCreation(newKey(DummyObjectWithLinks.TYPE, 12), FieldValues.EMPTY);
        changeSet.clear(Arrays.asList(DummyObject.TYPE, DummyObject2.TYPE));
        checker.assertChangesEqual(changeSet,
                "<create type='dummyObjectWithLinks' id='12'/>");
    }

    @Test
    public void testReverse() throws Exception {
        checkReverse("<changes>" +
                        "  <create type='dummyObject' id='1' name='name1'/>" +
                        "  <update type='dummyObject' id='2' name='newName2' _name='name2'/>" +
                        "  <delete type='dummyObject' id='3' _name='name3'/>" +
                        "</changes>",
                "<changes>" +
                        "  <update type='dummyObject' id='2' _name='newName2' name='name2'/>" +
                        "  <delete type='dummyObject' id='1' _name='name1'/>" +
                        "  <create type='dummyObject' id='3' name='name3'/>" +
                        "</changes>");
    }

    @Test
    public void testMergeWithReverseEqualsEmpty() throws Exception {
        MutableChangeSet changeSet = XmlChangeSetParser.parse(DummyModel.get(), new StringReader(
                "<changes>" +
                        "  <create type='dummyObject' id='1' name='name1'/>" +
                        "  <update type='dummyObject' id='2' name='newName2' _name='name2'/>" +
                        "  <delete type='dummyObject' id='3' _name='name3'/>" +
                        "</changes>"
        ));

        changeSet.merge(changeSet.reverse());

        assertEquals(0, changeSet.getChangeCount());
    }

    private void checkReverse(String input, String expected) {
        ChangeSet changeSet = XmlChangeSetParser.parse(DummyModel.get(), new StringReader(input));
        ChangeSet reverse = changeSet.reverse();

        StringWriter writer = new StringWriter();
        XmlChangeSetWriter.write(reverse, writer);

        XmlTestUtils.assertEquivalent(expected, writer.toString());
    }

    @Test
    public void checkPrint() throws Exception {
        MutableChangeSet changeSet = XmlChangeSetParser.parse(DummyModel.get(), new StringReader(
                "<changes>" +
                        "  <create type='dummyObject' id='1' name='name1'/>" +
                        "  <update type='dummyObject' id='2' name='newName2' _name='name2'/>" +
                        "  <delete type='dummyObject' id='3' _name='name3'/>" +
                        "</changes>"
        ));
        assertEquals(
                "delete : id='3' name='name3' value='' count='' present='' date='' password='' linkId='' link2Id=''\n" +
                        "update : id='2' name='newName2'\n" +
                        "                _name='name2'\n" +
                        "create : id='1' name='name1' value='' count='' present='' date='' password='' linkId='' link2Id=''\n",
                changeSet.toString());
    }

    @Test
    void checkDeltaOnSubObjectAreManaged() {
        final MutableGlob sub = DummyObjectInner.create(1.2);
        creationValues =
                FieldValuesBuilder
                        .init(DummyObjectWithInner.ID, 1)
                        .set(DummyObjectWithInner.VALUE, sub)
                        .get();

        changeSet.processCreation(DummyObjectWithInner.TYPE, creationValues);
        checker.assertChangesEqual(changeSet,
                "  <create id=\"1\" type=\"dummyObjectWithInner\">" +
                "    <value value=\"1.2\"/>" +
                "  </create>");
        changeSet.processUpdate(KeyBuilder.create(DummyObjectWithInner.TYPE, 1), DummyObjectWithInner.VALUE, sub.duplicate()
                .set(DummyObjectInner.VALUE, 3.3), sub);
        checker.assertChangesEqual(changeSet,
                "<create id=\"1\" type=\"dummyObjectWithInner\">" +
                "    <value value=\"3.3\"/>" +
                "  </create>");

    }

    @Test
    void checkDeltaOnSubObjectArrayAreManaged() {
        final MutableGlob sub = DummyObjectInner.create(1.2);
        final Glob[] values = {sub, DummyObjectInner.create(2.2)};
        creationValues =
                FieldValuesBuilder
                        .init(DummyObjectWithInner.ID, 1)
                        .set(DummyObjectWithInner.VALUES, values)
                        .get();

        changeSet.processCreation(DummyObjectWithInner.TYPE, creationValues);
        checker.assertChangesEqual(changeSet,
                "  <create id=\"1\" type=\"dummyObjectWithInner\">" +
                "    <values value=\"1.2\"/>" +
                "    <values value=\"2.2\"/>" +
                "  </create>");
        changeSet.processUpdate(KeyBuilder.create(DummyObjectWithInner.TYPE, 1), DummyObjectWithInner.VALUES,
                new Glob[]{sub.duplicate().set(DummyObjectInner.VALUE, 3.3)}, values);
        checker.assertChangesEqual(changeSet,
                "<create id=\"1\" type=\"dummyObjectWithInner\">" +
                "    <values value=\"3.3\"/>" +
                "  </create>");

    }

}
