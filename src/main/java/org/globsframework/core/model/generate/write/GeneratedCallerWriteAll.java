package org.globsframework.core.model.generate.write;

import org.globsframework.core.model.MutableGlob;

/**
 * The other half of {@link GeneratedCallerWrite} : no input to follow, every {@link MutableFunctionWrite}
 * called once, in the order of the array the caller was built from — a format whose entries are all there and
 * always in the same order.
 * <p>
 * A generating implementation unrolls that array, so here too each call site sees a single receiver.
 */
public interface GeneratedCallerWriteAll<Ctx1, Ctx2, Ctx3> {
    void call(MutableGlob data, Ctx1 ctx1, Ctx2 ctx2, Ctx3 ctx3);
}
