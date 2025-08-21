package org.globsframework.core.model;

import org.globsframework.core.metamodel.DummyObject;
import org.globsframework.core.utils.exceptions.InvalidParameter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class FieldValuesBuilderTest {
    @Test
    public void testValuesMustComplyWithTheFieldType() throws Exception {
        try {
            FieldValuesBuilder.init().setValue(DummyObject.PRESENT, "a");
            fail();
        } catch (InvalidParameter e) {
            assertEquals("Value 'a' (java.lang.String) is not authorized for field: " +
                    DummyObject.PRESENT.getName() + " (expected java.lang.Boolean)", e.getMessage());
        }
    }
}
