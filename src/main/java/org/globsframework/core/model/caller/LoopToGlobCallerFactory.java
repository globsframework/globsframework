package org.globsframework.core.model.caller;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.SortedMap;

/**
 * The {@link ToGlobCallerFactory} of a JVM where nothing generates : the plain loop over a table of
 * functions, behind a {@link Proxy} implementing the caller's own interface.
 * <p>
 * Behaviourally identical to a generated one — same functions, same order, the same fallback for an unknown
 * key, the same {@code endLoop} tested before the dispatch and the same refusals. What it does not give is
 * the point of generating: one call site for the whole loop, seeing every function it is ever handed, i.e.
 * exactly the megamorphic dispatch a generated caller exists to remove.
 * <p>
 * And it gives it back twice over, because the caller's interfaces are the caller's own : a Proxy reaches its
 * functions reflectively, so every primitive argument is boxed on every call and every call goes through an
 * {@code Object[]}. That is precisely what the typed shape exists to avoid. It is a fallback so that a parser
 * can keep one code path, not an alternative — a parser that cares should ask
 * {@link ToGlobCallerFactory#generated()} and keep its own hand-written path when that answers null, the way
 * globs-off-heap keeps its partitioned reader.
 * <p>
 * It has no class to name, so the {@code name} of a caller is only checked here, never used.
 */
public class LoopToGlobCallerFactory implements ToGlobCallerFactory {
    /** Stateless : the callers hold everything, so one instance serves the whole process. */
    public static final LoopToGlobCallerFactory INSTANCE = new LoopToGlobCallerFactory();

    /**
     * The lookupswitch a generated caller emits, by hand : the keys sorted, and a binary search per turn.
     * The key source is the {@code argument} that is one, read out of the call's own arguments.
     */
    public <T, D> T create(String name, SortedMap<Integer, D> functions, D fallback, int endLoop,
                           Class<T> tClass, Class<D> dClass, Class<?>... argument) {
        CallerName.check(name);
        CallerShape.checkInterface(tClass);
        int keySourceAt = ToGlobCallerFactory.keySourceIndex(argument);
        Method callerMethod = CallerShape.methodMatching(tClass, argument);
        Method functionMethod = CallerShape.methodMatching(dClass, argument);
        // sorted here rather than trusted : the lookup is a binary search, and the map may have been built
        // with a comparator of its own
        int[] keys = functions.keySet().stream().mapToInt(Integer::intValue).sorted().toArray();
        Object[] ordered = new Object[keys.length];
        for (int i = 0; i < keys.length; i++) {
            ordered[i] = CallerShape.checked(functions.get(keys[i]), "key " + keys[i]);
        }
        functionMethod.setAccessible(true);
        return proxy(tClass, dClass, callerMethod, ordered.length, (proxy, args) -> {
            KeySource keySource = (KeySource) args[keySourceAt];
            int nextToCall;
            while ((nextToCall = keySource.nextKey()) != endLoop) {
                int at = Arrays.binarySearch(keys, nextToCall);
                Object function = at >= 0 ? ordered[at] : fallback;
                if (function == null) {
                    throw ToGlobCallerFactory.unknownKey(nextToCall);
                }
                invoke(functionMethod, function, args);
            }
        });
    }

    /** The same table with no input to follow : every function once, in the order of the array. */
    public <T, D> T create(String name, D[] functions, Class<T> tClass, Class<D> dClass,
                           Class<?>... argument) {
        CallerName.check(name);
        CallerShape.checkInterface(tClass);
        Method callerMethod = CallerShape.methodMatching(tClass, argument);
        Method functionMethod = CallerShape.methodMatching(dClass, argument);
        D[] copy = functions.clone();
        for (int i = 0; i < copy.length; i++) {
            CallerShape.checked(copy[i], "index " + i);
        }
        functionMethod.setAccessible(true);
        return proxy(tClass, dClass, callerMethod, copy.length, (proxy, args) -> {
            for (D function : copy) {
                invoke(functionMethod, function, args);
            }
        });
    }

    /** What the function throws is what the caller throws — no InvocationTargetException in the way. */
    private static void invoke(Method functionMethod, Object function, Object[] args) throws Throwable {
        try {
            functionMethod.invoke(function, args);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    /** What the caller's one method does; everything else is Object's, plus a refusal. */
    private interface Body {
        void run(Object proxy, Object[] args) throws Throwable;
    }

    @SuppressWarnings("unchecked")
    private static <T> T proxy(Class<T> tClass, Class<?> dClass, Method callerMethod, int count, Body body) {
        InvocationHandler handler = (proxy, method, args) -> {
            // by shape rather than by identity : a Proxy hands back the Method of the interface the call
            // came through, which is not always the one getMethods() found it in
            if (method.getName().equals(callerMethod.getName())
                && Arrays.equals(method.getParameterTypes(), callerMethod.getParameterTypes())) {
                body.run(proxy, args);
                return null;
            }
            return switch (method.getName()) {
                case "toString" -> tClass.getName() + " over " + count + " " + dClass.getName();
                case "hashCode" -> System.identityHashCode(proxy);
                case "equals" -> proxy == args[0];
                default -> throw new UnsupportedOperationException(
                        method + " : this caller only answers " + callerMethod.getName() + ".");
            };
        };
        return (T) Proxy.newProxyInstance(tClass.getClassLoader(), new Class<?>[]{tClass}, handler);
    }
}
