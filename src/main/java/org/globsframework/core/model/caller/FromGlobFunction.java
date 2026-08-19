package org.globsframework.core.model.caller;

/**
 * What a {@link FromGlobCaller} calls for one field of one GlobType.
 * <p>
 * One instance per field. A generating implementation holds each of them in a {@code static final} field and
 * calls it from an unrolled call site, so the receiver is a JIT constant and the call is monomorphic there —
 * which is the whole point of the exercise, against the single megamorphic call site a loop over the fields
 * gives.
 *
 * @param <T> the value type of the field this function was built for (Integer, String, ...)
 */
public interface FromGlobFunction<T, C1, C2> {

    void call(boolean isSet, boolean isNull, T value, C1 ctx1, C2 ctx2);
}
