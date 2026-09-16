package org.globsframework.core.model.caller;

import org.globsframework.core.model.MutableGlob;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.SortedMap;

/**
 * The {@link ToGlobCallerFactory} of a JVM where nothing generates : the plain loop over a table of
 * functions.
 * <p>
 * Behaviourally identical to a generated one — same functions, same order, the same fallback for an unknown
 * key, the same {@code endLoop} tested before the dispatch and the same refusals. What it does not give is the
 * point of generating: one call site for the whole loop, seeing every function it is ever handed, i.e. exactly
 * the megamorphic dispatch a generated caller exists to remove. It is the fallback, not an alternative.
 * <p>
 * It has no class to name, so the {@code name} of a caller is only checked here, never used.
 */
public class LoopToGlobCallerFactory implements ToGlobCallerFactory {
    /** Stateless : the callers hold everything, so one instance serves the whole process. */
    public static final LoopToGlobCallerFactory INSTANCE = new LoopToGlobCallerFactory();

    @SuppressWarnings("unchecked")
    public <C1, C2, C3> ToGlobCaller<C1, C2, C3> create(
            String name, SortedMap<Integer, ToGlobFunction<C1, C2, C3>> functions,
            ToGlobFunction<C1, C2, C3> fallback, int endLoop) {
        CallerName.check(name);
        // sorted here rather than trusted : the lookup is a binary search, and the map may have been built
        // with a comparator of its own
        int[] keys = functions.keySet().stream().mapToInt(Integer::intValue).sorted().toArray();
        ToGlobFunction<C1, C2, C3>[] ordered = new ToGlobFunction[keys.length];
        for (int i = 0; i < keys.length; i++) {
            ordered[i] = ToGlobCallerFactory.checked(functions.get(keys[i]), "key " + keys[i]);
        }
        return new LoopToGlobCaller<>(keys, ordered, fallback, endLoop);
    }

    @SuppressWarnings("unchecked")
    public <C1, C2, C3> ToGlobCallerAll<C1, C2, C3> create(
            String name, ToGlobFunction<C1, C2, C3>[] functions) {
        CallerName.check(name);
        ToGlobFunction<C1, C2, C3>[] copy = new ToGlobFunction[functions.length];
        for (int i = 0; i < functions.length; i++) {
            copy[i] = ToGlobCallerFactory.checked(functions[i], "index " + i);
        }
        return new LoopToGlobCallerAll<>(copy);
    }

    /**
     * The typed shape without a generator : a {@link Proxy} implementing {@code tClass}, looping over the
     * functions and calling {@code dClass}'s method reflectively.
     * <p>
     * Same behaviour, same refusals, same order — and no pretence about the speed. This one boxes every
     * primitive argument on every call, which is exactly what the typed shape exists to avoid, so it is a
     * fallback for a JVM where nothing generates and not an alternative. A caller that cares should ask
     * {@link ToGlobCallerFactory#generated()} and keep its own path when that answers null, the way
     * globs-off-heap keeps its hand-written reader.
     */
    @SuppressWarnings("unchecked")
    public <T, D> T create(String name, D[] functions, Class<T> tClass, Class<D> dClass,
                           Class<?>... argument) {
        CallerName.check(name);
        if (!tClass.isInterface()) {
            throw new IllegalArgumentException(tClass.getName() + " is not an interface : there would be "
                                               + "nothing to implement.");
        }
        Method callerMethod = ToGlobCallerFactory.methodMatching(tClass, argument);
        Method functionMethod = ToGlobCallerFactory.methodMatching(dClass, argument);
        D[] copy = functions.clone();
        for (int i = 0; i < copy.length; i++) {
            if (copy[i] == null) {
                throw new IllegalArgumentException("No " + dClass.getName() + " for index " + i);
            }
        }
        functionMethod.setAccessible(true);
        InvocationHandler handler = (proxy, method, args) -> {
            if (method.equals(callerMethod)) {
                for (D function : copy) {
                    try {
                        functionMethod.invoke(function, args);
                    } catch (InvocationTargetException e) {
                        throw e.getCause();
                    }
                }
                return null;
            }
            return switch (method.getName()) {
                case "toString" -> tClass.getName() + " over " + copy.length + " " + dClass.getName();
                case "hashCode" -> System.identityHashCode(proxy);
                case "equals" -> proxy == args[0];
                default -> throw new UnsupportedOperationException(
                        method + " : this caller only answers " + callerMethod.getName() + ".");
            };
        };
        return (T) Proxy.newProxyInstance(tClass.getClassLoader(), new Class<?>[]{tClass}, handler);
    }

    /** The lookupswitch a generated caller emits, by hand : the keys sorted, and a binary search per turn. */
    private static class LoopToGlobCaller<C1, C2, C3> implements ToGlobCaller<C1, C2, C3> {
        private final int[] keys;
        private final ToGlobFunction<C1, C2, C3>[] functions;
        private final ToGlobFunction<C1, C2, C3> fallback;
        private final int endLoop;

        LoopToGlobCaller(int[] keys, ToGlobFunction<C1, C2, C3>[] functions,
                        ToGlobFunction<C1, C2, C3> fallback, int endLoop) {
            this.keys = keys;
            this.functions = functions;
            this.fallback = fallback;
            this.endLoop = endLoop;
        }

        public void call(KeySource keySource, MutableGlob data, C1 ctx1, C2 ctx2, C3 ctx3) {
            int nextToCall;
            while ((nextToCall = keySource.nextKey()) != endLoop) {
                int at = Arrays.binarySearch(keys, nextToCall);
                ToGlobFunction<C1, C2, C3> function = at >= 0 ? functions[at] : fallback;
                if (function == null) {
                    throw ToGlobCallerFactory.unknownKey(nextToCall);
                }
                function.call(data, ctx1, ctx2, ctx3);
            }
        }
    }

    private static class LoopToGlobCallerAll<C1, C2, C3> implements ToGlobCallerAll<C1, C2, C3> {
        private final ToGlobFunction<C1, C2, C3>[] functions;

        LoopToGlobCallerAll(ToGlobFunction<C1, C2, C3>[] functions) {
            this.functions = functions;
        }

        public void call(MutableGlob data, C1 ctx1, C2 ctx2, C3 ctx3) {
            for (ToGlobFunction<C1, C2, C3> function : functions) {
                function.call(data, ctx1, ctx2, ctx3);
            }
        }
    }
}
