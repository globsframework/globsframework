package org.globsframework.core.model.caller;

import org.globsframework.core.metamodel.DummyObject;
import org.globsframework.core.metamodel.DummyObject2;
import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.MutableGlob;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The fallback caller, and what a generated one has to agree with : every field is called, in index order,
 * with isSet from the Glob and isNull meaning "getValue answers null" — so an untouched field comes out
 * not set, null, and with no value, while an explicit null is set and null.
 * <p>
 * Everything is over the test's own two interfaces, which is the only shape there is : core supplies the
 * fixed head of each method — the Glob for the caller, isSet / isNull / the value for the functions — and
 * the codec supplies the rest.
 */
public class LoopFromGlobCallerTest {

    private record Seen(String field, boolean isSet, boolean isNull, Object value, Object ctx2) {
    }

    /** The codec's caller : the Glob first, then whatever the pass carries. */
    public interface GlobWalk {
        void walk(Glob data, List<Seen> seen, String ctx2);
    }

    /** The codec's function, written independently — isSet, isNull, the value, then the same pass. */
    public interface FieldWalk {
        void onField(boolean isSet, boolean isNull, Object value, List<Seen> seen, String ctx2);
    }

    private static final Class<?>[] ARGS = {List.class, String.class};

    private static FromGlobCallerFactory.Functions<FieldWalk> recorder() {
        return field -> {
            String name = field.getName();
            return (isSet, isNull, value, seen, ctx2) -> seen.add(new Seen(name, isSet, isNull, value, ctx2));
        };
    }

    private static GlobWalk caller(GlobType type, Field[] order) {
        return FromGlobCallerFactory.callerFor("test", type, recorder(), order, GlobWalk.class,
                FieldWalk.class, ARGS);
    }

    private List<Seen> call(MutableGlob glob) {
        return call(glob, null);
    }

    private List<Seen> call(MutableGlob glob, Field[] order) {
        List<Seen> seen = new ArrayList<>();
        caller(glob.getType(), order).walk(glob, seen, "ctx2");
        return seen;
    }

    private static List<String> names(List<Seen> seen) {
        return seen.stream().map(Seen::field).collect(Collectors.toList());
    }

    /** What "this one is the loop" means now : a Proxy, core having no class to emit. */
    private static boolean looped(Object caller) {
        return Proxy.isProxyClass(caller.getClass());
    }

    @Test
    public void everyFieldIsCalledInIndexOrder() {
        MutableGlob glob = DummyObject.TYPE.instantiate();
        List<Seen> seen = call(glob);
        assertEquals(
                Arrays.stream(DummyObject.TYPE.getFields()).map(Field::getName).collect(Collectors.toList()),
                names(seen));
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

    /**
     * A format whose layout is not the type's declaration order gives its own : the caller writes as it
     * walks, so reordering afterwards is not something a codec can do.
     */
    @Test
    public void theOrderGivenIsTheOrderTheFieldsAreCalledIn() {
        MutableGlob glob = DummyObject.TYPE.instantiate();
        glob.set(DummyObject.NAME, "a name");

        List<Seen> seen = call(glob, new Field[]{DummyObject.COUNT, DummyObject.NAME, DummyObject.ID});

        assertEquals(List.of("count", "name", "id"), names(seen));
        assertEquals(new Seen("name", true, false, "a name", "ctx2"), of(seen, "name"));
        assertEquals(new Seen("count", false, true, null, "ctx2"), of(seen, "count"));
    }

    /** An order may name fewer fields than the type has : the others are never asked for, never called. */
    @Test
    public void aFieldLeftOutOfTheOrderIsNotCalledAtAll() {
        MutableGlob glob = DummyObject.TYPE.instantiate();
        List<String> asked = new ArrayList<>();
        FromGlobCallerFactory.Functions<FieldWalk> functions = field -> {
            asked.add(field.getName());
            String name = field.getName();
            return (isSet, isNull, value, seen, ctx2) -> seen.add(new Seen(name, isSet, isNull, value, ctx2));
        };
        List<Seen> seen = new ArrayList<>();
        FromGlobCallerFactory.callerFor("test", DummyObject.TYPE, functions, new Field[]{DummyObject.NAME},
                GlobWalk.class, FieldWalk.class, ARGS).walk(glob, seen, "ctx2");

        assertEquals(List.of("name"), names(seen));
        assertEquals(List.of("name"), asked);
    }

    /** Refused when the caller is built, not when it runs — and the same way whoever builds it. */
    @Test
    public void anOrderThatIsNotAPermutationOfTheTypeIsRefused() {
        assertThrows(IllegalArgumentException.class, () ->
                FromGlobCallerFactory.fieldsToCall(DummyObject.TYPE,
                        new Field[]{DummyObject.NAME, DummyObject.NAME}));
        assertThrows(IllegalArgumentException.class, () ->
                FromGlobCallerFactory.fieldsToCall(DummyObject.TYPE, new Field[]{null}));
        assertThrows(IllegalArgumentException.class, () ->
                FromGlobCallerFactory.fieldsToCall(DummyObject.TYPE, new Field[]{DummyObject2.ID}));
    }

    /** No order means every field, in index order : what every caller did before there was one. */
    @Test
    public void noOrderIsEveryFieldInIndexOrder() {
        assertArrayEquals(DummyObject.TYPE.getFields(),
                FromGlobCallerFactory.fieldsToCall(DummyObject.TYPE, null));
    }

    /** The array is copied : what the caller walks cannot change under it. */
    @Test
    public void theOrderIsCopied() {
        Field[] order = {DummyObject.NAME, DummyObject.COUNT};
        Field[] fields = FromGlobCallerFactory.fieldsToCall(DummyObject.TYPE, order);
        order[0] = DummyObject.ID;
        assertSame(DummyObject.NAME, fields[0]);
    }

    /**
     * The two methods are found by their parameters, whatever they are named — core only fixes the head of
     * each list, the Glob on one side and isSet / isNull / the value on the other.
     */
    @Test
    public void theMethodsAreFoundByTheirParametersWhateverTheyAreCalled() {
        assertEquals("walk", FromGlobCallerFactory.callerMethod(GlobWalk.class, ARGS).getName());
        assertEquals("onField", FromGlobCallerFactory.functionMethod(FieldWalk.class, ARGS).getName());
        // the pass this caller carries is not the one these interfaces declare
        assertThrows(IllegalArgumentException.class,
                () -> FromGlobCallerFactory.callerMethod(GlobWalk.class, List.class));
        assertThrows(IllegalArgumentException.class,
                () -> FromGlobCallerFactory.callerMethod(String.class, ARGS));
    }

    @Test
    public void aTypeWithNoGeneratingFactoryFallsBackToTheLoopedCaller() {
        assertFalse(DummyObject.TYPE.getGlobFactory() instanceof CallerGlobFactory);
        assertTrue(looped(caller(DummyObject.TYPE, null)));
    }

    /**
     * The extension point : with a FromGlobCallerService installed, callerFor stops answering the loop for a
     * type core builds itself. Core has no implementation of its own — globs-generate's is the one that
     * generates over a DefaultGlob — so the test installs a stand-in and only checks the wiring.
     */
    @Test
    public void anInstalledServiceIsPreferredToTheLoop() {
        System.setProperty("globs.caller.fromGlob", StandInService.class.getName());
        FromGlobCallerService.Builder.reset();
        try {
            assertInstanceOf(StandInCaller.class, caller(DummyObject.TYPE, null));
        } finally {
            System.clearProperty("globs.caller.fromGlob");
            FromGlobCallerService.Builder.reset();
        }
        assertTrue(looped(caller(DummyObject.TYPE, null)));
    }

    /**
     * generatedCallerFor is the same resolution without the loop at the end : null means "nobody can generate
     * this", which is what a codec with a better fallback of its own needs to hear.
     */
    @Test
    public void generatedCallerForSaysNullRatherThanAnsweringTheLoop() {
        assertNull(FromGlobCallerFactory.generatedCallerFor("test", DummyObject.TYPE, recorder(), null,
                GlobWalk.class, FieldWalk.class, ARGS));

        System.setProperty("globs.caller.fromGlob", StandInService.class.getName());
        FromGlobCallerService.Builder.reset();
        try {
            assertInstanceOf(StandInCaller.class,
                    FromGlobCallerFactory.generatedCallerFor("test", DummyObject.TYPE, recorder(), null,
                            GlobWalk.class, FieldWalk.class, ARGS));
        } finally {
            System.clearProperty("globs.caller.fromGlob");
            FromGlobCallerService.Builder.reset();
        }
    }

    /** "not mine" is a null, and the loop takes over — it is not an error to report. */
    @Test
    public void aServiceThatDoesNotKnowTheTypeFallsBackToTheLoop() {
        System.setProperty("globs.caller.fromGlob", AbstainingService.class.getName());
        FromGlobCallerService.Builder.reset();
        try {
            assertTrue(looped(caller(DummyObject.TYPE, null)));
        } finally {
            System.clearProperty("globs.caller.fromGlob");
            FromGlobCallerService.Builder.reset();
        }
    }

    /** an explicitly asked for service that cannot be loaded is a misconfiguration, not a slow path */
    @Test
    public void anUnloadableServiceThrowsRatherThanDegradingSilently() {
        System.setProperty("globs.caller.fromGlob", "not.a.Class");
        try {
            assertThrows(RuntimeException.class, FromGlobCallerService.Builder::reset);
        } finally {
            System.clearProperty("globs.caller.fromGlob");
            FromGlobCallerService.Builder.reset();
        }
    }

    public static class StandInService implements FromGlobCallerService {
        public FromGlobCallerFactory factoryFor(GlobType type) {
            return new FromGlobCallerFactory() {
                @SuppressWarnings("unchecked")
                public <T, D> T create(String name, Functions<D> functions, Field[] order,
                                       Class<T> tClass, Class<D> dClass, Class<?>... argument) {
                    return (T) new StandInCaller();
                }
            };
        }
    }

    public static class AbstainingService implements FromGlobCallerService {
        public FromGlobCallerFactory factoryFor(GlobType type) {
            return null;
        }
    }

    static class StandInCaller implements GlobWalk {
        public void walk(Glob data, List<Seen> seen, String ctx2) {
        }
    }

    @Test
    public void aMissingFunctionIsRefusedAtBuildTimeRatherThanNPEingPerGlob() {
        assertThrows(IllegalArgumentException.class,
                () -> new LoopFromGlobCallerFactory(DummyObject.TYPE).create("test", field -> null, null,
                        GlobWalk.class, FieldWalk.class, ARGS));
    }

    /**
     * The name is what a generating implementation names its emitted class after, so that the class is the
     * same one from one run to the next. Refused here as well as there, so that a JVM with nothing installed
     * to generate does not let through a name that a JVM which generates would need.
     */
    @Test
    public void aCallerWithoutANameIsRefusedEvenThoughTheLoopWouldNotUseIt() {
        assertThrows(IllegalArgumentException.class, () -> caller0(null));
        assertThrows(IllegalArgumentException.class, () -> caller0("  "));
        assertThrows(IllegalArgumentException.class,
                () -> FromGlobCallerFactory.generatedCallerFor(null, DummyObject.TYPE, recorder(), null,
                        GlobWalk.class, FieldWalk.class, ARGS));
    }

    private static GlobWalk caller0(String name) {
        return FromGlobCallerFactory.callerFor(name, DummyObject.TYPE, recorder(), null, GlobWalk.class,
                FieldWalk.class, ARGS);
    }

    private Seen of(List<Seen> seen, String field) {
        return seen.stream().filter(s -> s.field().equals(field)).findFirst()
                .orElseThrow(() -> new AssertionError(field + " was not called"));
    }
}
