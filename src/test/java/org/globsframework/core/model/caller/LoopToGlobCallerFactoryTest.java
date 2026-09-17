package org.globsframework.core.model.caller;

import org.globsframework.core.metamodel.DummyObject;
import org.globsframework.core.model.MutableGlob;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The fallback to-Glob callers, and what a generated one has to agree with : the key source drives the loop,
 * the endLoop value is tested before the dispatch, an unknown key goes to the fallback and, without one,
 * throws.
 * <p>
 * Everything here is written over the test's own two interfaces, which is the whole shape of this side now :
 * core names {@link KeySource} and nothing else.
 */
public class LoopToGlobCallerFactoryTest {

    /** The input : both what says the next key and what the functions read from, as a parser's is. */
    public static class Script implements KeySource {
        private final int endLoop;
        private final int[] calls;
        final List<String> trace = new ArrayList<>();
        private int next;

        /** Answers the script, then {@code endLoop} for ever — a parser that ran out of input. */
        Script(int endLoop, int... calls) {
            this.endLoop = endLoop;
            this.calls = calls;
        }

        public int nextKey() {
            return next < calls.length ? calls[next++] : endLoop;
        }
    }

    /** The caller's interface : what the code holding the loop wants to call. */
    public interface GlobReader {
        void read(MutableGlob data, Script input, String ctx);
    }

    /** The function's, written independently — note the method is not named like the caller's. */
    public interface FieldReader {
        void readField(MutableGlob data, Script input, String ctx);
    }

    private static final Class<?>[] ARGS = {MutableGlob.class, Script.class, String.class};

    private FieldReader record(String label) {
        return (glob, input, ctx) -> input.trace.add(label + "/" + ctx);
    }

    private SortedMap<Integer, FieldReader> functions(int... keys) {
        SortedMap<Integer, FieldReader> functions = new TreeMap<>();
        for (int key : keys) {
            functions.put(key, record("fn" + key));
        }
        return functions;
    }

    private GlobReader caller(SortedMap<Integer, FieldReader> functions, FieldReader fallback, int endLoop) {
        return LoopToGlobCallerFactory.INSTANCE.create("test", functions, fallback, endLoop,
                GlobReader.class, FieldReader.class, ARGS);
    }

    private List<String> call(GlobReader caller, Script input) {
        caller.read(DummyObject.TYPE.instantiate(), input, "ctx");
        return input.trace;
    }

    @Test
    public void callsWhatTheKeySourceAsksForInOrder() {
        GlobReader caller = caller(functions(-3, 0, 1, 100000), record("fallback"), -1);

        assertEquals(List.of("fn1/ctx", "fn-3/ctx", "fn100000/ctx", "fn1/ctx", "fn0/ctx"),
                call(caller, new Script(-1, 1, -3, 100000, 1, 0)));
    }

    /** The keys are sorted by the caller, not taken as the map iterates them. */
    @Test
    public void aMapWithItsOwnComparatorIsStillReadRight() {
        SortedMap<Integer, FieldReader> functions = new TreeMap<>(Comparator.reverseOrder());
        for (int key : new int[]{1, 5, 9, 12}) {
            functions.put(key, record("fn" + key));
        }

        assertEquals(List.of("fn9/ctx", "fn1/ctx", "fn12/ctx", "fn5/ctx"),
                call(caller(functions, record("fallback"), -1), new Script(-1, 9, 1, 12, 5)));
    }

    @Test
    public void anUnknownKeyGoesToTheFallback() {
        assertEquals(List.of("fallback/ctx", "fn1/ctx", "fallback/ctx"),
                call(caller(functions(1, 2), record("fallback"), -1), new Script(-1, 17, 1, -2)));
    }

    @Test
    public void anUnknownKeyWithoutAFallbackThrowsAndSaysWhich() {
        GlobReader caller = caller(functions(1, 2), null, -1);

        assertEquals(List.of("fn2/ctx"), call(caller, new Script(-1, 2)));
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> call(caller, new Script(-1, 2, 17)));
        assertTrue(exception.getMessage().contains("17"), exception.getMessage());
    }

    /** endLoop is tested before the dispatch : it ends the pass even when it is also a key. */
    @Test
    public void anEndLoopOfItsOwnShadowsTheKeyItEquals() {
        assertEquals(List.of("fn1/ctx", "fn2/ctx"),
                call(caller(functions(1, 2, 3), record("fallback"), 3), new Script(3, 1, 2, 3, 1)));
    }

    @Test
    public void noFunctionAtAllIsALoopThatOnlyWaitsForTheEnd() {
        assertEquals(List.of("fallback/ctx", "fallback/ctx"),
                call(caller(Collections.emptySortedMap(), record("fallback"), 0), new Script(0, 4, 9)));
    }

    /** The functions get the Glob and write into it — the point of the whole thing. */
    @Test
    public void theFunctionsWriteIntoTheGlobTheyAreHanded() {
        SortedMap<Integer, FieldReader> functions = new TreeMap<>();
        functions.put(0, (glob, input, ctx) -> glob.set(DummyObject.NAME, "a name"));
        functions.put(1, (glob, input, ctx) -> glob.set(DummyObject.COUNT, 12));

        MutableGlob glob = DummyObject.TYPE.instantiate();
        caller(functions, null, -1).read(glob, new Script(-1, 1, 0), "ctx");

        assertEquals("a name", glob.get(DummyObject.NAME));
        assertEquals(12, glob.get(DummyObject.COUNT).intValue());
    }

    /** What the functions throw is what the caller throws — no InvocationTargetException in the way. */
    @Test
    public void whatAFunctionThrowsComesOutAsItIs() {
        SortedMap<Integer, FieldReader> functions = new TreeMap<>();
        functions.put(1, (glob, input, ctx) -> {
            throw new IllegalStateException("boom");
        });
        IllegalStateException thrown = assertThrows(IllegalStateException.class,
                () -> call(caller(functions, null, -1), new Script(-1, 1)));

        assertEquals("boom", thrown.getMessage());
    }

    @Test
    public void readAllCallsEveryFunctionOnceInOrder() {
        GlobReader caller = LoopToGlobCallerFactory.INSTANCE.create("test",
                new FieldReader[]{record("a"), record("b"), record("c")},
                GlobReader.class, FieldReader.class, ARGS);

        assertEquals(List.of("a/ctx", "b/ctx", "c/ctx"), call(caller, new Script(-1)));
    }

    @Test
    public void aMissingFunctionIsRefusedWhenTheCallerIsBuilt() {
        SortedMap<Integer, FieldReader> functions = functions(1, 2);
        functions.put(3, null);
        assertThrows(IllegalArgumentException.class, () -> caller(functions, null, -1));
        assertThrows(IllegalArgumentException.class, () -> LoopToGlobCallerFactory.INSTANCE
                .create("test", new FieldReader[]{record("a"), null}, GlobReader.class, FieldReader.class,
                        ARGS));
    }

    /** The loop drives its pass with the one argument that is a KeySource, and refuses to guess. */
    @Test
    public void theDispatchingShapeNeedsExactlyOneKeySourceAmongItsArguments() {
        assertThrows(IllegalArgumentException.class,
                () -> ToGlobCallerFactory.keySourceIndex(MutableGlob.class, String.class));
        assertThrows(IllegalArgumentException.class,
                () -> ToGlobCallerFactory.keySourceIndex(Script.class, Script.class));
        assertEquals(1, ToGlobCallerFactory.keySourceIndex(MutableGlob.class, Script.class, String.class));
    }

    /**
     * The name is what a generating implementation names its emitted class after, so that the class is the
     * same one from one run to the next. Refused here as well as there : the loop has no class to name, but
     * a parser must not be able to get away with a name on one JVM and not on another.
     */
    @Test
    public void aCallerWithoutANameIsRefusedEvenThoughTheLoopWouldNotUseIt() {
        assertThrows(IllegalArgumentException.class,
                () -> LoopToGlobCallerFactory.INSTANCE.create(null, functions(1, 2), null, -1,
                        GlobReader.class, FieldReader.class, ARGS));
        assertThrows(IllegalArgumentException.class,
                () -> LoopToGlobCallerFactory.INSTANCE.create("  ", functions(1, 2), null, -1,
                        GlobReader.class, FieldReader.class, ARGS));
        assertThrows(IllegalArgumentException.class, () -> LoopToGlobCallerFactory.INSTANCE
                .create(null, new FieldReader[]{record("a")}, GlobReader.class, FieldReader.class, ARGS));
    }

    /** Nothing installed : the loop, and a parser that never has to know. */
    @Test
    public void withNoServiceGetAnswersTheLoop() {
        assertNull(ToGlobCallerFactory.generated());
        assertSame(LoopToGlobCallerFactory.INSTANCE, ToGlobCallerFactory.get());
    }

    /**
     * The extension point : with a ToGlobCallerService installed, get stops answering the loop. Core
     * has no implementation of its own — globs-generate's is the one that emits the switch — so the test
     * installs a stand-in and only checks the wiring.
     */
    @Test
    public void anInstalledServiceIsPreferredToTheLoop() {
        System.setProperty("globs.caller.toGlob", StandInService.class.getName());
        ToGlobCallerService.Builder.reset();
        try {
            assertInstanceOf(StandIn.class, ToGlobCallerFactory.get());
            assertInstanceOf(StandIn.class, ToGlobCallerFactory.generated());
        } finally {
            System.clearProperty("globs.caller.toGlob");
            ToGlobCallerService.Builder.reset();
        }
        assertSame(LoopToGlobCallerFactory.INSTANCE, ToGlobCallerFactory.get());
    }

    /** "nothing to offer" is a null, and the loop takes over — it is not an error to report. */
    @Test
    public void aServiceThatAnswersNullFallsBackToTheLoop() {
        System.setProperty("globs.caller.toGlob", AbstainingService.class.getName());
        ToGlobCallerService.Builder.reset();
        try {
            assertSame(LoopToGlobCallerFactory.INSTANCE, ToGlobCallerFactory.get());
            assertNull(ToGlobCallerFactory.generated());
        } finally {
            System.clearProperty("globs.caller.toGlob");
            ToGlobCallerService.Builder.reset();
        }
    }

    /** an explicitly asked for service that cannot be loaded is a misconfiguration, not a slow path */
    @Test
    public void anUnloadableServiceThrowsRatherThanDegradingSilently() {
        System.setProperty("globs.caller.toGlob", "not.a.Class");
        try {
            assertThrows(RuntimeException.class, ToGlobCallerService.Builder::reset);
        } finally {
            System.clearProperty("globs.caller.toGlob");
            ToGlobCallerService.Builder.reset();
        }
    }

    public static class StandInService implements ToGlobCallerService {
        public ToGlobCallerFactory factory() {
            return new StandIn();
        }
    }

    public static class AbstainingService implements ToGlobCallerService {
        public ToGlobCallerFactory factory() {
            return null;
        }
    }

    public static class StandIn implements ToGlobCallerFactory {
        public <T, D> T create(String name, SortedMap<Integer, D> functions, D fallback, int endLoop,
                               Class<T> tClass, Class<D> dClass, Class<?>... argument) {
            throw new UnsupportedOperationException();
        }

        public <T, D> T create(String name, D[] functions, Class<T> tClass, Class<D> dClass,
                               Class<?>... argument) {
            throw new UnsupportedOperationException();
        }
    }
}
