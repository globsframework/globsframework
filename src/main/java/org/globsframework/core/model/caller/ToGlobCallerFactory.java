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
 *
 * <h2>The caller's own interfaces</h2>
 * Both shapes are built over two types the <em>caller</em> owns : {@code tClass}, the interface the emitted
 * class implements, and {@code dClass}, what the functions are called through. Core names neither, and there
 * is no generic {@code ToGlobCaller}/{@code ToGlobFunction} pair any more — there used to be, carrying three
 * {@code Object} contexts, and it cost exactly what it was built to save : a {@code long} boxed on every
 * call, a bridge method in front of every function whose real signature was something else, and one call
 * level more. Measured in globs-off-heap on a walk reading a record field by field, <b>four times</b> what
 * generating the dispatch earns back.
 * <p>
 * So the arguments are whatever the parser's own code passes around, primitives included, and the emitted
 * class <em>is</em> the parser's interface rather than something it holds :
 * <pre>
 * interface RecordReader { void read(MutableGlob data, MemorySegment segment, long offset, Ctx ctx); }
 * // HandleAccess already has readAtOffset(MutableGlob, MemorySegment, long, Ctx)
 * RecordReader reader = factory.create("offheap.readAll." + type.getName(), handleAccesses,
 *         RecordReader.class, HandleAccess.class,
 *         MutableGlob.class, MemorySegment.class, long.class, Ctx.class);
 * </pre>
 * The method to emit and the method to call are found by their parameter types, not by their names — the two
 * interfaces are written independently and have no reason to agree on a name. Each of the two types must
 * hold <b>exactly one</b> method taking {@code argument}, returning void : see {@link #methodMatching},
 * which is where this, the loop and the generating implementations all get it from.
 */
public interface ToGlobCallerFactory {

    /**
     * The dispatching caller : {@code functions} keyed by whatever the {@link KeySource} answers, looping
     * until it answers {@code endLoop}.
     * <p>
     * The key source is <b>one of the arguments</b> — exactly one of {@code argument} has to be a
     * {@link KeySource}, and that is the one the loop asks. A parser's input is normally both the thing that
     * says what comes next and the thing the functions read from, so it is passed once and used twice rather
     * than being a parameter of its own that every call site would have to repeat.
     *
     * @param name      what builds this caller, constant in the source — see {@link CallerName}. Nothing here
     *                  depends on a GlobType, so this is the whole of what a generated class is named after :
     *                  a parser that builds one caller per type has to say which type in it.
     * @param functions keyed by what the key source answers for them.
     * @param fallback  what an unknown key goes to. null means there is none, and an unknown key then throws.
     * @param endLoop   the value that ends the pass. It is tested before the dispatch, so it may be a key of
     *                  the map — it is simply shadowed.
     * @param tClass    the interface to implement. An interface, because that is what there is to implement.
     * @param dClass    what the functions are called through. May be a class.
     * @param argument  the parameter types, in order, of the one method of each.
     */
    <T, D> T create(String name, SortedMap<Integer, D> functions, D fallback, int endLoop,
                    Class<T> tClass, Class<D> dClass, Class<?>... argument);

    /**
     * The unrolled caller : every function called once, in the order of the array — a format whose entries
     * are all there and always in the same order, so there is no input to follow and no key source among the
     * arguments.
     *
     * @param name what builds this caller, constant in the source — see {@link CallerName}.
     * @see #create(String, SortedMap, Object, int, Class, Class, Class[]) for the arguments it shares
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
     * <p>
     * Note what the loop costs on this side, though : its callers are reflective {@link java.lang.reflect.Proxy}
     * instances, which box every primitive argument on every call. A parser with something better of its own
     * to fall back on should ask {@link #generated()} instead.
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
        return new IllegalStateException("No function for " + nextToCall + " and no fallback was given.");
    }

    /** The other shared refusal : a missing function is refused when the caller is built, not when it runs. */
    static <D> D checked(D function, String at) {
        if (function == null) {
            throw new IllegalArgumentException("No function for " + at);
        }
        return function;
    }

    /** Shared too : what is implemented has to be an interface, whichever shape is asked for. */
    static void checkInterface(Class<?> tClass) {
        if (!tClass.isInterface()) {
            throw new IllegalArgumentException(tClass.getName() + " is not an interface : there would be "
                                               + "nothing to implement.");
        }
    }

    /**
     * Which argument the dispatching shape drives its loop with : the one {@link KeySource} among them.
     * <p>
     * Exactly one, refused otherwise and refused here rather than in each implementation. None means there
     * is nothing to ask what comes next — the unrolled shape is what a caller without a key source wants.
     * Two means a choice nobody would remember, and one of the two would be read as input by the functions
     * while the other drove the loop, which is a bug waiting rather than a shape to support.
     */
    static int keySourceIndex(Class<?>... argument) {
        int found = -1;
        for (int i = 0; i < argument.length; i++) {
            if (KeySource.class.isAssignableFrom(argument[i])) {
                if (found != -1) {
                    throw new IllegalArgumentException(
                            "Both " + argument[found].getName() + " and " + argument[i].getName()
                            + " are a KeySource : the dispatching caller would not know which one drives "
                            + "its loop.");
                }
                found = i;
            }
        }
        if (found == -1) {
            throw new IllegalArgumentException(
                    "None of " + Arrays.toString(argument) + " is a KeySource : the dispatching caller has "
                    + "nothing to ask what comes next.");
        }
        return found;
    }

    /**
     * The one method of {@code type} taking exactly {@code argument} — how both shapes find the method to
     * emit and the method to call, in one place so that the loop and a generator can never disagree on what
     * a caller's shape is.
     * <p>
     * Matching is on the parameter types and on nothing else : the two interfaces are written independently,
     * so the names are not expected to agree. It has to be unambiguous, hence exactly one — an overload
     * taking the same types cannot exist, but a type holding two methods of different names over the same
     * parameters can, and that is refused here rather than resolved by a rule nobody would remember.
     * <p>
     * Void only, and for a reason that is not laziness : these shapes call every function once, so a return
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
