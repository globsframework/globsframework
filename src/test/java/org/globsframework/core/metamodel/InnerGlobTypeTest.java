package org.globsframework.core.metamodel;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class InnerGlobTypeTest {
    @Test
    public void load() {
        assertEquals(DummyObjectInner.TYPE, DummyObjectWithInner.VALUE.getTargetType());
        assertEquals(DummyObjectInner.TYPE, DummyObjectWithInner.VALUES.getTargetType());
    }
}
