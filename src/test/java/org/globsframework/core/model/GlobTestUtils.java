package org.globsframework.core.model;

import org.globsframework.core.metamodel.GlobModel;
import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.utils.exceptions.InvalidParameter;
import org.globsframework.core.xml.XmlChangeSetWriter;
import org.globsframework.core.xml.XmlGlobParser;
import org.globsframework.core.xml.XmlGlobWriter;
import org.globsframework.core.xml.tests.XmlComparisonMode;
import org.globsframework.core.xml.tests.XmlTestUtils;
import org.opentest4j.AssertionFailedError;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.List;

import static org.globsframework.core.xml.tests.XmlTestUtils.assertEquivalent;

public class GlobTestUtils {

    public static void assertEquals(GlobRepository repository,
                                    String expectedOutput) {
        assertEquals(repository, expectedOutput, XmlComparisonMode.EXPECTED_ATTRIBUTES_ONLY);
    }

    public static void assertEquals(GlobRepository repository,
                                    String expectedOutput,
                                    XmlComparisonMode comparisonMode) {
        assertListEquals(repository.getAll(), repository, expectedOutput, comparisonMode);
    }

    public static void assertEquals(GlobRepository repository, GlobType type, String expectedOutput) {
        assertListEquals(repository.getAll(type), repository, expectedOutput,
                XmlComparisonMode.EXPECTED_ATTRIBUTES_ONLY);
    }

    public static void assertListEquals(Collection<Glob> globs,
                                        GlobRepository repository,
                                        String expectedOutput) {
        assertListEquals(globs, repository, expectedOutput, XmlComparisonMode.EXPECTED_ATTRIBUTES_ONLY);
    }

    public static void assertListEquals(Collection<Glob> globs,
                                        GlobRepository repository,
                                        String expectedOutput,
                                        XmlComparisonMode comparisonMode) {
        StringWriter writer = new StringWriter();
        try {
            XmlGlobWriter.write(globs, repository, writer);
            switch (comparisonMode) {
                case ALL_ATTRIBUTES:
                    XmlTestUtils.assertEquivalent("<globs>" + expectedOutput + "</globs>", writer.toString());
                    break;
                case EXPECTED_ATTRIBUTES_ONLY:
                    XmlTestUtils.assertIsSubset("<globs>" + expectedOutput + "</globs>", writer.toString());
                    break;
                default:
                    throw new InvalidParameter("Unexpected comparison mode: " + comparisonMode);
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void assertEquals(GlobRepository repository1, GlobRepository repository2) {
        XmlTestUtils.assertIsSubset(dumpRepository(repository1),
                dumpRepository(repository2));
    }

    public static void assertEquals(List expectedList, List actualList, GlobRepository repository) {
        XmlTestUtils.assertIsSubset(dumpRepository(expectedList, repository),
                dumpRepository(actualList, repository));
    }

    public static String dumpRepository(GlobRepository repository) {
        return dumpRepository(repository.getAll(), repository);
    }

    public static String dumpRepository(List<Glob> globs, GlobRepository repository) {
        StringWriter writer = new StringWriter();
        try {
            XmlGlobWriter.write(globs, repository, writer);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return writer.toString();
    }

    public static void assertChangesEqual(ChangeSet changeSet, List<Key> keys, String expectedXml) {
        StringWriter writer = new StringWriter();
        XmlChangeSetWriter.write(changeSet, keys, writer);
        assertEquivalent("<changes>" + expectedXml + "</changes>", writer.toString());
    }

    public static void assertChangesEqual(ChangeSet changeSet, GlobType type, String expectedXml) {
        StringWriter writer = new StringWriter();
        XmlChangeSetWriter.write(changeSet, type, writer);
        try {
            assertEquivalent("<changes>" + expectedXml + "</changes>", writer.toString());
        } catch (AssertionFailedError e) {
            System.out.println("Expected changes: \n" + writer.toString());
            throw e;
        }
    }

    public static void assertChangesEqual(ChangeSet changeSet, String expectedXml) {
        StringWriter writer = new StringWriter();
        XmlChangeSetWriter.write(changeSet, writer);
        assertEquivalent("<changes>" + expectedXml + "</changes>", writer.toString());
    }

    public static void parse(GlobModel model, GlobRepository repository, String xmlStream) {
        XmlGlobParser.parse(model, repository,
                new StringReader("<globs>" + xmlStream + "</globs>"),
                "globs");
    }

    public static void parseIgnoreError(GlobModel model, GlobRepository repository, String xmlStream) {
        XmlGlobParser.parseIgnoreError(model, repository,
                new StringReader("<globs>" + xmlStream + "</globs>"),
                "globs");
    }

}
