package org.globsframework.core.utils;

import org.globsframework.core.utils.exceptions.InvalidParameter;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class StringsTest {

    @Test
    public void testIsNull() throws Exception {
        assertTrue(Strings.isNullOrEmpty(null));
        assertTrue(Strings.isNullOrEmpty(""));
        assertFalse(Strings.isNullOrEmpty("a"));
    }

    @Test
    public void testCapitalize() throws Exception {
        assertEquals("TotoTiti", Strings.capitalize("totoTiti"));
        assertNull(Strings.capitalize(null));
        assertEquals("", Strings.capitalize(""));
    }

    @Test
    public void testUncapitalize() throws Exception {
        assertEquals("TotoTiti", Strings.capitalize("totoTiti"));
        assertNull(Strings.uncapitalize(null));
        assertEquals("", Strings.uncapitalize(""));
    }

    @Test
    public void testToLower() throws Exception {
        assertEquals("", Strings.toNiceLowerCase(""));
        assertEquals("name", Strings.toNiceLowerCase("name"));
        assertEquals("aName", Strings.toNiceLowerCase("A_NAME"));
        assertEquals("helloWorld", Strings.toNiceLowerCase("HELLO_WORLD"));
    }

    @Test
    public void testToUpper() throws Exception {
        assertEquals("", Strings.toNiceUpperCase(""));
        assertEquals("NAME", Strings.toNiceUpperCase("name"));
        assertEquals("A_NAME_WITH_PARTS", Strings.toNiceUpperCase("aNameWithParts"));
        assertEquals("HELLO_WORLD", Strings.toNiceUpperCase("helloWorld"));
    }

    @Test
    public void testMapToString() throws Exception {
        Map map = new HashMap();
        map.put("toto", 1);
        map.put(3, "4");
        assertEquals("'3' = '4'" + Strings.LINE_SEPARATOR
                        + "'toto' = '1'" + Strings.LINE_SEPARATOR,
                Strings.toString(map));
    }

    @Test
    public void testJoin() throws Exception {
        assertEquals("", Strings.join("", null));
        assertEquals("a c", Strings.join("a", null, "", "c"));
    }

    @Test
    public void testCut() throws Exception {
        assertEquals(null, Strings.cut(null, 1));
        assertEquals("", Strings.cut("", 1));
        assertEquals("12", Strings.cut("12345", 2));
        assertEquals("1234567...", Strings.cut("12345678901234567890", 10));
    }

    @Test
    public void testSplit() throws Exception {

        assertEquals(null, Strings.toSplittedHtml(null, 10));
        assertEquals("", Strings.toSplittedHtml("", 10));

        assertEquals("<html>Two words</html>", Strings.toSplittedHtml("Two words", 10));
        assertEquals("<html>Two words</html>", Strings.toSplittedHtml("Two \n \t words", 10));

        assertEquals("<html>One Two<br>Three</html>", Strings.toSplittedHtml("One Two Three", 10));
        assertEquals("<html>One Two<br>Three</html>", Strings.toSplittedHtml("One\nTwo\nThree", 10));

        assertEquals("<html>One Two<br>Three Four<br>Five</html>", Strings.toSplittedHtml("One Two Three Four Five", 10));

        try {
            Strings.toSplittedHtml("One", -1);
        } catch (InvalidParameter e) {
            assertEquals("Line length parameter must be greater than 0", e.getMessage());
        }
    }

    @Test
    public void testTrimLines() throws Exception {
        assertEquals("One", Strings.trimLines("One"));
        assertEquals("One\n" +
                        "\n" +
                        "Two\n" +
                        "Three",
                Strings.trimLines("  One \n\n" +
                        "Two   \n" +
                        "  Three  "));

    }

    @Test
    public void testUnaccent() throws Exception {
        assertEquals("aaaa eeeee iii ooo uuu", Strings.unaccent("aàäâ eéèêë iïî oöô uùü"));
        assertEquals("AAAA EEEEE III OOO UUU", Strings.unaccent("AÀÄÂ EÉÈÊË IÏÎ OÖÔ UÙÜ"));
    }

    @Test
    public void testRemoveNewLine() throws Exception {
        assertEquals("some new line", Strings.removeNewLine("some \n\nnew \n\rline\r"));
    }

    @Test
    public void testRemoveSpaces() throws Exception {
        assertEquals("abcd123", Strings.removeSpaces("  a\tb c\nd 12  \n3 "));
    }
}
