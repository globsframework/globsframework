package org.globsframework.core.model.generate;

/**
 * What a {@link GeneratedFunctionCaller} calls for one field of one GlobType.
 * <p>
 * One instance per field. A generating implementation holds each of them in a {@code static final} field and
 * calls it from an unrolled call site, so the receiver is a JIT constant and the call is monomorphic there —
 * which is the whole point of the exercise, against the single megamorphic call site a loop over the fields
 * gives.
 *
 * @param <T> the value type of the field this function was built for (Integer, String, ...)
 */
public interface FieldValueFunction<T, D, E> {

    void call(boolean isSet, boolean isNull, T value, D ctx1, E ctx2);
}
