package org.globsframework.core.model.caller;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * What both sides of {@code model/caller} agree on about the pair of interfaces a caller is built over.
 * <p>
 * Neither side names a caller interface of its own any more : a caller supplies {@code tClass}, what the
 * emitted class implements, and {@code dClass}, what it calls, and the two are matched to each other by
 * their <em>parameter types</em>. The generic pairs this replaced carried {@code Object} contexts, which
 * meant a box per primitive, an adapter object in front of every function of another shape and one call
 * level more — measured in globs-off-heap at four times what generating the dispatch earns back.
 * <p>
 * The rules live here rather than on either factory so that the loops and the generators cannot disagree on
 * what a caller's shape is, and so that both sides refuse the same things with the same words.
 */
public final class CallerShape {

    private CallerShape() {
    }

    /** What is implemented has to be an interface, whichever shape is asked for. */
    public static void checkInterface(Class<?> tClass) {
        if (!tClass.isInterface()) {
            throw new IllegalArgumentException(tClass.getName() + " is not an interface : there would be "
                                               + "nothing to implement.");
        }
    }

    /** A missing function is refused when the caller is built, not when it runs. */
    public static <D> D checked(D function, String at) {
        if (function == null) {
            throw new IllegalArgumentException("No function for " + at);
        }
        return function;
    }

    /**
     * The one method of {@code type} taking exactly {@code argument} — how both sides find the method to
     * emit and the method to call.
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
    public static Method methodMatching(Class<?> type, Class<?>... argument) {
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

    /**
     * {@code before} then {@code argument} — how a side that puts fixed parameters in front of the ones the
     * caller chose builds the list to match on. The from-Glob side does it twice: the caller's method takes
     * the Glob first, each function takes isSet, isNull and the value first.
     */
    public static Class<?>[] parameters(Class<?>[] before, Class<?>... argument) {
        Class<?>[] parameters = new Class<?>[before.length + argument.length];
        System.arraycopy(before, 0, parameters, 0, before.length);
        System.arraycopy(argument, 0, parameters, before.length, argument.length);
        return parameters;
    }
}
