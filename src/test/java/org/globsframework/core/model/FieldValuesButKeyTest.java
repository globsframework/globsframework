package org.globsframework.core.model;

import org.globsframework.core.metamodel.DummyObject;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class FieldValuesButKeyTest {

    @Test
    public void name() {
        FieldValues fieldValues = DummyObject.TYPE.instantiate().set(DummyObject.ID, 3).set(DummyObject.NAME, "toto");
        FieldValues withoutKeyField = fieldValues.withoutKeyField();
        assertEquals(1, withoutKeyField.size());
        FieldValue[] fieldValues1 = withoutKeyField.toArray();
        assertEquals(withoutKeyField.size(), fieldValues1.length);

        try {
            withoutKeyField.get(DummyObject.ID);
            fail("access to key should throw an error");
        } catch (RuntimeException e) {
        }
    }
}
