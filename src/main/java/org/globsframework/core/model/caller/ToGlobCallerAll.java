package org.globsframework.core.model.caller;

import org.globsframework.core.model.MutableGlob;

/**
 * The other half of {@link ToGlobCaller} : no input to follow, every {@link ToGlobFunction}
 * called once, in the order of the array the caller was built from — a format whose entries are all there and
 * always in the same order.
 * <p>
 * A generating implementation unrolls that array, so here too each call site sees a single receiver.
 */
public interface ToGlobCallerAll<C1, C2, C3> {
    void call(MutableGlob data, C1 ctx1, C2 ctx2, C3 ctx3);
}
