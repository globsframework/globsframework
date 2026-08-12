package org.globsframework.core.model.generate.read;

import org.globsframework.core.model.Glob;

/**
 * Applies the {@link FieldValueFunction}s it was built from to every field of a Glob, in one pass.
 * <p>
 * A generating implementation may read the fields of its own Glob class directly, in which case the Glob has
 * to be one the type's own factory produced and anything else is a ClassCastException.
 * {@link DefaultFunctionCaller} takes any Glob of the type.
 */
public interface GeneratedFunctionCaller<D, E> {
    void call(Glob data, D ctx1, E ctx2);
}
