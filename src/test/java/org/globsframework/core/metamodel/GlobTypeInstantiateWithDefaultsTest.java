package org.globsframework.core.metamodel;

import org.globsframework.core.model.MutableGlob;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class GlobTypeInstantiateWithDefaultsTest {
    @Test
    public void test() {
        MutableGlob globWithDefaults = DummyObjectWithDefaultValues.TYPE.instantiateWithDefaults();

        assertEquals("Hello", globWithDefaults.get(DummyObjectWithDefaultValues.STRING));
        assertEquals(globWithDefaults.get(DummyObjectWithDefaultValues.DOUBLE), Double.valueOf(3.14159265));
        assertEquals(globWithDefaults.get(DummyObjectWithDefaultValues.INTEGER), Integer.valueOf(7));
        assertEquals(Long.valueOf(5), globWithDefaults.get(DummyObjectWithDefaultValues.LONG));
        assertEquals(new BigDecimal("1.61803398875"), globWithDefaults.get(DummyObjectWithDefaultValues.BIG_DECIMAL));
        assertNull(globWithDefaults.get(DummyObjectWithDefaultValues.ID));
        assertNull(globWithDefaults.get(DummyObjectWithDefaultValues.LINK));
        assertTrue(globWithDefaults.get(DummyObjectWithDefaultValues.BOOLEAN));

        MutableGlob glob = DummyObjectWithDefaultValues.TYPE.instantiate();
        assertNull(glob.get(DummyObjectWithDefaultValues.STRING));
        assertNull(glob.get(DummyObjectWithDefaultValues.DOUBLE));
        assertNull(glob.get(DummyObjectWithDefaultValues.INTEGER));
        assertNull(glob.get(DummyObjectWithDefaultValues.LONG));
        assertNull(glob.get(DummyObjectWithDefaultValues.BIG_DECIMAL));
        assertNull(glob.get(DummyObjectWithDefaultValues.ID));
        assertNull(glob.get(DummyObjectWithDefaultValues.LINK));
        assertNull(glob.get(DummyObjectWithDefaultValues.BOOLEAN));

    }
}
