package org.globsframework.core.model.impl;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.GlobTypeBuilderFactory;
import org.globsframework.core.metamodel.fields.StringField;
import org.globsframework.core.model.MutableGlob;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CheckSetUnsetTest {

    @Test
    public void testManyFieldToTestGlobImplementation() {
        for (int i = 0; i < 256; i++) {
            check(i);
        }
    }

    private static void check(int nbField) {
        final GlobTypeBuilder globTypeBuilder = GlobTypeBuilderFactory.create("test");
        StringField[] fields = new StringField[nbField];
        for (int i = 0; i < nbField; i++) {
            fields[i] = globTypeBuilder.declareStringField("f_" + i);
        }

        final GlobType globType = globTypeBuilder.get();

        final MutableGlob instantiate = globType.instantiate();
        for (StringField field : fields) {
            assertFalse(instantiate.isSet(field));
            instantiate.set(field, "AA");
            assertTrue(instantiate.isSet(field));
            instantiate.unset(field);
            assertFalse(instantiate.isSet(field));
            instantiate.set(field, "AA");
            assertTrue(instantiate.isSet(field));
        }
    }
}
