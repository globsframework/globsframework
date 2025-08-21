package org.globsframework.core.model.format;

import org.globsframework.core.metamodel.DummyObject;
import org.globsframework.core.metamodel.DummyObject2;
import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.GlobRepository;
import org.globsframework.core.model.GlobRepositoryBuilder;
import org.globsframework.core.utils.Strings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;

import static org.globsframework.core.model.FieldValue.value;
import static org.globsframework.core.utils.Strings.LINE_SEPARATOR;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GlobPrinterTest {
    private GlobRepository repository;

    @BeforeEach
    public void setUp() throws Exception {
        repository = GlobRepositoryBuilder.createEmpty();
    }

    @Test
    public void testStandardCase() throws Exception {
        createObj2(1, "a");
        createObj2(0, "b");
        createObj2(2, "c");
        checkOutput("===== dummyObject2 ======" + LINE_SEPARATOR +
                "| id | label | value |" + LINE_SEPARATOR +
                "| 0  | b     |       |" + LINE_SEPARATOR +
                "| 1  | a     |       |" + LINE_SEPARATOR +
                "| 2  | c     |       |" + LINE_SEPARATOR +
                LINE_SEPARATOR);
    }

    @Test
    public void testTwoTypes() throws Exception {
        createObj2(1, "a");

        createObj(0, "n1", 20060223);
        createObj(1, "n2", 20060221);

        checkOutput("===== dummyObject ======" + LINE_SEPARATOR +
                "| id | name | value | count | present | date     | password | linkId | link2Id |" + LINE_SEPARATOR +
                "| 0  | n1   |       |       |         | 20060223 |          |        |         |" + LINE_SEPARATOR +
                "| 1  | n2   |       |       |         | 20060221 |          |        |         |" + LINE_SEPARATOR +
                "" + LINE_SEPARATOR +
                "===== dummyObject2 ======" + LINE_SEPARATOR +
                "| id | label | value |" + LINE_SEPARATOR +
                "| 1  | a     |       |" + LINE_SEPARATOR +
                LINE_SEPARATOR);
    }

    @Test
    public void testFiltering() throws Exception {
        createObj2(1, "a");
        createObj(0, "n1", 20060223);

        checkOutput("===== dummyObject ======" + LINE_SEPARATOR +
                        "| id | name | value | count | present | date     | password | linkId | link2Id |" + LINE_SEPARATOR +
                        "| 0  | n1   |       |       |         | 20060223 |          |        |         |" + LINE_SEPARATOR +
                        "" + LINE_SEPARATOR,
                DummyObject.TYPE);

        checkOutput("===== dummyObject2 ======" + LINE_SEPARATOR +
                        "| id | label | value |" + LINE_SEPARATOR +
                        "| 1  | a     |       |" + LINE_SEPARATOR +
                        "" + LINE_SEPARATOR,
                DummyObject2.TYPE);
    }

    @Test
    public void testExcludingColumns() throws Exception {
        createObj2(1, "a");

        String actual =
                GlobPrinter.init(repository)
                        .showOnly(DummyObject2.TYPE)
                        .exclude(DummyObject2.ID, DummyObject2.VALUE)
                        .toString();

        assertEquals("===== dummyObject2 ======" + LINE_SEPARATOR +
                "| label |" + LINE_SEPARATOR +
                "| a     |" + LINE_SEPARATOR +
                "" + LINE_SEPARATOR, actual);
    }

    @Test
    public void testLinkFieldsUseTheTargetName() throws Exception {
        repository.create(DummyObject.TYPE,
                value(DummyObject.ID, 1),
                value(DummyObject.NAME, "obj1"),
                value(DummyObject.LINK_ID, 2));
        repository.create(DummyObject.TYPE,
                value(DummyObject.ID, 2),
                value(DummyObject.NAME, "obj2"));

        checkRepositoryOutput("===== dummyObject ======" + LINE_SEPARATOR +
                "| id | name | value | count | present | date | password | linkId | link2Id |" + LINE_SEPARATOR +
                "| 1  | obj1 |       |       |         |      |          | 2      |         |" + LINE_SEPARATOR +
                "| 2  | obj2 |       |       |         |      |          |        |         |" + LINE_SEPARATOR +
                "" + LINE_SEPARATOR, DummyObject.TYPE);
    }

    @Test
    public void testSingleGlob() throws Exception {
        Glob glob = repository.create(DummyObject.TYPE,
                value(DummyObject.ID, 1),
                value(DummyObject.NAME, "obj1"),
                value(DummyObject.LINK_ID, 2));
        glob.getKey();

        checkOutput(glob,
                "===== dummyObject[id=1] ======" + Strings.LINE_SEPARATOR +
                        "| Field    | Value |" + Strings.LINE_SEPARATOR +
                        "| count    |       |" + Strings.LINE_SEPARATOR +
                        "| date     |       |" + Strings.LINE_SEPARATOR +
                        "| id       | 1     |" + Strings.LINE_SEPARATOR +
                        "| link2Id  |       |" + Strings.LINE_SEPARATOR +
                        "| linkId   | 2     |" + Strings.LINE_SEPARATOR +
                        "| name     | obj1  |" + Strings.LINE_SEPARATOR +
                        "| password |       |" + Strings.LINE_SEPARATOR +
                        "| present  |       |" + Strings.LINE_SEPARATOR +
                        "| value    |       |");
    }

    private void checkOutput(Glob glob, String expected) {
        StringWriter writer = new StringWriter();
        GlobPrinter.print(glob, writer);
        assertEquals(expected, writer.getBuffer().toString().trim());
    }

    private void checkOutput(String expected, GlobType... types) {
        checkRepositoryOutput(expected, types);
        assertEquals(expected, GlobPrinter.init(repository.getAll()).showOnly(types).toString());
    }

    private void checkRepositoryOutput(String expected, GlobType... types) {
        assertEquals(expected, GlobPrinter.init(repository).showOnly(types).toString());
    }

    private void createObj(int id, String name, int date) {
        repository.create(DummyObject.TYPE,
                value(DummyObject.ID, id),
                value(DummyObject.NAME, name),
                value(DummyObject.DATE, date));
    }

    private void createObj2(int id, String value) {
        repository.create(DummyObject2.TYPE,
                value(DummyObject2.ID, id),
                value(DummyObject2.LABEL, value));
    }
}
