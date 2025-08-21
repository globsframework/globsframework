package org.globsframework.core.utils;

import org.junit.jupiter.api.Assertions;

import java.util.Arrays;
import java.util.List;

public class ArrayTestUtils {
    public static void assertEquals(Object[] expected, Object[] actual) {
        if (!Arrays.equals(expected, actual)) {
            Assertions.assertEquals(toString(expected), toString(actual));
        }
    }

    public static void assertEquals(int[] expected, int[] actual) {
        if (!Arrays.equals(expected, actual)) {
            Assertions.assertEquals(toString(expected), toString(actual));
        }
    }

    public static void assertContentEquals(List actual, Object... expected) {
        Object[] actualArray = actual.toArray();
        if (!Arrays.equals(actualArray, expected)) {
            Assertions.assertEquals(toString(expected), toString(actualArray));
        }
    }

    public static String toString(Object... array) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            Object object = array[i];
            builder.append(object != null ? object.toString() : "null");
            if (i < (array.length - 1)) {
                builder.append('\n');
            }
        }
        return builder.toString();
    }
}
