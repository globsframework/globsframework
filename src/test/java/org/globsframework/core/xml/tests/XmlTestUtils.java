package org.globsframework.core.xml.tests;

import org.apache.xerces.parsers.SAXParser;
import org.globsframework.saxstack.comparator.XmlComparator;
import org.globsframework.saxstack.utils.XmlUtils;
import static org.junit.jupiter.api.Assertions.*;


import java.io.IOException;

public class XmlTestUtils {
    private XmlTestUtils() {
    }

    public static void assertEquals(String expected, String actual) {
        SAXParser parser = new SAXParser();
        try {
            if (!XmlComparator.areEqual(expected, actual, parser)) {
                showStringDiff(expected, actual, parser);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void assertEquivalent(String expected, String actual) {
        SAXParser parser = new SAXParser();
        try {
            if (!XmlComparator.areEquivalent(expected, actual, parser)) {
                showStringDiff(expected, actual, parser);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void assertIsSubset(String expected, String actual) {
        SAXParser parser = new SAXParser();
        try {
            if (XmlComparator.computeDiff(expected, actual, parser) != null) {
                showStringDiff(expected, actual, parser);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void showStringDiff(String expected, String actual, SAXParser parser) throws IOException {
        assertEquals(XmlUtils.format(expected, parser, 4),
                XmlUtils.format(actual, parser, 4));
    }
}
