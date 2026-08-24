package org.globsframework.core.model.caller;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.Field;

/**
 * Builds a {@link FromGlobCaller} for one GlobType. Implemented by the GlobFactory of a type whose
 * implementation is generated (see {@link CallerGlobFactory}) — the interface lives here so that a codec
 * can be written against it without depending on the module that does the generating.
 * <p>
 * An implementation is free to emit a class per call to {@link #create}, holding <em>these</em> functions in
 * its static finals — that is what makes each call site monomorphic, and it is why this belongs to the setup
 * phase of a serializer or a codec, not to its hot path. That emitted class is named after the {@code name}
 * given here : see {@link CallerName} for what to pass and why it is not optional.
 */
public interface FromGlobCallerFactory {

    /**
     * @param name  what builds this caller, constant in the source — see {@link CallerName}. An
     *              implementation that emits a class names it after this, so it is what makes that class the
     *              same one from one run to the next.
     * @param order the fields to call, in the order to call them, or <b>null</b> for every field of the type
     *              in index order — see {@link #fieldsToCall}. A format whose layout is not the type's
     *              declaration order passes its own here rather than reordering afterwards, which a caller
     *              cannot do : it writes as it walks.
     */
    <C1, C2>
    FromGlobCaller<C1, C2> create(String name, Functions<C1, C2> functions, Field[] order);

    /** Every field of the type, in index order. */
    default <C1, C2>
    FromGlobCaller<C1, C2> create(String name, Functions<C1, C2> functions) {
        return create(name, functions, null);
    }

    /**
     * Called once per field <em>that will be called</em>, at generation time, to get the function that field
     * will be handled with. A field left out of {@code order} is never asked for and never called.
     */
    interface Functions<C1, C2> {
        <T> FromGlobFunction<T, C1, C2> forField(Field field);
    }

    /**
     * What a caller actually walks : {@code order} when it was given one, every field of the type in index
     * order when it was not.
     * <p>
     * Shared so that the loop and every generator refuse the same arrays — a field of another type, the same
     * field twice, a null — when the caller is built rather than when it runs. The array is copied : what a
     * caller walks cannot change under it afterwards.
     * <p>
     * An order may name <em>fewer</em> fields than the type has, and the ones left out are simply not called.
     * That is not a mistake to guard against: a codec that binds part of a type says so here instead of
     * handing out a do-nothing function per unbound field.
     */
    static Field[] fieldsToCall(GlobType type, Field[] order) {
        if (order == null) {
            // a copy : getFields() hands out the type's own array, and this one is kept for the life of the
            // caller
            return type.getFields().clone();
        }
        Field[] fields = new Field[order.length];
        boolean[] seen = new boolean[type.getFieldCount()];
        for (int i = 0; i < order.length; i++) {
            Field field = order[i];
            if (field == null) {
                throw new IllegalArgumentException("null field at " + i + " in the order given for "
                                                   + type.getName());
            }
            if (field.getGlobType() != type) {
                throw new IllegalArgumentException(field.getFullName() + " is not a field of "
                                                   + type.getName());
            }
            if (seen[field.getIndex()]) {
                throw new IllegalArgumentException(field.getFullName()
                                                   + " appears twice in the order given for " + type.getName());
            }
            seen[field.getIndex()] = true;
            fields[i] = field;
        }
        return fields;
    }

    /**
     * The caller of any type, generated or not. Callers get the same behaviour whichever comes out and never
     * have to carry a second code path — only the speed differs. Three sources, in order:
     * <ol>
     * <li>the type's own factory, when it is a {@link CallerGlobFactory} : it knows its Glob's layout, so
     * nothing can do better;</li>
     * <li>the {@link FromGlobCallerService} installed through {@code -Dglobs.caller.fromGlob}, which is how a
     * generator
     * offers a caller over a Glob it did not build — core's DefaultGlob;</li>
     * <li>{@link LoopFromGlobCaller}, the loop, which works for anything.</li>
     * </ol>
     * A type has no generated factory when no generating GlobFactoryService is installed at all, and, in
     * globs-generate, when the type asks for {@code mode none} or has more than 64 fields.
     */
    static <C1, C2> FromGlobCaller<C1, C2> callerFor(String name, GlobType type,
                                                     Functions<C1, C2> functions) {
        return callerFor(name, type, functions, null);
    }

    /** @param order what to call and in which order, or null for every field in index order. */
    static <C1, C2> FromGlobCaller<C1, C2> callerFor(String name, GlobType type,
                                                     Functions<C1, C2> functions, Field[] order) {
        FromGlobCaller<C1, C2> generated = generatedCallerFor(name, type, functions, order);
        return generated != null ? generated : new LoopFromGlobCaller<>(type, functions, order);
    }

    /**
     * The first two sources of {@link #callerFor}, without the third : **null** when nothing can generate a
     * caller for this type.
     * <p>
     * For a caller that already has something better than {@link LoopFromGlobCaller} to fall back on —
     * a codec holding a table of per-field closures over typed accessors, say, which its own loop walks
     * faster than the loop here does through {@code Glob.getValue}. Going through this rather than testing
     * {@link CallerGlobFactory} by hand is what makes {@code -Dglobs.caller.fromGlob} reach it.
     */
    static <C1, C2> FromGlobCaller<C1, C2> generatedCallerFor(String name, GlobType type,
                                                              Functions<C1, C2> functions) {
        return generatedCallerFor(name, type, functions, null);
    }

    /** @param order what to call and in which order, or null for every field in index order. */
    static <C1, C2> FromGlobCaller<C1, C2> generatedCallerFor(String name, GlobType type,
                                                              Functions<C1, C2> functions, Field[] order) {
        // checked here rather than only in the generators, so that a JVM with nothing installed to generate
        // refuses the same names and the same orders as one that generates
        CallerName.check(name);
        Field[] fields = fieldsToCall(type, order);
        if (type.getGlobFactory() instanceof CallerGlobFactory globFactory) {
            return globFactory.create(name, functions, fields);
        }
        FromGlobCallerService service = FromGlobCallerService.Builder.getService();
        if (service != null) {
            FromGlobCallerFactory factory = service.factoryFor(type);
            if (factory != null) {
                return factory.create(name, functions, fields);
            }
        }
        return null;
    }
}
