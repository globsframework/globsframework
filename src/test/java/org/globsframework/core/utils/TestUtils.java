package org.globsframework.core.utils;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.model.FieldValues;
import org.globsframework.core.model.FieldValuesWithPrevious;
import org.globsframework.core.model.FieldsValueScanner;
import org.globsframework.core.model.FieldsValueWithPreviousScanner;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;

import static org.junit.jupiter.api.Assertions.*;


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

public class TestUtils {

    public final static byte[] SAMPLE_BYTE_ARRAY;
    public static final File TMP_DIR = new File("tmp");

    private TestUtils() {
    }

    static {
        SAMPLE_BYTE_ARRAY = new byte[12];
        for (int i = 0; i < SAMPLE_BYTE_ARRAY.length; i++) {
            SAMPLE_BYTE_ARRAY[i] = 1;
        }
        clearTmpDir();
    }

    public static void assertFails(Runnable functor, Class<? extends Exception> expectedException) {
        try {
            functor.run();
        } catch (Exception e) {
            if (!e.getClass().isAssignableFrom(expectedException)) {
                StringWriter writer = new StringWriter();
                e.printStackTrace(new PrintWriter(writer));
                fail(expectedException.getName() + " expected but was " + e.getClass().getName() + "\n" +
                        writer.toString());
            }
        }
    }

    public static <T> void assertEquals(T[] expected, T... actual) {
        if (!Arrays.equals(expected, actual)) {
            fail("expected: \n" + Arrays.toString(expected) + "\n   but was: " + Arrays.toString(actual));
        }
    }

    public static void assertEquals(double precision, double[] actual, double... expected) {
        if (actual.length != expected.length) {
            fail("invalid length - expected: " + Arrays.toString(expected) + " but was: " + Arrays.toString(actual));
        }
        for (int i = 0; i < expected.length; i++) {
            if (Math.abs(expected[i] - actual[i]) > precision) {
                fail("error at position " + i + " - expected: " + Arrays.toString(expected) + " but was: " + Arrays.toString(actual));
            }
        }
    }

    public static void assertEquals(double[] actual, double... expected) {
        if (!Arrays.equals(expected, actual)) {
            fail("expected: " + Arrays.toString(expected) + " but was: " + Arrays.toString(actual));
        }
    }

    public static void assertEquals(int[] actual, int... expected) {
        if (!Arrays.equals(expected, actual)) {
            fail("expected: " + Arrays.toString(expected) + " but was: " + Arrays.toString(actual));
        }
    }

    public static <T> void assertIteratorContains(Iterator<T> iterator, T... values) throws Exception {
        if ((values.length == 0) && iterator.hasNext()) {
            fail("Expected empty iterator, but contains at least: " + iterator.next());
        }
        for (int i = 0; i < values.length; i++) {
            if (!iterator.hasNext()) {
                fail("Iterator has " + i + " elements instead of " + values.length);
            }
            T value = values[i];
            T nextValue = iterator.next();
            if (!Utils.equal(nextValue, value)) {
                fail("Error at index " + i + ": expected: " + value + " but was: " + nextValue);
            }
        }
        if (iterator.hasNext()) {
            fail("Iterator has more than " + values.length + " elements, at least: " + iterator.next());
        }
    }

    public static <T> void assertEmpty(Collection<T> list) {
        if (!list.isEmpty()) {
            fail("Expected an empty list, but contains: " + list);
        }
    }

    public static <T> void assertEmpty(Object[] array) {
        if (array.length != 0) {
            fail("Expected an empty array, but contains: " + Arrays.toString(array));
        }
    }

    public static void assertEquals(GlobType[] expected, GlobType[] actual) {
        assertEquals(Arrays.asList(expected), actual);
    }

    public static <T> void assertEquals(Collection<T> expected, Collection<T> actual) {
        if ((expected.isEmpty())) {
            assertEmpty(actual);
        }
        if (expected.size() != actual.size()) {
            showFailures("Invalid number of items", actual, expected);
        }
        Iterator actualIterator = actual.iterator();
        int index = 0;
        for (T anExpected : expected) {
            if (!Utils.equal(actualIterator.next(), anExpected)) {
                showFailures("Error at item " + index, actual, expected);
            }
            index++;
        }
    }

    public static <T> void assertEquals(Collection<T> actual, T... expected) {
        assertEquals(Arrays.asList(expected), actual);
    }

    public static <T> void assertSetEquals(T[] actual, T... expected) {
        assertSetEquals(Arrays.asList(actual), Arrays.asList(expected));
    }

    public static <T> void assertSetEquals(Collection<T> actual, T... expected) {
        assertSetEquals(actual, Arrays.asList(expected));
    }

    public static <T> void assertSetEquals(Collection<T> actual, Collection<T> expected) {
        assertEquals(new HashSet<>(expected), new HashSet<>(actual));
    }

    public static <T> void assertSetEquals(Iterator<T> actual, T... expected) {
        Set<T> actualSet = new HashSet<>();
        while (actual.hasNext()) {
            actualSet.add(actual.next());
        }
        assertEquals(actualSet, new HashSet<>(Arrays.asList(expected)));
    }

    public static <T> void assertContained(T[] actualArray, T[] expectedItems) {
        List<T> actual = Arrays.asList(actualArray);
        List<T> expected = Arrays.asList(expectedItems);
        if (!actual.containsAll(expected)) {
            showFailures("Expected list is not contained in actual", actual, expected);
        }
    }

    public static <T> void assertContains(Collection<T> actual, T... expectedItems) {
        if (!actual.containsAll(Arrays.asList(expectedItems))) {
            fail("Collection: " + actual + "\n does not contain: " + Arrays.toString(expectedItems));
        }
    }

    public static <T> void assertNotContains(Collection<T> actual, T... expectedItems) {
        for (T item : expectedItems) {
            if (actual.contains(item)) {
                fail("Item '" + item + "' was found - actual collection: " + actual);
            }
        }
    }

    public static void assertDatesEqual(Date date1, Date date2, int margin) throws Exception {
        if (Math.abs(date1.getTime() - date2.getTime()) > margin) {
            Assertions.assertEquals(date1, date2);
        }
    }

    private static <T> void showFailures(String message, Collection<T> actual, Collection<T> expected) {
        fail(message + "\n" +
                "expected: " + expected + "\n" +
                "but was:  " + actual);
    }

    public static void clearTmpDir() {
        TMP_DIR.mkdirs();
        for (File file : TMP_DIR.listFiles()) {
            file.delete();
        }
    }

    public static File getFileName(Object test, String extension) {
        try {
            File tmpDir = TMP_DIR;
            if (!tmpDir.exists()) {
                if (!tmpDir.mkdir()) {
                    throw new RuntimeException("Can not create root dir " + tmpDir.getAbsolutePath());
                }
            }
            return File.createTempFile(test.getClass().getSimpleName(), extension, TMP_DIR);
        } catch (IOException e) {
            throw new RuntimeException("Unable to create file", e);
        }
    }

    public static <T> void checkEmpty(Collection<T> keys, String message) {
        if (!keys.isEmpty()) {
            StringBuilder text = new StringBuilder();
            text.append(message);
            for (Object key : keys) {
                text.append("\n").append(key);
            }
            fail(text.toString());
        }
    }

    public static void retry(Runnable runnable) throws Exception {
        long end = System.currentTimeMillis() + 3000;
        AssertionFailedError exception = null;
        while (System.currentTimeMillis() < end) {
            try {
                runnable.run();
                Thread.sleep(200);
                return;
            } catch (AssertionFailedError e) {
                exception = e;
            }
        }
        throw exception;
    }

    public static boolean contains(FieldsValueScanner valueScanner, Field searchField) {
        return valueScanner.safeApply(new FieldValues.Functor() {
            boolean contains = false;

            public void process(Field field, Object value) throws Exception {
                contains |= field == searchField;
            }
        }).contains;
    }

    public static Object get(FieldsValueScanner valueScanner, Field searchField) {
        return valueScanner.safeApply(new FieldValues.Functor() {
            Object value = null;

            public void process(Field field, Object value) throws Exception {
                if (field == searchField) {
                    this.value = value;
                }
            }
        }).value;
    }

    public static Object getPrevious(FieldsValueWithPreviousScanner valueScanner, Field searchField) {
        return valueScanner.safeApplyWithPrevious(new FieldValuesWithPrevious.FunctorWithPrevious() {
            Object value = null;

            public void process(Field field, Object value, Object previous) throws Exception {
                if (field == searchField) {
                    this.value = previous;
                }
            }
        }).value;
    }

}
