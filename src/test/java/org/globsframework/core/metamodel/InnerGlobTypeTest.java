package org.globsframework.core.metamodel;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class InnerGlobTypeTest {
    @Test
    public void load() {
        assertEquals(DummyObjectWithInner.VALUE.getTargetType(), DummyObjectInner.TYPE);
        assertEquals(DummyObjectWithInner.VALUES.getTargetType(), DummyObjectInner.TYPE);
    }
}
