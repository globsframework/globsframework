package org.globsframework.core.model.generate.write;

import org.globsframework.core.model.MutableGlob;
import org.globsframework.core.model.generate.CallerName;

import java.util.Arrays;
import java.util.SortedMap;

/**
 * The {@link GeneratedFunctionCallerWrite} of a JVM where nothing generates : the plain loop over a table of
 * functions.
 * <p>
 * Behaviourally identical to a generated one — same functions, same order, the same fallback for an unknown
 * key, the same {@code endLoop} tested before the dispatch and the same refusals. What it does not give is the
 * point of generating: one call site for the whole loop, seeing every function it is ever handed, i.e. exactly
 * the megamorphic dispatch a generated caller exists to remove. It is the fallback, not an alternative.
 * <p>
 * It has no class to name, so the {@code name} of a caller is only checked here, never used.
 */
public class DefaultFunctionCallerWrite implements GeneratedFunctionCallerWrite {
    /** Stateless : the callers hold everything, so one instance serves the whole process. */
    public static final DefaultFunctionCallerWrite INSTANCE = new DefaultFunctionCallerWrite();

    @SuppressWarnings("unchecked")
    public <Ctx1, Ctx2, Ctx3> GeneratedCallerWrite<Ctx1, Ctx2, Ctx3> create(
            String name, SortedMap<Integer, MutableFunctionWrite<Ctx1, Ctx2, Ctx3>> functions,
            MutableFunctionWrite fallback, int endLoop) {
        CallerName.check(name);
        // sorted here rather than trusted : the lookup is a binary search, and the map may have been built
        // with a comparator of its own
        int[] keys = functions.keySet().stream().mapToInt(Integer::intValue).sorted().toArray();
        MutableFunctionWrite<Ctx1, Ctx2, Ctx3>[] ordered = new MutableFunctionWrite[keys.length];
        for (int i = 0; i < keys.length; i++) {
            ordered[i] = GeneratedFunctionCallerWrite.checked(functions.get(keys[i]), "key " + keys[i]);
        }
        return new LoopCallerWrite<>(keys, ordered, fallback, endLoop);
    }

    @SuppressWarnings("unchecked")
    public <Ctx1, Ctx2, Ctx3> GeneratedCallerWriteAll<Ctx1, Ctx2, Ctx3> create(
            String name, MutableFunctionWrite<Ctx1, Ctx2, Ctx3>[] functions) {
        CallerName.check(name);
        MutableFunctionWrite<Ctx1, Ctx2, Ctx3>[] copy = new MutableFunctionWrite[functions.length];
        for (int i = 0; i < functions.length; i++) {
            copy[i] = GeneratedFunctionCallerWrite.checked(functions[i], "index " + i);
        }
        return new LoopCallerWriteAll<>(copy);
    }

    /** The lookupswitch a generated caller emits, by hand : the keys sorted, and a binary search per turn. */
    private static class LoopCallerWrite<Ctx1, Ctx2, Ctx3> implements GeneratedCallerWrite<Ctx1, Ctx2, Ctx3> {
        private final int[] keys;
        private final MutableFunctionWrite<Ctx1, Ctx2, Ctx3>[] functions;
        private final MutableFunctionWrite<Ctx1, Ctx2, Ctx3> fallback;
        private final int endLoop;

        LoopCallerWrite(int[] keys, MutableFunctionWrite<Ctx1, Ctx2, Ctx3>[] functions,
                        MutableFunctionWrite<Ctx1, Ctx2, Ctx3> fallback, int endLoop) {
            this.keys = keys;
            this.functions = functions;
            this.fallback = fallback;
            this.endLoop = endLoop;
        }

        public void call(CallAtWrite callAt, MutableGlob data, Ctx1 ctx1, Ctx2 ctx2, Ctx3 ctx3) {
            int nextToCall;
            while ((nextToCall = callAt.getNextToCall()) != endLoop) {
                int at = Arrays.binarySearch(keys, nextToCall);
                MutableFunctionWrite<Ctx1, Ctx2, Ctx3> function = at >= 0 ? functions[at] : fallback;
                if (function == null) {
                    throw GeneratedFunctionCallerWrite.unknownKey(nextToCall);
                }
                function.call(data, ctx1, ctx2, ctx3);
            }
        }
    }

    private static class LoopCallerWriteAll<Ctx1, Ctx2, Ctx3> implements GeneratedCallerWriteAll<Ctx1, Ctx2, Ctx3> {
        private final MutableFunctionWrite<Ctx1, Ctx2, Ctx3>[] functions;

        LoopCallerWriteAll(MutableFunctionWrite<Ctx1, Ctx2, Ctx3>[] functions) {
            this.functions = functions;
        }

        public void call(MutableGlob data, Ctx1 ctx1, Ctx2 ctx2, Ctx3 ctx3) {
            for (MutableFunctionWrite<Ctx1, Ctx2, Ctx3> function : functions) {
                function.call(data, ctx1, ctx2, ctx3);
            }
        }
    }
}
