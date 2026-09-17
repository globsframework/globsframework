package org.globsframework.core.model.caller;

import org.globsframework.core.model.MutableGlob;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The typed shape : the caller's own interfaces, so the arguments stay what they are — a {@code long} is a
 * {@code long}, and a function is called through its own method rather than through an adapter.
 * <p>
 * What is pinned here is the <em>contract</em>, which a generating implementation has to answer the same way:
 * how the method is found (by its parameter types, not its name), what is refused, and that the functions are
 * called once each in order with the arguments they were given. The fallback proxy is what runs it when
 * nothing generates.
 */
public class TypedCallerShapeTest {

    /** The caller's interface : what the code holding the loop wants to call. */
    public interface RecordReader {
        void read(MutableGlob data, long offset, List<String> trace);
    }

    /** The function's interface, written independently — note the method is not named like the caller's. */
    public interface FieldRead {
        void readAtOffset(MutableGlob data, long offset, List<String> trace);
    }

    private FieldRead record(String label) {
        return (data, offset, trace) -> trace.add(label + "@" + offset);
    }

    private RecordReader reader(FieldRead... functions) {
        return LoopToGlobCallerFactory.INSTANCE.create("test.typed", functions, RecordReader.class,
                FieldRead.class, MutableGlob.class, long.class, List.class);
    }

    @Test
    public void everyFunctionIsCalledOnceInOrderWithTheArgumentsGiven() {
        List<String> trace = new ArrayList<>();
        reader(record("a"), record("b"), record("c")).read(null, 42L, trace);

        assertEquals(List.of("a@42", "b@42", "c@42"), trace);
    }

    @Test
    public void nothingToCallIsAnEmptyCall() {
        List<String> trace = new ArrayList<>();
        reader().read(null, 1L, trace);

        assertTrue(trace.isEmpty());
    }

    /** A function of the array is refused when the caller is built, not when it runs. */
    @Test
    public void aMissingFunctionIsRefusedAtBuildTime() {
        assertThrows(IllegalArgumentException.class, () -> reader(record("a"), null));
    }

    /** What the functions throw is what the caller throws — no InvocationTargetException in the way. */
    @Test
    public void whatAFunctionThrowsComesOutAsItIs() {
        FieldRead boom = (data, offset, trace) -> {
            throw new IllegalStateException("boom");
        };
        IllegalStateException thrown = assertThrows(IllegalStateException.class,
                () -> reader(record("a"), boom).read(null, 0L, new ArrayList<>()));
        assertEquals("boom", thrown.getMessage());
    }

    @Test
    public void theMethodIsFoundByItsParametersWhateverItIsCalled() {
        Method caller = CallerShape.methodMatching(RecordReader.class, MutableGlob.class, long.class,
                List.class);
        Method function = CallerShape.methodMatching(FieldRead.class, MutableGlob.class, long.class,
                List.class);

        assertEquals("read", caller.getName());
        assertEquals("readAtOffset", function.getName());
    }

    /** long is not Long : the whole point is that the parameter types are taken literally. */
    @Test
    public void aBoxedParameterIsNotThePrimitiveOne() {
        assertThrows(IllegalArgumentException.class,
                () -> CallerShape.methodMatching(FieldRead.class, MutableGlob.class, Long.class,
                        List.class));
    }

    public interface TwoMatches {
        void one(long value);

        void other(long value);
    }

    public interface NotVoid {
        int one(long value);
    }

    @Test
    public void anAmbiguousOrValuedShapeIsRefused() {
        assertThrows(IllegalArgumentException.class,
                () -> CallerShape.methodMatching(TwoMatches.class, long.class));
        assertThrows(IllegalArgumentException.class,
                () -> CallerShape.methodMatching(NotVoid.class, long.class));
        assertThrows(IllegalArgumentException.class,
                () -> CallerShape.methodMatching(RecordReader.class, long.class));
    }

    @Test
    public void aCallerCanOnlyBeBuiltOverAnInterface() {
        assertThrows(IllegalArgumentException.class,
                () -> LoopToGlobCallerFactory.INSTANCE.create("test.typed", new FieldRead[0],
                        String.class, FieldRead.class, MutableGlob.class, long.class, List.class));
        assertThrows(IllegalArgumentException.class,
                () -> LoopToGlobCallerFactory.INSTANCE.create("  ", new FieldRead[0], RecordReader.class,
                        FieldRead.class, MutableGlob.class, long.class, List.class));
    }
}
