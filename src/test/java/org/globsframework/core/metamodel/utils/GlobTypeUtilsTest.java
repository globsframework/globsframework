package org.globsframework.core.metamodel.utils;

import org.globsframework.core.metamodel.DummyObject;
import org.globsframework.core.metamodel.DummyObject2;
import org.globsframework.core.metamodel.annotations.NamingField;
import org.globsframework.core.utils.exceptions.ItemNotFound;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GlobTypeUtilsTest {
    @Test
    public void testGetType() throws Exception {
        assertSame(DummyObject.TYPE, GlobTypeUtils.getType(DummyObject.class));

        try {
            GlobTypeUtils.getType(String.class);
            fail();
        } catch (Exception e) {
            assertEquals("Class java.lang.String does not define a GlobType", e.getMessage());
        }
    }

    @Test
    public void testGetNamingField() throws Exception {
        assertSame(DummyObject.NAME, GlobTypeUtils.getNamingField(DummyObject.TYPE));

        try {
            GlobTypeUtils.getNamingField(DummyObject2.TYPE);
            fail();
        } catch (ItemNotFound e) {
            assertEquals("no field found with EmptyKey/" + NamingField.TYPE.getName() + " under dummyObject2", e.getMessage());
        }
    }

    @Test
    public void testFindNamingField() throws Exception {
        assertSame(DummyObject.NAME, GlobTypeUtils.findNamingField(DummyObject.TYPE));
        assertNull(GlobTypeUtils.findNamingField(DummyObject2.TYPE));
    }
}
