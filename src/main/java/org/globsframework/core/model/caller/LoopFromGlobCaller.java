package org.globsframework.core.model.caller;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.model.Glob;

/**
 * The {@link FromGlobCaller} of a type whose factory generates nothing : the plain loop over a
 * table of functions indexed by {@code Field.getIndex()}.
 * <p>
 * Behaviourally identical to a generated one — same functions, same order, and the same isSet / isNull /
 * value, isNull being what {@code getValue} answers null for. What it does not give is the point of
 * generating: one call site for the whole loop, seeing every function class it is ever handed, i.e. exactly
 * the megamorphic dispatch a generated caller exists to remove. It is the fallback, not an alternative.
 * <p>
 * Unlike a generated caller it accepts any Glob of the type, whoever built it.
 */
public class LoopFromGlobCaller<C1, C2> implements FromGlobCaller<C1, C2> {
    private final GlobType type;
    private final Field[] fields;
    private final FromGlobFunction<Object, C1, C2>[] functions;

    @SuppressWarnings("unchecked")
    public LoopFromGlobCaller(GlobType type, FromGlobCallerFactory.Functions<C1, C2> source) {
        this.type = type;
        Field[] typeFields = type.getFields();
        fields = new Field[typeFields.length];
        functions = new FromGlobFunction[typeFields.length];
        for (Field field : typeFields) {
            FromGlobFunction<Object, C1, C2> function =
                    (FromGlobFunction<Object, C1, C2>) source.forField(field);
            if (function == null) {
                throw new IllegalArgumentException("No FromGlobFunction for " + field.getName()
                                                   + " of " + type.getName());
            }
            fields[field.getIndex()] = field;
            functions[field.getIndex()] = function;
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
