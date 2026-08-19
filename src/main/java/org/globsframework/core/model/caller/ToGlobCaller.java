package org.globsframework.core.model.caller;

import org.globsframework.core.model.MutableGlob;

/**
 * Runs the loop a parser is : ask the {@link KeySource} what comes next, call the
 * {@link ToGlobFunction} it names, until it answers the {@code endLoop} value.
 * <p>
 * A generating implementation emits that loop as a switch over the keys, each case reading its function from
 * a {@code static final} of the generated class : one monomorphic call site per key.
 */
public interface ToGlobCaller<C1, C2, C3> {
    void call(KeySource keySource, MutableGlob data, C1 ctx1, C2 ctx2, C3 ctx3);
}
