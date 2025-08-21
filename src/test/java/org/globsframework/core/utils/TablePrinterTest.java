package org.globsframework.core.utils;

import org.globsframework.core.utils.exceptions.InvalidParameter;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TablePrinterTest {
    private StringWriter writer = new StringWriter();

    @Test
    public void testStandardUsage() throws Exception {
        TablePrinter table = new TablePrinter(true);
        table.setHeader("t1", "t2", "t3");
        table.addRow("Value 1", 22.0, "Item 3");
        table.addRow(1.1111, "v2", "33");
        assertEquals("| t1      | t2    | t3     |" + Strings.LINE_SEPARATOR +
                        "| 1.11    | v2    | 33     |" + Strings.LINE_SEPARATOR +
                        "| Value 1 | 22.00 | Item 3 |" + Strings.LINE_SEPARATOR,
                table.toString());
    }

    @Test
    public void testNoHeader() throws Exception {
        TablePrinter table = new TablePrinter(true);
        table.addRow("Value 1", 22.0, "Item 3");
        assertEquals("| Value 1 | 22.00 | Item 3 |" + Strings.LINE_SEPARATOR,
                table.toString());
    }

    @Test
    public void testStaticPrint() throws Exception {
        TablePrinter.print(new Object[]{"Title 1", "Title 2"},
                Arrays.asList(
                        new Object[]{"first value", "second value"},
                        new Object[]{"a", "b"}
                ),
                true, new PrintWriter(writer));

        assertEquals("| Title 1     | Title 2      |" + Strings.LINE_SEPARATOR +
                        "| a           | b            |" + Strings.LINE_SEPARATOR +
                        "| first value | second value |" + Strings.LINE_SEPARATOR,
                writer.toString());
    }

    @Test
    public void testRowsMustNotBeLargerThanTheHeader() throws Exception {
        try {
            TablePrinter.print(new Object[]{"Title 1", "Title 2"},
                    Arrays.asList(
                            new Object[]{"first value", "second value",},
                            new Object[]{"a", "b", "c"}
                    ),
                    true, new PrintWriter(writer));
        } catch (InvalidParameter e) {
            assertEquals("Row larger than the first row: [a, b, c]", e.getMessage());
        }

    }
}
