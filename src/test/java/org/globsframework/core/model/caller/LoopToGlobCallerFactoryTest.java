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
 * The fallback to-Glob callers, and what a generated one has to agree with : the CallAt drives the loop, the
 * endLoop value is tested before the dispatch, an unknown key goes to the fallback and, without one, throws.
 */
public class LoopToGlobCallerFactoryTest {

    /** ctx1 is the trace, so what it collects is the proof the three contexts were forwarded. */
    private ToGlobFunction<List<String>, String, String> record(String label) {
        return (glob, trace, ctx2, ctx3) -> trace.add(label + "/" + ctx2 + "/" + ctx3);
    }

    /** Answers the script, then {@code endLoop} for ever — a parser that ran out of input. */
    private KeySource script(int endLoop, int... calls) {
        return new KeySource() {
            int next = 0;

            public int nextKey() {
                return next < calls.length ? calls[next++] : endLoop;
            }
        };
    }

    private SortedMap<Integer, ToGlobFunction<List<String>, String, String>> functions(int... keys) {
        SortedMap<Integer, ToGlobFunction<List<String>, String, String>> functions = new TreeMap<>();
        for (int key : keys) {
            functions.put(key, record("fn" + key));
        }
        return functions;
    }

    private List<String> call(ToGlobCaller<List<String>, String, String> caller, KeySource keySource) {
        List<String> trace = new ArrayList<>();
        caller.call(keySource, DummyObject.TYPE.instantiate(), trace, "c2", "c3");
        return trace;
    }

    @Test
    public void callsWhatTheCallAtAsksForInOrder() {
        ToGlobCaller<List<String>, String, String> caller = LoopToGlobCallerFactory.INSTANCE
                .create("test", functions(-3, 0, 1, 100000), record("fallback"), -1);

        assertEquals(List.of("fn1/c2/c3", "fn-3/c2/c3", "fn100000/c2/c3", "fn1/c2/c3", "fn0/c2/c3"),
                call(caller, script(-1, 1, -3, 100000, 1, 0)));
    }

    /** The keys are sorted by the caller, not taken as the map iterates them. */
    @Test
    public void aMapWithItsOwnComparatorIsStillReadRight() {
        SortedMap<Integer, ToGlobFunction<List<String>, String, String>> functions =
                new TreeMap<>(Comparator.reverseOrder());
        for (int key : new int[]{1, 5, 9, 12}) {
            functions.put(key, record("fn" + key));
        }
        ToGlobCaller<List<String>, String, String> caller =
                LoopToGlobCallerFactory.INSTANCE.create("test", functions, record("fallback"), -1);

        assertEquals(List.of("fn9/c2/c3", "fn1/c2/c3", "fn12/c2/c3", "fn5/c2/c3"),
                call(caller, script(-1, 9, 1, 12, 5)));
    }

    @Test
    public void anUnknownKeyGoesToTheFallback() {
        ToGlobCaller<List<String>, String, String> caller =
                LoopToGlobCallerFactory.INSTANCE.create("test", functions(1, 2), record("fallback"), -1);

        assertEquals(List.of("fallback/c2/c3", "fn1/c2/c3", "fallback/c2/c3"),
                call(caller, script(-1, 17, 1, -2)));
    }

    @Test
    public void anUnknownKeyWithoutAFallbackThrowsAndSaysWhich() {
        ToGlobCaller<List<String>, String, String> caller =
                LoopToGlobCallerFactory.INSTANCE.create("test", functions(1, 2), null, -1);

        assertEquals(List.of("fn2/c2/c3"), call(caller, script(-1, 2)));
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> call(caller, script(-1, 2, 17)));
        assertTrue(exception.getMessage().contains("17"), exception.getMessage());
    }

    /** endLoop is tested before the dispatch : it ends the pass even when it is also a key. */
    @Test
    public void anEndLoopOfItsOwnShadowsTheKeyItEquals() {
        ToGlobCaller<List<String>, String, String> caller =
                LoopToGlobCallerFactory.INSTANCE.create("test", functions(1, 2, 3), record("fallback"), 3);

        assertEquals(List.of("fn1/c2/c3", "fn2/c2/c3"), call(caller, script(3, 1, 2, 3, 1)));
    }

    @Test
    public void noFunctionAtAllIsALoopThatOnlyWaitsForTheEnd() {
        ToGlobCaller<List<String>, String, String> caller = LoopToGlobCallerFactory.INSTANCE
                .create("test", Collections.emptySortedMap(), record("fallback"), 0);

        assertEquals(List.of("fallback/c2/c3", "fallback/c2/c3"), call(caller, script(0, 4, 9)));
    }

    /** The functions get the Glob and write into it — the point of the whole thing. */
    @Test
    public void theFunctionsWriteIntoTheGlobTheyAreHanded() {
        SortedMap<Integer, ToGlobFunction<List<String>, String, String>> functions = new TreeMap<>();
        functions.put(0, (glob, trace, ctx2, ctx3) -> glob.set(DummyObject.NAME, "a name"));
        functions.put(1, (glob, trace, ctx2, ctx3) -> glob.set(DummyObject.COUNT, 12));
        ToGlobCaller<List<String>, String, String> caller =
                LoopToGlobCallerFactory.INSTANCE.create("test", functions, null, -1);

        MutableGlob glob = DummyObject.TYPE.instantiate();
        caller.call(script(-1, 1, 0), glob, new ArrayList<>(), "c2", "c3");

        assertEquals("a name", glob.get(DummyObject.NAME));
        assertEquals(12, glob.get(DummyObject.COUNT).intValue());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void writeAllCallsEveryFunctionOnceInOrder() {
        ToGlobCallerAll<List<String>, String, String> caller = LoopToGlobCallerFactory.INSTANCE
                .create("test", new ToGlobFunction[]{record("a"), record("b"), record("c")});

        List<String> trace = new ArrayList<>();
        caller.call(DummyObject.TYPE.instantiate(), trace, "c2", "c3");

        assertEquals(List.of("a/c2/c3", "b/c2/c3", "c/c2/c3"), trace);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void aMissingFunctionIsRefusedWhenTheCallerIsBuilt() {
        SortedMap<Integer, ToGlobFunction<List<String>, String, String>> functions = functions(1, 2);
        functions.put(3, null);
        assertThrows(IllegalArgumentException.class,
                () -> LoopToGlobCallerFactory.INSTANCE.create("test", functions, null, -1));
        assertThrows(IllegalArgumentException.class, () -> LoopToGlobCallerFactory.INSTANCE
                .create("test", new ToGlobFunction[]{record("a"), null}));
    }

    /**
     * The name is what a generating implementation names its emitted class after, so that the class is the
     * same one from one run to the next. Refused here as well as there : the loop has no class to name, but
     * a parser must not be able to get away with a name on one JVM and not on another.
     */
    @Test
    public void aCallerWithoutANameIsRefusedEvenThoughTheLoopWouldNotUseIt() {
        assertThrows(IllegalArgumentException.class,
                () -> LoopToGlobCallerFactory.INSTANCE.create(null, functions(1, 2), null, -1));
        assertThrows(IllegalArgumentException.class,
                () -> LoopToGlobCallerFactory.INSTANCE.create("  ", functions(1, 2), null, -1));
        assertThrows(IllegalArgumentException.class, () -> LoopToGlobCallerFactory.INSTANCE
                .create(null, new ToGlobFunction[]{record("a")}));
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
        public <C1, C2, C3> ToGlobCaller<C1, C2, C3> create(
                String name, SortedMap<Integer, ToGlobFunction<C1, C2, C3>> functions,
                ToGlobFunction fallback, int endLoop) {
            throw new UnsupportedOperationException();
        }

        public <C1, C2, C3> ToGlobCallerAll<C1, C2, C3> create(
                String name, ToGlobFunction<C1, C2, C3>[] functions) {
            throw new UnsupportedOperationException();
        }
    }
}
