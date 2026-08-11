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
     * The caller of any type, generated or not. Callers get the same behaviour whichever comes out and never
     * have to carry a second code path — only the speed differs. Three sources, in order:
     * <ol>
     * <li>the type's own factory, when it is a {@link GlobGenerateFactory} : it knows its Glob's layout, so
     * nothing can do better;</li>
     * <li>the {@link GenerateCallerService} installed through {@code -Dglobs.caller}, which is how a generator
     * offers a caller over a Glob it did not build — core's DefaultGlob;</li>
     * <li>{@link DefaultFunctionCaller}, the loop, which works for anything.</li>
     * </ol>
     * A type has no generated factory when no generating GlobFactoryService is installed at all, and, in
     * globs-generate, when the type asks for {@code mode none} or has more than 64 fields.
     */
    static <D, E> GeneratedFunctionCaller<D, E> callerFor(GlobType type, GetFieldValueFunction<D, E> getFieldValueFunction) {
        GeneratedFunctionCaller<D, E> generated = generatedCallerFor(type, getFieldValueFunction);
        return generated != null ? generated : new DefaultFunctionCaller<>(type, getFieldValueFunction);
    }

    /**
     * The first two sources of {@link #callerFor}, without the third : **null** when nothing can generate a
     * caller for this type.
     * <p>
     * For a caller that already has something better than {@link DefaultFunctionCaller} to fall back on —
     * a codec holding a table of per-field closures over typed accessors, say, which its own loop walks
     * faster than the loop here does through {@code Glob.getValue}. Going through this rather than testing
     * {@link GlobGenerateFactory} by hand is what makes {@code -Dglobs.caller} reach it.
     */
    static <D, E> GeneratedFunctionCaller<D, E> generatedCallerFor(GlobType type,
                                                                   GetFieldValueFunction<D, E> getFieldValueFunction) {
        if (type.getGlobFactory() instanceof GlobGenerateFactory generate) {
            return generate.create(getFieldValueFunction);
        }
        GenerateCallerService service = GenerateCallerService.Builder.getService();
        if (service != null) {
            GenerateCaller generator = service.getGenerateCaller(type);
            if (generator != null) {
                return generator.create(getFieldValueFunction);
            }
        }
        return null;
    }
}
