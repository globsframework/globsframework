package org.globsframework.core.model.generate.write;

import org.globsframework.core.model.MutableGlob;

/**
 * Runs the loop a parser is : ask the {@link CallAtWrite} what comes next, call the
 * {@link MutableFunctionWrite} it names, until it answers the {@code endLoop} value.
 * <p>
 * A generating implementation emits that loop as a switch over the keys, each case reading its function from
 * a {@code static final} of the generated class : one monomorphic call site per key.
 */
public interface GeneratedCallerWrite<Ctx1, Ctx2, Ctx3> {
    void call(CallAtWrite callAt, MutableGlob data, Ctx1 ctx1, Ctx2 ctx2, Ctx3 ctx3);
}
