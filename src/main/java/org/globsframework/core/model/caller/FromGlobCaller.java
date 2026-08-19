package org.globsframework.core.model.caller;

import org.globsframework.core.model.Glob;

/**
 * Applies the {@link FromGlobFunction}s it was built from to every field of a Glob, in one pass.
 * <p>
 * A generating implementation may read the fields of its own Glob class directly, in which case the Glob has
 * to be one the type's own factory produced and anything else is a ClassCastException.
 * {@link LoopFromGlobCaller} takes any Glob of the type.
 */
public interface FromGlobCaller<C1, C2> {
    void call(Glob data, C1 ctx1, C2 ctx2);
}
