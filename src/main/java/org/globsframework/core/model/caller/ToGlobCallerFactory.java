package org.globsframework.core.model.caller;

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
                                                 ToGlobFunction fallback, int endLoop);

    /**
     * The unrolled caller : every function called once, in the order of the array.
     *
     * @param name what builds this caller — see {@link #create(String, SortedMap, ToGlobFunction, int)}.
     */
    <C1, C2, C3> ToGlobCallerAll<C1, C2, C3> create(String name,
                                                    ToGlobFunction<C1, C2, C3>[] functions);

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
}
