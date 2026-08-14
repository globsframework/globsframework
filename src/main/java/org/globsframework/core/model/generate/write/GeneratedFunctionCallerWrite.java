package org.globsframework.core.model.generate.write;

import java.util.SortedMap;

/**
 * Builds the callers of the write side. Core's own implementation is {@link DefaultFunctionCallerWrite}, the
 * plain loop; a module able to emit bytecode offers a better one through {@link GenerateCallerWriteService},
 * and the interface lives here so that a parser can be written against it without depending on that module.
 * <p>
 * A generating implementation is free to emit a class per call, holding <em>these</em> functions in its static
 * finals — that is what makes each call site monomorphic, and it is why this belongs to the setup phase of a
 * parser, not to its hot path.
 */
public interface GeneratedFunctionCallerWrite {

    /**
     * The dispatching caller : {@code functions} keyed by whatever the {@link CallAtWrite} answers.
     *
     * @param fallback what an unknown key goes to. null means there is none, and an unknown key then throws.
     * @param endLoop  the value that ends the pass. It is tested before the dispatch, so it may be a key of
     *                 the map — it is simply shadowed.
     */
    <Ctx1, Ctx2, Ctx3> GeneratedCallerWrite<Ctx1, Ctx2, Ctx3> create(SortedMap<Integer, MutableFunctionWrite<Ctx1, Ctx2, Ctx3>> functions,
                                                                     MutableFunctionWrite fallback, int endLoop);

    /** The unrolled caller : every function called once, in the order of the array. */
    <Ctx1, Ctx2, Ctx3> GeneratedCallerWriteAll<Ctx1, Ctx2, Ctx3> create(MutableFunctionWrite<Ctx1, Ctx2, Ctx3>[] functions);

    /**
     * What a parser builds its callers with : the {@link GenerateCallerWriteService} installed through
     * {@code -Dglobs.callerWrite}, or {@link DefaultFunctionCallerWrite} when there is none.
     * <p>
     * The two give the same behaviour — same order, same fallback, same end of loop — so a parser keeps one
     * code path and only the speed changes. This is the write side's answer to
     * {@code GenerateCaller.callerFor}, without the GlobType: nothing here depends on the type, since the
     * functions do their own writing through {@code MutableGlob}.
     */
    static GeneratedFunctionCallerWrite get() {
        GeneratedFunctionCallerWrite generated = getGenerated();
        return generated != null ? generated : DefaultFunctionCallerWrite.INSTANCE;
    }

    /**
     * The same resolution without the loop at the end : **null** when nothing is installed to generate. For a
     * parser that already has something better than {@link DefaultFunctionCallerWrite} to fall back on.
     */
    static GeneratedFunctionCallerWrite getGenerated() {
        GenerateCallerWriteService service = GenerateCallerWriteService.Builder.getService();
        return service != null ? service.getGenerateCallerWrite() : null;
    }

    /**
     * What every implementation throws for a key it has no function for, when it was built without a
     * fallback: a bug in the parser, not a case to skip silently. Shared so that the generated callers and
     * the loop say the same thing.
     */
    static RuntimeException unknownKey(int nextToCall) {
        return new IllegalStateException("No MutableFunctionWrite for " + nextToCall
                                         + " and no fallback was given.");
    }

    /** The other shared refusal : a missing function is refused when the caller is built, not when it runs. */
    static MutableFunctionWrite checked(MutableFunctionWrite function, String at) {
        if (function == null) {
            throw new IllegalArgumentException("No MutableFunctionWrite for " + at);
        }
        return function;
    }
}
