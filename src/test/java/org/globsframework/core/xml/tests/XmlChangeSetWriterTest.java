package org.globsframework.core.xml.tests;

import org.globsframework.core.metamodel.DummyObject;
import org.globsframework.core.model.FieldValuesBuilder;
import org.globsframework.core.model.delta.DefaultChangeSet;
import org.globsframework.core.xml.XmlChangeSetWriter;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;

import static org.globsframework.core.model.KeyBuilder.newKey;

public class XmlChangeSetWriterTest {

    @Test
    public void test() throws Exception {
        DefaultChangeSet changeSet = new DefaultChangeSet();
        changeSet.processCreation(DummyObject.TYPE,
                FieldValuesBuilder.init()
                        .set(DummyObject.ID, 1)
                        .set(DummyObject.NAME, "obj1")
                        .get());

        changeSet.processUpdate(newKey(DummyObject.TYPE, 2), DummyObject.VALUE, 2.3, 1.7);
        changeSet.processUpdate(newKey(DummyObject.TYPE, 3), DummyObject.VALUE, null, 1.5);
        changeSet.processDeletion(newKey(DummyObject.TYPE, 4), FieldValuesBuilder.init(DummyObject.NAME, "obj3").get());

        StringWriter writer = new StringWriter();
        XmlChangeSetWriter.write(changeSet, writer);
        XmlTestUtils.assertEquivalent("<changes>" +
                        "  <create type='dummyObject' id='1' name='obj1'/>" +
                        "  <update type='dummyObject' id='2' value='2.3' _value='1.7'/>" +
                        "  <update type='dummyObject' id='3' value='(null)' _value='1.5'/>" +
                        "  <delete type='dummyObject' id='4' _name='obj3'/>" +
                        "</changes>",
                writer.toString());
    }
}
