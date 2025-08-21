package org.globsframework.core.utils;

import org.globsframework.core.utils.exceptions.InvalidParameter;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UtilsTest {

    @Test
    public void testJoin() throws Exception {
        String[] input = {"b", "c"};
        String[] result = {"a", "b", "c"};
        TestUtils.assertEquals(result, Utils.join("a", input));
    }

    @Test
    public void testJoinWithArrays() throws Exception {
        String[] first = {"a", "b"};
        String[] second = {"c", "d", "e"};
        String[] result = {"a", "b", "c", "d", "e"};
        TestUtils.assertEquals(result, Utils.join(first, second));
    }

    @Test
    public void testSplitList() throws Exception {

        checkSplit(Arrays.asList("a", "b", "c"), 1,
                Arrays.asList("a"),
                Arrays.asList("b"),
                Arrays.asList("c"));

        checkSplit(Arrays.asList("a", "b", "c", "d", "e"), 2,
                Arrays.asList("a", "b"),
                Arrays.asList("c", "d"),
                Arrays.asList("e"));

        checkSplit(Arrays.asList("a", "b"), 2,
                Arrays.asList("a", "b"));

        checkSplit(Arrays.asList("a", "b", "c", "d", "e"), 6,
                Arrays.asList("a", "b", "c", "d", "e"));
    }

    private void checkSplit(List<String> input, int count,
                            List<String>... result) {
        String actual = Utils.split(input, count).toString() + "\n";
        String expected = Arrays.asList(result).toString() + "\n";
        assertEquals(expected, actual);
    }

    @Test
    public void testSplitAnEmptyList() throws Exception {
        List<List<String>> result = Utils.split(new ArrayList<String>(), 2);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testSplitListParameterCheck() throws Exception {
        try {
            Utils.split(Arrays.asList("a", "b"), 0);
        } catch (InvalidParameter e) {
            assertEquals("Parameter 'count' must be > 0 - actual: 0", e.getMessage());
        }
    }

    @Test
    public void testMinMax() throws Exception {
        assertNull(Utils.min());
        assertEquals("a", Utils.min("a", "b"));
        assertEquals("a", Utils.min("a", null, "b"));

        assertNull(Utils.max());
        assertEquals("b", Utils.max("a", "b"));
        assertEquals("b", Utils.max("a", null, "b"));
    }

    @Test
    public void testMinInt() throws Exception {
        assertEquals(0, Utils.minInt());
        assertEquals(1, Utils.minInt(4, 1, 2, 3));
    }

    @Test
    public void testCompare() throws Exception {
        assertTrue(Utils.compare("a", "b") < 0);
        assertTrue(Utils.compare("a", "a") == 0);
        assertTrue(Utils.compare("b", "a") > 0);
        assertTrue(Utils.compare(null, "a") < 0);
        assertTrue(Utils.compare("a", null) > 0);
    }

    @Test
    public void testCompareIgnoreCase() throws Exception {
        assertTrue(Utils.compareIgnoreCase("a", "b") < 0);
        assertTrue(Utils.compareIgnoreCase("a", "B") < 0);
        assertTrue(Utils.compareIgnoreCase("a", "A") == 0);
        assertTrue(Utils.compareIgnoreCase("B", "a") > 0);
        assertTrue(Utils.compareIgnoreCase(null, "a") < 0);
        assertTrue(Utils.compareIgnoreCase("a", null) > 0);
    }

    @Test
    public void testAppend() throws Exception {
        ArrayTestUtils.assertEquals(new int[]{1, 3, 1, 5, 6},
                Utils.append(new int[]{1, 3}, new int[]{1, 5, 6}));
    }

    @Test
    public void testRange() throws Exception {
        ArrayTestUtils.assertEquals(new Integer[]{1}, Utils.range(1, 1));
        ArrayTestUtils.assertEquals(new Integer[]{-1, 0, 1, 2}, Utils.range(-1, 2));

        try {
            Utils.range(2, 1);
        } catch (Exception e) {
            assertEquals("Lower bound 2 should be less than 1", e.getMessage());
        }
    }
}
