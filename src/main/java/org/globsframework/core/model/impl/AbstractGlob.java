package org.globsframework.core.model.impl;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.model.*;
import org.globsframework.core.utils.exceptions.ItemNotFound;

public interface AbstractGlob extends AbstractFieldValues, Glob, Key {

    GlobType getType();

    Object doGet(Field field);

    // we don't want to add a dependency on any json framework here : we output a json like string here => need help : may be a bad idea
    default void toString(StringBuilder buffer) {
        buffer.append("{ \"_kind\":\"").append(escapeQuote(getType().getName())).append("\"");

        GlobType type = getType();
        for (Field field : type.getFields()) {
            if (isSet(field)) {
                buffer.append(", ");
                buffer.append("\"").append(escapeQuote(field.getName())).append("\":");
                field.toString(buffer, doGet(field));
            }
        }
        buffer.append("}");
    }

    private String escapeQuote(String name) {
        return name.contains("\"") ? name.replace("\"", "'") : name;
    }

    default boolean matches(FieldValues values) {
        return values.safeApply(new Functor() {
            Boolean result = Boolean.TRUE;

            public void process(Field field, Object value) {
                if (!field.valueEqual(value, getValue(field))) {
                    result = Boolean.FALSE;
                }
            }
        }).result;
    }

    default boolean matches(FieldValue... values) {
        for (FieldValue value : values) {
            if (!value.getField().valueEqual(value.getValue(), getValue(value.getField()))) {
                return false;
            }
        }
        return true;
    }

    default <T extends FieldValueVisitor> T acceptOnKeyField(T functor) throws Exception {
        for (Field field : getType().getFields()) {
            field.acceptValue(functor, doGet(field));
        }
        return functor;
    }

    default <T extends FieldValueVisitor> T safeAcceptOnKeyField(T functor) {
        try {
            for (Field field : getType().getKeyFields()) {
                field.acceptValue(functor, doGet(field));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return functor;
    }

    default <T extends FieldValues.Functor>
    T applyOnKeyField(T functor) throws Exception {
        for (Field field : getType().getFields()) {
            functor.process(field, doGet(field));
        }
        return functor;
    }

    default <T extends FieldValues.Functor>
    T safeApplyOnKeyField(T functor) {
        try {
            for (Field field : getType().getKeyFields()) {
                functor.process(field, doGet(field));
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return functor;
    }

    default <CTX, T extends FieldValueVisitorWithContext<CTX>>
    T acceptOnKeyField(T functor, CTX ctx) throws Exception {
        for (Field field : getType().getKeyFields()) {
            field.acceptValue(functor, doGet(field), ctx);
        }
        return functor;
    }

    // implement asFieldValues for key
    default FieldValues asFieldValues() {
        return new AbstractFieldValues() {
            GlobType type = getType();

            public boolean isSet(Field field) throws ItemNotFound {
                return AbstractGlob.this.isSet(field);
            }

            public boolean contains(Field field) {
                return field.isKeyField();
            }

            public int size() {
                return type.getKeyFields().length;
            }

            public <T extends Functor>
            T apply(T functor) throws Exception {
                for (Field field : type.getKeyFields()) {
                    functor.process(field, AbstractGlob.this.doGet(field));
                }
                return functor;
            }

            public <T extends FieldValueVisitor> T accept(T functor) throws Exception {
                for (Field field : type.getKeyFields()) {
                    field.acceptValue(functor, AbstractGlob.this.doGet(field));
                }
                return functor;
            }

            public <CTX, T extends FieldValueVisitorWithContext<CTX>> T accept(T functor, CTX ctx) throws Exception {
                for (Field field : type.getKeyFields()) {
                    field.acceptValue(functor, AbstractGlob.this.doGet(field), ctx);
                }
                return functor;
            }

            public FieldValue[] toArray() {
                FieldValue[] arrays = new FieldValue[type.getKeyFields().length];
                int i = 0;
                for (Field field : type.getKeyFields()) {
                    if (isSet(field)) {
                        arrays[i] = new FieldValue(field, AbstractGlob.this.doGet(field));
                        i++;
                    }
                }
                return arrays;
            }

            public Object doCheckedGet(Field field) {
                return AbstractGlob.this.doGet(field);
            }

        };
    }

    default boolean contains(Field field) {
        return field.getGlobType().equals(getType());
    }

    default int size() {
        return getType().getFieldCount();
    }

    default GlobType getGlobType() {
        return getType();
    }

    private boolean reallyEquals(Glob glob) {
        GlobType type = getType();
        for (Field field : type.getFields()) {
            if (!field.valueEqual(getValue(field), glob.getValue(field))) {
                return false;
            }
        }
        return true;
    }

    default MutableGlob asMutableGlob() {
        return duplicate();
    }

    default MutableGlob duplicate() {
        MutableGlob instantiate = getType().instantiate();
        for (Field field : getType().getFields()) {
            if (isSet(field)) {
                if (isNull(field)) {
                    instantiate.setValue(field, null);
                } else {
                    // 10% faster than a switch case
                    field.safeAccept(DuplicateFieldVisitorWithTwoContext.INSTANCE, instantiate, this);
                }
            }
        }
        return instantiate;
    }


    class DuplicateFieldVisitorWithTwoContext implements FieldVisitorWithTwoContext<MutableGlob, Glob> {
        static DuplicateFieldVisitorWithTwoContext INSTANCE = new DuplicateFieldVisitorWithTwoContext();

        public void visitInteger(IntegerField field, MutableGlob instantiate, Glob src) throws Exception {
            instantiate.set(field, src.get(field));
        }

        public void visitIntegerArray(IntegerArrayField field, MutableGlob instantiate, Glob src) throws Exception {
            instantiate.set(field, src.get(field).clone());
        }

        public void visitDouble(DoubleField field, MutableGlob instantiate, Glob src) throws Exception {
            instantiate.set(field, src.get(field));
        }

        public void visitDoubleArray(DoubleArrayField field, MutableGlob instantiate, Glob src) throws Exception {
            instantiate.set(field, src.get(field).clone());
        }

        public void visitBigDecimal(BigDecimalField field, MutableGlob instantiate, Glob src) throws Exception {
            instantiate.set(field, src.get(field));
        }

        public void visitBigDecimalArray(BigDecimalArrayField field, MutableGlob instantiate, Glob src) throws Exception {
            instantiate.set(field, src.get(field).clone());
        }

        public void visitString(StringField field, MutableGlob instantiate, Glob src) throws Exception {
            instantiate.set(field, src.get(field));
        }

        public void visitStringArray(StringArrayField field, MutableGlob instantiate, Glob src) throws Exception {
            instantiate.set(field, src.get(field).clone());
        }

        public void visitBoolean(BooleanField field, MutableGlob instantiate, Glob src) throws Exception {
            instantiate.set(field, src.get(field));
        }

        public void visitBooleanArray(BooleanArrayField field, MutableGlob instantiate, Glob src) throws Exception {
            instantiate.set(field, src.get(field).clone());
        }

        public void visitLong(LongField field, MutableGlob instantiate, Glob src) throws Exception {
            instantiate.set(field, src.get(field));
        }

        public void visitLongArray(LongArrayField field, MutableGlob instantiate, Glob src) throws Exception {
            instantiate.set(field, src.get(field).clone());
        }

        public void visitDate(DateField field, MutableGlob instantiate, Glob src) throws Exception {
            instantiate.set(field, src.get(field));
        }

        public void visitDateTime(DateTimeField field, MutableGlob instantiate, Glob src) throws Exception {
            instantiate.set(field, src.get(field));
        }

        public void visitBytes(BytesField field, MutableGlob instantiate, Glob src) throws Exception {
            instantiate.set(field, src.get(field).clone());
        }

        public void visitGlob(GlobField field, MutableGlob instantiate, Glob src) throws Exception {
            instantiate.set(field, src.get(field).duplicate());
        }

        public void visitGlobArray(GlobArrayField field, MutableGlob instantiate, Glob src) throws Exception {
            instantiate.set(field, duplicate(src.get(field)));
        }

        private static Glob[] duplicate(Glob[] globs) {
            Glob[] duplicate = new MutableGlob[globs.length];
            for (int i = 0; i < duplicate.length; i++) {
                duplicate[i] = globs[i].duplicate();
            }
            return duplicate;
        }

        public void visitUnionGlob(GlobUnionField field, MutableGlob instantiate, Glob src) throws Exception {
            instantiate.set(field, src.get(field).duplicate());
        }

        public void visitUnionGlobArray(GlobArrayUnionField field, MutableGlob instantiate, Glob src) throws Exception {
            Glob[] globs = src.get(field);
            final Glob[] duplicate = duplicate(globs);
            instantiate.set(field, duplicate);
        }
    }
}
