package org.globsframework.core.model.generate;

import org.globsframework.core.metamodel.DummyObject;
import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.MutableGlob;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The fallback caller, and what a generated one has to agree with : every field is called, in index order,
 * with isSet from the Glob and isNull meaning "getValue answers null" — so an untouched field comes out
 * not set, null, and with no value, while an explicit null is set and null.
 */
public class DefaultFunctionCallerTest {

    private record Seen(String field, boolean isSet, boolean isNull, Object value, Object ctx2) {
    }

    private static GenerateCaller.GetFieldValueFunction<List<Seen>, String> recorder() {
        return new GenerateCaller.GetFieldValueFunction<>() {
            public <T> FieldValueFunction<T, List<Seen>, String> create(Field field) {
                String name = field.getName();
                return (isSet, isNull, value, ctx1, ctx2) -> ctx1.add(new Seen(name, isSet, isNull, value, ctx2));
            }
        };
    }

    private List<Seen> call(MutableGlob glob) {
        List<Seen> seen = new ArrayList<>();
        GenerateCaller.callerFor(glob.getType(), recorder()).call(glob, seen, "ctx2");
        return seen;
    }

    @Test
    public void everyFieldIsCalledInIndexOrder() {
        MutableGlob glob = DummyObject.TYPE.instantiate();
        List<Seen> seen = call(glob);
        assertEquals(
                Arrays.stream(DummyObject.TYPE.getFields()).map(Field::getName).collect(Collectors.toList()),
                seen.stream().map(Seen::field).collect(Collectors.toList()));
    }

    @Test
    public void aValueAnExplicitNullAndAnUntouchedFieldAreThreeDifferentThings() {
        MutableGlob glob = DummyObject.TYPE.instantiate();
        glob.set(DummyObject.NAME, "a name");
        glob.setValue(DummyObject.COUNT, null);

        List<Seen> seen = call(glob);
        assertEquals(new Seen("name", true, false, "a name", "ctx2"), of(seen, "name"));
        assertEquals(new Seen("count", true, true, null, "ctx2"), of(seen, "count"));
        assertEquals(new Seen("value", false, true, null, "ctx2"), of(seen, "value"));
    }

    @Test
    public void aTypeWithNoGeneratingFactoryFallsBackToTheLoopedCaller() {
        assertFalse(DummyObject.TYPE.getGlobFactory() instanceof GlobGenerateFactory);
        assertInstanceOf(DefaultFunctionCaller.class,
                GenerateCaller.callerFor(DummyObject.TYPE, recorder()));
    }

    /**
     * The extension point : with a GenerateCallerService installed, callerFor stops answering the loop for a
     * type core builds itself. Core has no implementation of its own — globs-generate's is the one that
     * generates over a DefaultGlob — so the test installs a stand-in and only checks the wiring.
     */
    @Test
    public void anInstalledServiceIsPreferredToTheLoop() {
        System.setProperty("globs.caller", StandInService.class.getName());
        GenerateCallerService.Builder.reset();
        try {
            assertInstanceOf(StandInCaller.class, GenerateCaller.callerFor(DummyObject.TYPE, recorder()));
        } finally {
            System.clearProperty("globs.caller");
            GenerateCallerService.Builder.reset();
        }
        assertInstanceOf(DefaultFunctionCaller.class, GenerateCaller.callerFor(DummyObject.TYPE, recorder()));
    }

    /**
     * generatedCallerFor is the same resolution without the loop at the end : null means "nobody can generate
     * this", which is what a codec with a better fallback of its own needs to hear.
     */
    @Test
    public void generatedCallerForSaysNullRatherThanAnsweringTheLoop() {
        assertNull(GenerateCaller.generatedCallerFor(DummyObject.TYPE, recorder()));

        System.setProperty("globs.caller", StandInService.class.getName());
        GenerateCallerService.Builder.reset();
        try {
            assertInstanceOf(StandInCaller.class,
                    GenerateCaller.generatedCallerFor(DummyObject.TYPE, recorder()));
        } finally {
            System.clearProperty("globs.caller");
            GenerateCallerService.Builder.reset();
        }
    }

    /** "not mine" is a null, and the loop takes over — it is not an error to report. */
    @Test
    public void aServiceThatDoesNotKnowTheTypeFallsBackToTheLoop() {
        System.setProperty("globs.caller", AbstainingService.class.getName());
        GenerateCallerService.Builder.reset();
        try {
            assertInstanceOf(DefaultFunctionCaller.class, GenerateCaller.callerFor(DummyObject.TYPE, recorder()));
        } finally {
            System.clearProperty("globs.caller");
            GenerateCallerService.Builder.reset();
        }
    }

    /** an explicitly asked for service that cannot be loaded is a misconfiguration, not a slow path */
    @Test
    public void anUnloadableServiceThrowsRatherThanDegradingSilently() {
        System.setProperty("globs.caller", "not.a.Class");
        try {
            assertThrows(RuntimeException.class, GenerateCallerService.Builder::reset);
        } finally {
            System.clearProperty("globs.caller");
            GenerateCallerService.Builder.reset();
        }
    }

    public static class StandInService implements GenerateCallerService {
        public GenerateCaller getGenerateCaller(GlobType type) {
            return new GenerateCaller() {
                public <D, E> GeneratedFunctionCaller<D, E> create(GetFieldValueFunction<D, E> functions) {
                    return new StandInCaller<>();
                }
            };
        }
    }

    public static class AbstainingService implements GenerateCallerService {
        public GenerateCaller getGenerateCaller(GlobType type) {
            return null;
        }
    }

    static class StandInCaller<D, E> implements GeneratedFunctionCaller<D, E> {
        public void call(Glob data, D ctx1, E ctx2) {
        }
    }

    @Test
    public void aMissingFunctionIsRefusedAtBuildTimeRatherThanNPEingPerGlob() {
        assertThrows(IllegalArgumentException.class, () -> new DefaultFunctionCaller<>(DummyObject.TYPE,
                new GenerateCaller.GetFieldValueFunction<Object, Object>() {
                    public <T> FieldValueFunction<T, Object, Object> create(Field field) {
                        return null;
                    }
                }));
    }

    private Seen of(List<Seen> seen, String field) {
        return seen.stream().filter(s -> s.field().equals(field)).findFirst()
                .orElseThrow(() -> new AssertionError(field + " was not called"));
    }
}
