package org.globsframework.core.model.generate.write;

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
 * The fallback write callers, and what a generated one has to agree with : the CallAt drives the loop, the
 * endLoop value is tested before the dispatch, an unknown key goes to the fallback and, without one, throws.
 */
public class DefaultFunctionCallerWriteTest {

    /** ctx1 is the trace, so what it collects is the proof the three contexts were forwarded. */
    private MutableFunctionWrite<List<String>, String, String> record(String label) {
        return (glob, trace, ctx2, ctx3) -> trace.add(label + "/" + ctx2 + "/" + ctx3);
    }

    /** Answers the script, then {@code endLoop} for ever — a parser that ran out of input. */
    private CallAtWrite script(int endLoop, int... calls) {
        return new CallAtWrite() {
            int next = 0;

            public int getNextToCall() {
                return next < calls.length ? calls[next++] : endLoop;
            }
        };
    }

    private SortedMap<Integer, MutableFunctionWrite<List<String>, String, String>> functions(int... keys) {
        SortedMap<Integer, MutableFunctionWrite<List<String>, String, String>> functions = new TreeMap<>();
        for (int key : keys) {
            functions.put(key, record("fn" + key));
        }
        return functions;
    }

    private List<String> call(GeneratedCallerWrite<List<String>, String, String> caller, CallAtWrite callAt) {
        List<String> trace = new ArrayList<>();
        caller.call(callAt, DummyObject.TYPE.instantiate(), trace, "c2", "c3");
        return trace;
    }

    @Test
    public void callsWhatTheCallAtAsksForInOrder() {
        GeneratedCallerWrite<List<String>, String, String> caller = DefaultFunctionCallerWrite.INSTANCE
                .create("test", functions(-3, 0, 1, 100000), record("fallback"), -1);

        assertEquals(List.of("fn1/c2/c3", "fn-3/c2/c3", "fn100000/c2/c3", "fn1/c2/c3", "fn0/c2/c3"),
                call(caller, script(-1, 1, -3, 100000, 1, 0)));
    }

    /** The keys are sorted by the caller, not taken as the map iterates them. */
    @Test
    public void aMapWithItsOwnComparatorIsStillReadRight() {
        SortedMap<Integer, MutableFunctionWrite<List<String>, String, String>> functions =
                new TreeMap<>(Comparator.reverseOrder());
        for (int key : new int[]{1, 5, 9, 12}) {
            functions.put(key, record("fn" + key));
        }
        GeneratedCallerWrite<List<String>, String, String> caller =
                DefaultFunctionCallerWrite.INSTANCE.create("test", functions, record("fallback"), -1);

        assertEquals(List.of("fn9/c2/c3", "fn1/c2/c3", "fn12/c2/c3", "fn5/c2/c3"),
                call(caller, script(-1, 9, 1, 12, 5)));
    }

    @Test
    public void anUnknownKeyGoesToTheFallback() {
        GeneratedCallerWrite<List<String>, String, String> caller =
                DefaultFunctionCallerWrite.INSTANCE.create("test", functions(1, 2), record("fallback"), -1);

        assertEquals(List.of("fallback/c2/c3", "fn1/c2/c3", "fallback/c2/c3"),
                call(caller, script(-1, 17, 1, -2)));
    }

    @Test
    public void anUnknownKeyWithoutAFallbackThrowsAndSaysWhich() {
        GeneratedCallerWrite<List<String>, String, String> caller =
                DefaultFunctionCallerWrite.INSTANCE.create("test", functions(1, 2), null, -1);

        assertEquals(List.of("fn2/c2/c3"), call(caller, script(-1, 2)));
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> call(caller, script(-1, 2, 17)));
        assertTrue(exception.getMessage().contains("17"), exception.getMessage());
    }

    /** endLoop is tested before the dispatch : it ends the pass even when it is also a key. */
    @Test
    public void anEndLoopOfItsOwnShadowsTheKeyItEquals() {
        GeneratedCallerWrite<List<String>, String, String> caller =
                DefaultFunctionCallerWrite.INSTANCE.create("test", functions(1, 2, 3), record("fallback"), 3);

        assertEquals(List.of("fn1/c2/c3", "fn2/c2/c3"), call(caller, script(3, 1, 2, 3, 1)));
    }

    @Test
    public void noFunctionAtAllIsALoopThatOnlyWaitsForTheEnd() {
        GeneratedCallerWrite<List<String>, String, String> caller = DefaultFunctionCallerWrite.INSTANCE
                .create("test", Collections.emptySortedMap(), record("fallback"), 0);

        assertEquals(List.of("fallback/c2/c3", "fallback/c2/c3"), call(caller, script(0, 4, 9)));
    }

    /** The functions get the Glob and write into it — the point of the whole thing. */
    @Test
    public void theFunctionsWriteIntoTheGlobTheyAreHanded() {
        SortedMap<Integer, MutableFunctionWrite<List<String>, String, String>> functions = new TreeMap<>();
        functions.put(0, (glob, trace, ctx2, ctx3) -> glob.set(DummyObject.NAME, "a name"));
        functions.put(1, (glob, trace, ctx2, ctx3) -> glob.set(DummyObject.COUNT, 12));
        GeneratedCallerWrite<List<String>, String, String> caller =
                DefaultFunctionCallerWrite.INSTANCE.create("test", functions, null, -1);

        MutableGlob glob = DummyObject.TYPE.instantiate();
        caller.call(script(-1, 1, 0), glob, new ArrayList<>(), "c2", "c3");

        assertEquals("a name", glob.get(DummyObject.NAME));
        assertEquals(12, glob.get(DummyObject.COUNT).intValue());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void writeAllCallsEveryFunctionOnceInOrder() {
        GeneratedCallerWriteAll<List<String>, String, String> caller = DefaultFunctionCallerWrite.INSTANCE
                .create("test", new MutableFunctionWrite[]{record("a"), record("b"), record("c")});

        List<String> trace = new ArrayList<>();
        caller.call(DummyObject.TYPE.instantiate(), trace, "c2", "c3");

        assertEquals(List.of("a/c2/c3", "b/c2/c3", "c/c2/c3"), trace);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void aMissingFunctionIsRefusedWhenTheCallerIsBuilt() {
        SortedMap<Integer, MutableFunctionWrite<List<String>, String, String>> functions = functions(1, 2);
        functions.put(3, null);
        assertThrows(IllegalArgumentException.class,
                () -> DefaultFunctionCallerWrite.INSTANCE.create("test", functions, null, -1));
        assertThrows(IllegalArgumentException.class, () -> DefaultFunctionCallerWrite.INSTANCE
                .create("test", new MutableFunctionWrite[]{record("a"), null}));
    }

    /**
     * The name is what a generating implementation names its emitted class after, so that the class is the
     * same one from one run to the next. Refused here as well as there : the loop has no class to name, but
     * a parser must not be able to get away with a name on one JVM and not on another.
     */
    @Test
    public void aCallerWithoutANameIsRefusedEvenThoughTheLoopWouldNotUseIt() {
        assertThrows(IllegalArgumentException.class,
                () -> DefaultFunctionCallerWrite.INSTANCE.create(null, functions(1, 2), null, -1));
        assertThrows(IllegalArgumentException.class,
                () -> DefaultFunctionCallerWrite.INSTANCE.create("  ", functions(1, 2), null, -1));
        assertThrows(IllegalArgumentException.class, () -> DefaultFunctionCallerWrite.INSTANCE
                .create(null, new MutableFunctionWrite[]{record("a")}));
    }

    /** Nothing installed : the loop, and a parser that never has to know. */
    @Test
    public void withNoServiceGetAnswersTheLoop() {
        assertNull(GeneratedFunctionCallerWrite.getGenerated());
        assertSame(DefaultFunctionCallerWrite.INSTANCE, GeneratedFunctionCallerWrite.get());
    }

    /**
     * The extension point : with a GenerateCallerWriteService installed, get stops answering the loop. Core
     * has no implementation of its own — globs-generate's is the one that emits the switch — so the test
     * installs a stand-in and only checks the wiring.
     */
    @Test
    public void anInstalledServiceIsPreferredToTheLoop() {
        System.setProperty("globs.callerWrite", StandInService.class.getName());
        GenerateCallerWriteService.Builder.reset();
        try {
            assertInstanceOf(StandIn.class, GeneratedFunctionCallerWrite.get());
            assertInstanceOf(StandIn.class, GeneratedFunctionCallerWrite.getGenerated());
        } finally {
            System.clearProperty("globs.callerWrite");
            GenerateCallerWriteService.Builder.reset();
        }
        assertSame(DefaultFunctionCallerWrite.INSTANCE, GeneratedFunctionCallerWrite.get());
    }

    /** "nothing to offer" is a null, and the loop takes over — it is not an error to report. */
    @Test
    public void aServiceThatAnswersNullFallsBackToTheLoop() {
        System.setProperty("globs.callerWrite", AbstainingService.class.getName());
        GenerateCallerWriteService.Builder.reset();
        try {
            assertSame(DefaultFunctionCallerWrite.INSTANCE, GeneratedFunctionCallerWrite.get());
            assertNull(GeneratedFunctionCallerWrite.getGenerated());
        } finally {
            System.clearProperty("globs.callerWrite");
            GenerateCallerWriteService.Builder.reset();
        }
    }

    /** an explicitly asked for service that cannot be loaded is a misconfiguration, not a slow path */
    @Test
    public void anUnloadableServiceThrowsRatherThanDegradingSilently() {
        System.setProperty("globs.callerWrite", "not.a.Class");
        try {
            assertThrows(RuntimeException.class, GenerateCallerWriteService.Builder::reset);
        } finally {
            System.clearProperty("globs.callerWrite");
            GenerateCallerWriteService.Builder.reset();
        }
    }

    public static class StandInService implements GenerateCallerWriteService {
        public GeneratedFunctionCallerWrite getGenerateCallerWrite() {
            return new StandIn();
        }
    }

    public static class AbstainingService implements GenerateCallerWriteService {
        public GeneratedFunctionCallerWrite getGenerateCallerWrite() {
            return null;
        }
    }

    public static class StandIn implements GeneratedFunctionCallerWrite {
        public <Ctx1, Ctx2, Ctx3> GeneratedCallerWrite<Ctx1, Ctx2, Ctx3> create(
                String name, SortedMap<Integer, MutableFunctionWrite<Ctx1, Ctx2, Ctx3>> functions,
                MutableFunctionWrite fallback, int endLoop) {
            throw new UnsupportedOperationException();
        }

        public <Ctx1, Ctx2, Ctx3> GeneratedCallerWriteAll<Ctx1, Ctx2, Ctx3> create(
                String name, MutableFunctionWrite<Ctx1, Ctx2, Ctx3>[] functions) {
            throw new UnsupportedOperationException();
        }
    }
}
