package org.globsframework.core.model.caller;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.model.Glob;

import java.lang.reflect.Method;

/**
 * Builds the caller of one GlobType : one pass over its fields, one function per field. Implemented by the
 * GlobFactory of a type whose implementation is generated (see {@link CallerGlobFactory}) — the interface
 * lives here so that a codec can be written against it without depending on the module that does the
 * generating.
 * <p>
 * An implementation is free to emit a class per call to {@link #create}, holding <em>these</em> functions in
 * its static finals — that is what makes each call site monomorphic, and it is why this belongs to the setup
 * phase of a serializer or a codec, not to its hot path. That emitted class is named after the {@code name}
 * given here : see {@link CallerName} for what to pass and why it is not optional.
 *
 * <h2>The caller's own interfaces</h2>
 * A caller is built over two types the <em>codec</em> owns : {@code tClass}, the interface the emitted class
 * implements, and {@code dClass}, what the functions are called through. Core names neither, and there is no
 * generic {@code FromGlobCaller}/{@code FromGlobFunction} pair any more — there used to be, carrying two
 * {@code Object} contexts, which meant a box per primitive context and an adapter in front of every function
 * whose real signature was something else.
 * <p>
 * {@code argument} is what the codec carries through the pass — its output, its context. The two methods are
 * built from it the same way on every call, and found by their parameter types rather than their names
 * ({@link CallerShape#methodMatching}) :
 * <pre>
 * tClass :  void &lt;anything&gt;(Glob data, argument...)
 * dClass :  void &lt;anything&gt;(boolean isSet, boolean isNull, Object value, argument...)
 *
 * interface GlobWriter  { void write(Glob data, CodedOutputStream out); }
 * interface FieldWriter { void write(boolean isSet, boolean isNull, Object value, CodedOutputStream out); }
 *
 * GlobWriter writer = FromGlobCallerFactory.callerFor("binser.write", type, functions, null,
 *         GlobWriter.class, FieldWriter.class, CodedOutputStream.class);
 * writer.write(glob, out);
 * </pre>
 * The <b>value stays an {@code Object}</b>, unlike everything else here : it is the one argument whose type
 * changes from one field to the next, so there is nothing for a codec to declare it as. A function takes the
 * value of the field it was built for, boxed on the primitive flavour.
 */
public interface FromGlobCallerFactory {

    /** What the caller's own method takes before {@code argument} : the Glob being walked. */
    Class<?>[] CALLER_BEFORE = {Glob.class};

    /** What each function takes before {@code argument} : the state of the field, and its value. */
    Class<?>[] FUNCTION_BEFORE = {boolean.class, boolean.class, Object.class};

    /**
     * @param name     what builds this caller, constant in the source — see {@link CallerName}. An
     *                 implementation that emits a class names it after this, so it is what makes that class
     *                 the same one from one run to the next.
     * @param order    the fields to call, in the order to call them, or <b>null</b> for every field of the
     *                 type in index order — see {@link #fieldsToCall}. A format whose layout is not the
     *                 type's declaration order passes its own here rather than reordering afterwards, which
     *                 a caller cannot do : it writes as it walks.
     * @param tClass   the interface to implement. An interface, because that is what there is to implement.
     * @param dClass   what the functions are called through. May be a class.
     * @param argument what the pass carries through, after the fixed parameters of each of the two methods.
     */
    <T, D> T create(String name, Functions<D> functions, Field[] order,
                    Class<T> tClass, Class<D> dClass, Class<?>... argument);

    /**
     * Called once per field <em>that will be called</em>, at generation time, to get the function that field
     * will be handled with. A field left out of {@code order} is never asked for and never called.
     */
    interface Functions<D> {
        D forField(Field field);
    }

    /** The method the emitted class implements : {@code (Glob, argument...)} on {@code tClass}. */
    static Method callerMethod(Class<?> tClass, Class<?>... argument) {
        CallerShape.checkInterface(tClass);
        return CallerShape.methodMatching(tClass, CallerShape.parameters(CALLER_BEFORE, argument));
    }

    /** The method it calls : {@code (isSet, isNull, value, argument...)} on {@code dClass}. */
    static Method functionMethod(Class<?> dClass, Class<?>... argument) {
        return CallerShape.methodMatching(dClass, CallerShape.parameters(FUNCTION_BEFORE, argument));
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
     * <li>{@link LoopFromGlobCallerFactory}, the loop, which works for anything.</li>
     * </ol>
     * A type has no generated factory when no generating GlobFactoryService is installed at all, and, in
     * globs-generate, when the type asks for {@code mode none} or has more than 64 fields.
     * <p>
     * Note what the loop costs since the shape became the codec's own : it can only answer a {@code tClass}
     * through a reflective {@link java.lang.reflect.Proxy}, which boxes every primitive argument on every
     * call. A codec with something better of its own — a table of per-field closures over typed accessors,
     * say — should ask {@link #generatedCallerFor} and keep its own path when that answers null, which is
     * what globs-bin-serialisation, globs-grpc and globs-fix all do.
     *
     * @param order what to call and in which order, or null for every field in index order.
     */
    static <T, D> T callerFor(String name, GlobType type, Functions<D> functions, Field[] order,
                              Class<T> tClass, Class<D> dClass, Class<?>... argument) {
        T generated = generatedCallerFor(name, type, functions, order, tClass, dClass, argument);
        return generated != null ? generated
                : new LoopFromGlobCallerFactory(type).create(name, functions, order, tClass, dClass, argument);
    }

    /**
     * The first two sources of {@link #callerFor}, without the third : **null** when nothing can generate a
     * caller for this type.
     * <p>
     * For a codec that already has something better than the loop to fall back on. Going through this rather
     * than testing {@link CallerGlobFactory} by hand is what makes {@code -Dglobs.caller.fromGlob} reach it.
     *
     * @param order what to call and in which order, or null for every field in index order.
     */
    static <T, D> T generatedCallerFor(String name, GlobType type, Functions<D> functions, Field[] order,
                                       Class<T> tClass, Class<D> dClass, Class<?>... argument) {
        // checked here rather than only in the generators, so that a JVM with nothing installed to generate
        // refuses the same names, the same orders and the same shapes as one that generates
        CallerName.check(name);
        CallerShape.checkInterface(tClass);
        Field[] fields = fieldsToCall(type, order);
        if (type.getGlobFactory() instanceof CallerGlobFactory globFactory) {
            return globFactory.create(name, functions, fields, tClass, dClass, argument);
        }
        FromGlobCallerService service = FromGlobCallerService.Builder.getService();
        if (service != null) {
            FromGlobCallerFactory factory = service.factoryFor(type);
            if (factory != null) {
                return factory.create(name, functions, fields, tClass, dClass, argument);
            }
        }
        return null;
    }
}
