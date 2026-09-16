package org.globsframework.core.model.caller;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;

/**
 * Builds the callers of the to-Glob side. Core's own implementation is {@link LoopToGlobCallerFactory}, the
 * plain loop; a module able to emit bytecode offers a better one through {@link ToGlobCallerService},
 * and the interface lives here so that a parser can be written against it without depending on that module.
 * <p>
 * A generating implementation is free to emit a class per call, holding <em>these</em> functions in its static
 * finals — that is what makes each call site monomorphic, and it is why this belongs to the setup phase of a
 * parser, not to its hot path. That emitted class is named after the {@code name} given here : see
 * {@link CallerName} for what to pass and why it is not optional.
 */
public interface ToGlobCallerFactory {

    /**
     * The dispatching caller : {@code functions} keyed by whatever the {@link KeySource} answers.
     *
     * @param name     what builds this caller, constant in the source — see {@link CallerName}. Nothing here
     *                 depends on a GlobType, so this is the whole of what a generated class is named after :
     *                 a parser that builds one caller per type has to say which type in it.
     * @param fallback what an unknown key goes to. null means there is none, and an unknown key then throws.
     * @param endLoop  the value that ends the pass. It is tested before the dispatch, so it may be a key of
     *                 the map — it is simply shadowed.
     */
    <C1, C2, C3> ToGlobCaller<C1, C2, C3> create(String name,
                                                 SortedMap<Integer, ToGlobFunction<C1, C2, C3>> functions,
                                                 ToGlobFunction<C1, C2, C3> fallback, int endLoop);

//    <T, D> T create(String name, SortedMap<Integer, D> functions, D fallback, int endLoop);

    /**
     * The unrolled caller : every function called once, in the order of the array.
     *
     * @param name what builds this caller — see {@link #create(String, SortedMap, ToGlobFunction, int)}.
     */
    <C1, C2, C3> ToGlobCallerAll<C1, C2, C3> create(String name,
                                                    ToGlobFunction<C1, C2, C3>[] functions);

    /**
     * The unrolled caller again, but over <em>the caller's own</em> interfaces instead of
     * {@link ToGlobCallerAll} and {@link ToGlobFunction} — which is what lets the arguments be whatever they
     * are, primitives included.
     * <p>
     * The two generic interfaces carry objects only : a {@code long} has to be boxed, and a function whose
     * real signature is something else needs an adapter object in front of it. Both were measured in
     * globs-off-heap, on a walk reading a record field by field, and they cost <b>four times</b> what
     * generating the dispatch saves — one call level more, the bridge's casts, an unboxing per element. Here
     * the emitted class implements {@code tClass} directly and calls {@code dClass} directly, so there is
     * nothing to adapt : no wrapper, no bridge, no box.
     * <pre>
     * interface RecordReader { void read(MutableGlob data, MemorySegment segment, long offset, Ctx ctx); }
     * // HandleAccess already has readAtOffset(MutableGlob, MemorySegment, long, Ctx)
     * RecordReader reader = factory.create("offheap.readAll." + type.getName(), handleAccesses,
     *         RecordReader.class, HandleAccess.class,
     *         MutableGlob.class, MemorySegment.class, long.class, Ctx.class);
     * </pre>
     * The method to emit and the method to call are found by their parameter types, not by their names — the
     * two interfaces are written independently and have no reason to agree on a name. Each of the two types
     * must hold <b>exactly one</b> method taking {@code argument}, returning void : see
     * {@link #methodMatching}, which is where both this and the generating implementations get it from.
     *
     * @param name      what builds this caller, constant in the source — see {@link CallerName}.
     * @param functions called once each, in the order of the array, with the arguments the caller was given.
     * @param tClass    the interface to implement. An interface, because that is what there is to implement.
     * @param dClass    what the elements of {@code functions} are called through. May be a class.
     * @param argument  the parameter types, in order, of the one method of each.
     */
    <T, D> T create(String name, D[] functions, Class<T> tClass, Class<D> dClass, Class<?>... argument);

    /**
     * What a parser builds its callers with : the {@link ToGlobCallerService} installed through
     * {@code -Dglobs.caller.toGlob}, or {@link LoopToGlobCallerFactory} when there is none.
     * <p>
     * The two give the same behaviour — same order, same fallback, same end of loop — so a parser keeps one
     * code path and only the speed changes. This is the to-Glob side's answer to
     * {@code FromGlobCallerFactory.callerFor}, without the GlobType: nothing here depends on the type,
     * since the functions do their own writing through {@code MutableGlob}.
     */
    static ToGlobCallerFactory get() {
        ToGlobCallerFactory generated = generated();
        return generated != null ? generated : LoopToGlobCallerFactory.INSTANCE;
    }

    /**
     * The same resolution without the loop at the end : **null** when nothing is installed to generate. For a
     * parser that already has something better than {@link LoopToGlobCallerFactory} to fall back on.
     */
    static ToGlobCallerFactory generated() {
        ToGlobCallerService service = ToGlobCallerService.Builder.getService();
        return service != null ? service.factory() : null;
    }

    /**
     * What every implementation throws for a key it has no function for, when it was built without a
     * fallback: a bug in the parser, not a case to skip silently. Shared so that the generated callers and
     * the loop say the same thing.
     */
    static RuntimeException unknownKey(int nextToCall) {
        return new IllegalStateException("No ToGlobFunction for " + nextToCall
                                         + " and no fallback was given.");
    }

    /** The other shared refusal : a missing function is refused when the caller is built, not when it runs. */
    static ToGlobFunction checked(ToGlobFunction function, String at) {
        if (function == null) {
            throw new IllegalArgumentException("No ToGlobFunction for " + at);
        }
        return function;
    }

    /**
     * The one method of {@code type} taking exactly {@code argument} — how
     * {@link #create(String, Object[], Class, Class, Class...)} finds both the method to emit and the method
     * to call, in one place so that the loop and a generator can never disagree on what a caller's shape is.
     * <p>
     * Matching is on the parameter types and on nothing else : the two interfaces are written independently,
     * so the names are not expected to agree. It has to be unambiguous, hence exactly one — an overload
     * taking the same types cannot exist, but a type holding two methods of different names over the same
     * parameters can, and that is refused here rather than resolved by a rule nobody would remember.
     * <p>
     * Void only, and for a reason that is not laziness : this shape calls every function once, so a return
     * value would be N values and one of them would have to win. A shape that folds a value through the calls
     * is a different contract, not a relaxation of this one.
     */
    static Method methodMatching(Class<?> type, Class<?>... argument) {
        List<Method> found = new ArrayList<>();
        for (Method method : type.getMethods()) {
            if (!Modifier.isStatic(method.getModifiers())
                && Arrays.equals(method.getParameterTypes(), argument)) {
                found.add(method);
            }
        }
        if (found.size() != 1) {
            throw new IllegalArgumentException(
                    (found.isEmpty() ? "No method of " : "More than one method of ") + type.getName()
                    + " takes " + Arrays.toString(argument)
                    + (found.isEmpty() ? "" : " : " + found));
        }
        Method method = found.get(0);
        if (method.getReturnType() != void.class) {
            throw new IllegalArgumentException(method + " returns " + method.getReturnType().getName()
                                               + " : a caller calling every function once has no value to "
                                               + "answer, so this shape is void only.");
        }
        return method;
    }
}
