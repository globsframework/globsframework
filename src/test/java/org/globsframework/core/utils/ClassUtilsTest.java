package org.globsframework.core.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClassUtilsTest {
    @Test
    public void test() throws Exception {
        assertEquals("String", String.class.getSimpleName());
    }
}
