package org.globsframework.core.model.caller;

import org.globsframework.core.model.MutableGlob;

/**
 * What a {@link ToGlobCaller} or a {@link ToGlobCallerAll} calls for one entry — typically
 * "read the value that comes next in the input and set it on this field".
 * <p>
 * One instance per entry. A generating implementation holds each of them in a {@code static final} field and
 * calls it from a call site of its own — one per {@code case} of the emitted switch, one per element of the
 * unrolled loop — so the receiver is a JIT constant there and the call is monomorphic, against the single
 * megamorphic call site a hand written loop over a table of functions gives.
 * <p>
 * Public because an implementation of {@link ToGlobCallerFactory} lives outside this package.
 */
public interface ToGlobFunction<C1, C2, C3> {
    void call(MutableGlob mutableGlob, C1 ctx1, C2 ctx2, C3 ctx3);
}
