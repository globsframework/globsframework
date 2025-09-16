package org.globsframework.core.model;

import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.metamodel.fields.FieldValueVisitor;
import org.globsframework.core.metamodel.fields.FieldValueVisitorWithContext;

public interface FieldsValueScanner {
    static FieldsValueScanner from(FieldValue[] fieldValues) {
        return new FieldsValueScanner() {

            public <T extends FieldValues.Functor> T apply(T functor) throws Exception {
                for (FieldValue fieldValue : fieldValues) {
                    functor.process(fieldValue.getField(), fieldValue.getValue());
                }
                return functor;
            }

            public <T extends FieldValueVisitor> T accept(T functor) throws Exception {
                for (FieldValue fieldValue : fieldValues) {
                    fieldValue.getField().acceptValue(functor, fieldValue.getValue());
                }
                return functor;
            }

            public <CTX, T extends FieldValueVisitorWithContext<CTX>> T accept(T functor, CTX ctx) throws Exception{
                for (FieldValue fieldValue : fieldValues) {
                    fieldValue.getField().acceptValue(functor, fieldValue.getValue(), ctx);
                }
                return functor;
            }

        };
    }

    <T extends FieldValues.Functor> T apply(T functor) throws Exception;

    default <T extends FieldValues.Functor> T safeApply(T functor) {
        try {
            return apply(functor);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    <T extends FieldValueVisitor> T accept(T functor) throws Exception;

    default <T extends FieldValueVisitor> T safeAccept(T functor) {
        try {
            return accept(functor);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    <CTX, T extends FieldValueVisitorWithContext<CTX>> T accept(T functor, CTX ctx) throws Exception;

    default <CTX, T extends FieldValueVisitorWithContext<CTX>> T safeAccept(T functor, CTX ctx) {
        try {
            return accept(functor, ctx);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    default FieldsValueScanner withoutKey() {
        if (this instanceof WithoutKeyFieldsValueScanner) {
            return this;
        }
        return new WithoutKeyFieldsValueScanner(this);
    }

    default int size() {
        return safeApply(new FieldValues.Functor() {
            int count;

            public void process(Field field, Object value) throws Exception {
                count++;
            }
        }).count;
    }

    class WithoutKeyFieldsValueScanner implements FieldsValueScanner {
        private FieldsValueScanner valueScanner;

        public WithoutKeyFieldsValueScanner(FieldsValueScanner valueScanner) {
            this.valueScanner = valueScanner;
        }

        public <T extends FieldValues.Functor> T apply(T functor) throws Exception {
            valueScanner.apply(functor.withoutKeyField());
            return functor;
        }

        public <T extends FieldValues.Functor> T safeApply(T functor) {
            valueScanner.safeApply(functor.withoutKeyField());
            return functor;
        }


        public <T extends FieldValueVisitor> T accept(T functor) throws Exception {
            valueScanner.accept(functor.withoutKey());
            return functor;
        }

        public <CTX, T extends FieldValueVisitorWithContext<CTX>> T accept(T functor, CTX ctx) throws Exception{
            valueScanner.accept(functor.withoutKey(), ctx);
            return functor;
        }

        public <T extends FieldValueVisitor> T safeAccept(T functor) {
            valueScanner.safeAccept(functor.withoutKey());
            return functor;
        }
    }

    default Glob toGlob(Key key) {
        return safeApply(new FieldValues.Functor() {
            MutableGlob glob;

            {
                glob = key.getGlobType().instantiate();
                key.safeApplyOnKeyField(this);
            }

            public void process(Field field, Object value) throws Exception {
                glob.setValue(field, value);
            }
        }).glob;
    }
}
