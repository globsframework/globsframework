package org.globsframework.core.model.generate;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.Field;

/**
 * Builds a {@link GeneratedFunctionCaller} for one GlobType. Implemented by the GlobFactory of a type whose
 * implementation is generated (see {@link GlobGenerateFactory}) — the interface lives here so that a codec
 * can be written against it without depending on the module that does the generating.
 * <p>
 * An implementation is free to emit a class per call to {@link #create}, holding <em>these</em> functions in
 * its static finals — that is what makes each call site monomorphic, and it is why this belongs to the setup
 * phase of a serializer or a codec, not to its hot path.
 */
public interface GenerateCaller {

    <D, E>
    GeneratedFunctionCaller<D, E> create(GetFieldValueFunction<D, E> getFieldValueFunction);

    /** Called once per field, at generation time, to get the function that field will be handled with. */
    interface GetFieldValueFunction<D, E> {
        <T> FieldValueFunction<T, D, E> create(Field field);
    }

    /**
     * The caller of any type, generated or not : the generated one when the type's factory can build it, a
     * {@link DefaultFunctionCaller} otherwise. Callers get the same behaviour either way and never have to
     * carry a second code path — only the speed differs.
     * <p>
     * A type has no generated factory when no generating GlobFactoryService is installed at all, and, in
     * globs-generate, when the type asks for {@code mode none} or has more than 64 fields.
     */
    static <D, E> GeneratedFunctionCaller<D, E> callerFor(GlobType type, GetFieldValueFunction<D, E> getFieldValueFunction) {
        return type.getGlobFactory() instanceof GlobGenerateFactory generate
                ? generate.create(getFieldValueFunction)
                : new DefaultFunctionCaller<>(type, getFieldValueFunction);
    }
}
