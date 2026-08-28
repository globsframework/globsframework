package org.globsframework.core.utils;

import org.globsframework.core.metamodel.DummyObject;
import org.globsframework.core.metamodel.DummyObjectInner;
import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.GlobTypeBuilderFactory;
import org.globsframework.core.metamodel.annotations.*;
import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.metamodel.index.MultiFieldNotUniqueIndex;
import org.globsframework.core.metamodel.index.NotUniqueIndex;
import org.globsframework.core.metamodel.index.UniqueIndex;
import org.globsframework.core.model.Glob;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class GenerateBuilderTest {

    @Test
    public void generateASimpleType() {
        String source = GenerateBuilder.generate("org.globsframework.core.metamodel", "DummyObject", DummyObject.TYPE);
        assertEquals("""
                package org.globsframework.core.metamodel;

                import org.globsframework.core.metamodel.annotations.AutoIncrement;
                import org.globsframework.core.metamodel.annotations.KeyField;
                import org.globsframework.core.metamodel.annotations.NamingField;
                import org.globsframework.core.metamodel.fields.BooleanField;
                import org.globsframework.core.metamodel.fields.BytesField;
                import org.globsframework.core.metamodel.fields.DoubleField;
                import org.globsframework.core.metamodel.fields.IntegerField;
                import org.globsframework.core.metamodel.fields.StringField;
                import org.globsframework.core.metamodel.index.NotUniqueIndex;

                public class DummyObject {
                    public static final GlobType TYPE;

                    public static final IntegerField ID;
                    public static final StringField NAME;
                    public static final DoubleField VALUE;
                    public static final IntegerField COUNT;
                    public static final BooleanField PRESENT;
                    public static final IntegerField DATE;
                    public static final BytesField PASSWORD;
                    public static final IntegerField LINK_ID;
                    public static final IntegerField LINK2_ID;
                    public static final NotUniqueIndex DATE_INDEX;

                    static {
                        GlobTypeBuilder typeBuilder = GlobTypeBuilderFactory.create("dummyObject");
                        ID = typeBuilder.declareIntegerField("id", AutoIncrement.INSTANCE, KeyField.ZERO);
                        NAME = typeBuilder.declareStringField("name", NamingField.UNIQUE_GLOB);
                        VALUE = typeBuilder.declareDoubleField("value");
                        COUNT = typeBuilder.declareIntegerField("count");
                        PRESENT = typeBuilder.declareBooleanField("present");
                        DATE = typeBuilder.declareIntegerField("date");
                        PASSWORD = typeBuilder.declareBytesField("password");
                        LINK_ID = typeBuilder.declareIntegerField("linkId");
                        LINK2_ID = typeBuilder.declareIntegerField("link2Id");
                        DATE_INDEX = typeBuilder.addNotUniqueIndex("dateIndex", DATE);
                        TYPE = typeBuilder.build();
                    }
                }
                """, source);
    }

    @Test
    public void annotationsAreRebuiltWithTheirOwnClass() {
        String source = GenerateBuilder.generate("some.pack", "Rich", RichType.TYPE);

        // a public static Glob constant with the same values
        assertTrue(source.contains("KeyField.ZERO"), source);
        assertTrue(source.contains("Required.UNIQUE_GLOB"), source);
        // a static createXXX(...) that gives back the same values
        assertTrue(source.contains("MaxSize.create(255)"), source);
        assertTrue(source.contains("MaxSize.create(12, true)"), source);
        assertTrue(source.contains("DefaultString.create(\"a default\")"), source);
        assertTrue(source.contains("EnumAnnotation.create(new String[]{\"a\", \"b\"})"), source);
        // no factory : generic instantiation
        assertTrue(source.contains("Comment.create(\"a comment\")"), source);
        // and the imports that go with them
        assertTrue(source.contains("import org.globsframework.core.metamodel.annotations.MaxSize;"), source);
        assertTrue(source.contains("import org.globsframework.core.metamodel.annotations.Comment;"), source);
        // the type level annotation
        assertTrue(source.contains("typeBuilder.addAnnotation(Comment.create(\"the type\"))"),
                source);
    }

    @Test
    public void generatedSourceCompilesAndRebuildsTheSameType() throws Exception {
        assertSameType(RichType.TYPE, compileAndBuild("Rich", RichType.TYPE));
        assertSameType(DummyObject.TYPE, compileAndBuild("Dummy", DummyObject.TYPE));
    }

    @Test
    public void unknownTypeIsReported() {
        GlobType unknown = GlobTypeBuilderFactory.create("aTypeThatHasNoJavaClass").build();
        GlobTypeBuilder builder = GlobTypeBuilderFactory.create("useIt");
        builder.declareStringField("name", unknown.instantiate());
        String source = GenerateBuilder.generate("some.pack", "UseIt", builder.build());
        assertTrue(source.contains("// unresolved type(s) : aTypeThatHasNoJavaClass"), source);
        assertTrue(source.contains("TODO unknown java class for annotation aTypeThatHasNoJavaClass"), source);
    }

    @Test
    public void constantNames() {
        assertEquals("ID", GenerateBuilder.toConstantName("id"));
        assertEquals("LINK_ID", GenerateBuilder.toConstantName("linkId"));
        assertEquals("BYTE_ARRAY_DATA", GenerateBuilder.toConstantName("byteArrayData"));
        assertEquals("ALREADY_A_CONSTANT", GenerateBuilder.toConstantName("ALREADY_A_CONSTANT"));
        assertEquals("A_NAME_WITH_SPACE", GenerateBuilder.toConstantName("a name with space"));
        assertEquals("_1_FIRST", GenerateBuilder.toConstantName("1First"));
    }

    // ------------------------------------------------------------------ tools

    private GlobType compileAndBuild(String className, GlobType globType) throws Exception {
        String packageName = "org.globsframework.generated";
        String source = GenerateBuilder.generate(packageName, className, globType);
        Path directory = Files.createTempDirectory("globs-generate-builder");
        try {
            Path sourceFile = directory.resolve(className + ".java");
            Files.writeString(sourceFile, source, StandardCharsets.UTF_8);
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            assertNotNull(compiler, "test must run on a JDK");
            int result = compiler.run(null, null, null, "-classpath", classpath(),
                    "-d", directory.toString(), sourceFile.toString());
            assertEquals(0, result, "generated source does not compile :\n" + source);
            try (URLClassLoader loader = new URLClassLoader(new URL[]{directory.toUri().toURL()},
                    getClass().getClassLoader())) {
                Class<?> compiled = loader.loadClass(packageName + "." + className);
                return (GlobType) compiled.getField("TYPE").get(null);
            }
        } finally {
            try (var paths = Files.walk(directory)) {
                paths.sorted(java.util.Comparator.reverseOrder()).forEach(path -> path.toFile().delete());
            }
        }
    }

    private String classpath() {
        Set<String> entries = new LinkedHashSet<>();
        for (Class<?> aClass : List.of(GlobType.class, GenerateBuilderTest.class)) {
            entries.add(new File(aClass.getProtectionDomain().getCodeSource().getLocation().getPath()).getAbsolutePath());
        }
        return String.join(File.pathSeparator, entries);
    }

    private void assertSameType(GlobType expected, GlobType actual) {
        assertEquals(expected.getName(), actual.getName());
        assertSameAnnotations(expected.getName(), expected.streamAnnotations().toList(), actual.streamAnnotations().toList());
        assertEquals(Arrays.stream(expected.getFields()).map(Field::getName).toList(),
                Arrays.stream(actual.getFields()).map(Field::getName).toList());
        for (Field field : expected.getFields()) {
            Field other = actual.getField(field.getName());
            assertEquals(field.getDataType(), other.getDataType(), field.getName());
            assertEquals(field.isKeyField(), other.isKeyField(), field.getName());
            if (field instanceof GlobField<?> globField) {
                assertSame(globField.getTargetType(), ((GlobField<?>) other).getTargetType(), field.getName());
            }
            if (field instanceof GlobUnionField unionField) {
                assertEquals(unionField.getTargetTypes().stream().map(GlobType::getName).toList(),
                        ((GlobUnionField) other).getTargetTypes().stream().map(GlobType::getName).toList(),
                        field.getName());
            }
            assertSameAnnotations(field.getFullName(), field.streamAnnotations().toList(), other.streamAnnotations().toList());
        }
        assertEquals(expected.getIndices().stream().map(index -> index.getName() + index.fields()
                        .map(Field::getName).toList()).collect(Collectors.toSet()),
                actual.getIndices().stream().map(index -> index.getName() + index.fields()
                        .map(Field::getName).toList()).collect(Collectors.toSet()));
    }

    private void assertSameAnnotations(String on, List<Glob> expected, List<Glob> actual) {
        assertEquals(expected.size(), actual.size(), on + " : " + expected + " / " + actual);
        for (Glob annotation : expected) {
            assertTrue(actual.stream().anyMatch(other -> GenerateBuilder.sameValues(annotation, other)),
                    on + " : missing " + annotation + " in " + actual);
        }
    }

    // ------------------------------------------------------------------ a type with many annotations

    public static class RichType {
        public static final GlobType TYPE;

        public static final IntegerField ID;
        public static final StringField NAME;
        public static final StringField SHORT_NAME;
        public static final StringField COMMENTED;
        public static final StringField ENUM_VALUE;
        public static final DoubleField AMOUNT;
        public static final LongField COUNT;
        public static final BooleanField PRESENT;
        public static final DateField DAY;
        public static final DateTimeField MOMENT;
        public static final BytesField DATA;
        public static final StringArrayField TAGS;
        public static final IntegerArrayField NUMBERS;
        public static final GlobField<DummyObjectInner> INNER;
        public static final GlobArrayField<DummyObjectInner> INNERS;
        public static final GlobUnionField UNION;
        public static final UniqueIndex NAME_INDEX;
        public static final MultiFieldNotUniqueIndex MULTI_INDEX;

        static {
            GlobTypeBuilder typeBuilder = GlobTypeBuilderFactory.create("richType");
            typeBuilder.addAnnotation(Comment.TYPE.instantiate().set(Comment.VALUE, "the type"));
            ID = typeBuilder.declareIntegerField("id", KeyField.ZERO, AutoIncrement.INSTANCE);
            NAME = typeBuilder.declareStringField("name", Required.UNIQUE_GLOB, NamingField.UNIQUE_GLOB,
                    MaxSize.create(255), DefaultString.create("a default"));
            SHORT_NAME = typeBuilder.declareStringField("shortName", MaxSize.create(12, true),
                    FieldName.create("short_name"));
            COMMENTED = typeBuilder.declareStringField("commented",
                    Comment.TYPE.instantiate().set(Comment.VALUE, "a comment"));
            ENUM_VALUE = typeBuilder.declareStringField("enumValue", EnumAnnotation.create(new String[]{"a", "b"}));
            AMOUNT = typeBuilder.declareDoubleField("amount", DoublePrecision.create(4), DefaultDouble.create(1.5));
            COUNT = typeBuilder.declareLongField("count", DefaultLong.create(3L));
            PRESENT = typeBuilder.declareBooleanField("present", DefaultBoolean.create(true));
            DAY = typeBuilder.declareDateField("day", IsDate.UNIQUE);
            MOMENT = typeBuilder.declareDateTimeField("moment", IsDateTime.UNIQUE);
            DATA = typeBuilder.declareBytesField("data");
            TAGS = typeBuilder.declareStringArrayField("tags");
            NUMBERS = typeBuilder.declareIntegerArrayField("numbers");
            INNER = typeBuilder.declareGlobField("inner", () -> DummyObjectInner.TYPE);
            INNERS = typeBuilder.declareGlobArrayField("inners", () -> DummyObjectInner.TYPE);
            UNION = typeBuilder.declareGlobUnionField("union",
                    new Supplier[]{() -> DummyObjectInner.TYPE, () -> DummyObject.TYPE});
            NAME_INDEX = typeBuilder.addUniqueIndex("nameIndex", NAME);
            MULTI_INDEX = typeBuilder.addMultiFieldNotUniqueIndex("multiIndex", NAME, COUNT);
            TYPE = typeBuilder.build();
        }
    }
}
