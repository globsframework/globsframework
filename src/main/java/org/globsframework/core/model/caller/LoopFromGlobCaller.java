package org.globsframework.core.model.caller;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.model.Glob;

/**
 * The {@link FromGlobCaller} of a type whose factory generates nothing : the plain loop over a
 * table of functions indexed by {@code Field.getIndex()}.
 * <p>
 * Behaviourally identical to a generated one — same functions, same order, and the same isSet / isNull /
 * value, isNull being what {@code getValue} answers null for. Like a generated one it walks the fields it was
 * given, in that order, or every field of the type in index order when it was given none. What it does not give is the point of
 * generating: one call site for the whole loop, seeing every function class it is ever handed, i.e. exactly
 * the megamorphic dispatch a generated caller exists to remove. It is the fallback, not an alternative.
 * <p>
 * Unlike a generated caller it accepts any Glob of the type, whoever built it.
 */
public class LoopFromGlobCaller<C1, C2> implements FromGlobCaller<C1, C2> {
    private final GlobType type;
    private final Field[] fields;
    private final FromGlobFunction<Object, C1, C2>[] functions;

    public LoopFromGlobCaller(GlobType type, FromGlobCallerFactory.Functions<C1, C2> source) {
        this(type, source, null);
    }

    /** @param order what to call and in which order, or null for every field in index order. */
    @SuppressWarnings("unchecked")
    public LoopFromGlobCaller(GlobType type, FromGlobCallerFactory.Functions<C1, C2> source, Field[] order) {
        this.type = type;
        fields = FromGlobCallerFactory.fieldsToCall(type, order);
        functions = new FromGlobFunction[fields.length];
        for (int i = 0; i < fields.length; i++) {
            FromGlobFunction<Object, C1, C2> function =
                    (FromGlobFunction<Object, C1, C2>) source.forField(fields[i]);
            if (function == null) {
                throw new IllegalArgumentException("No FromGlobFunction for " + fields[i].getName()
                                                   + " of " + type.getName());
            }
            functions[i] = function;
        }
    }

    public void call(Glob data, C1 ctx1, C2 ctx2) {
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            Object value = data.getValue(field);
            functions[i].call(data.isSet(field), value == null, value, ctx1, ctx2);
        }
    }

    public GlobType getType() {
        return type;
    }
}
