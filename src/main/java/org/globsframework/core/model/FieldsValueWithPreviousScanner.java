package org.globsframework.core.model;

import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.metamodel.fields.FieldValueVisitor;
import org.globsframework.core.metamodel.fields.FieldValueVisitorWithContext;

public interface FieldsValueWithPreviousScanner extends FieldsValueScanner {
    <T extends FieldValueVisitor> T acceptOnPrevious(T functor) throws Exception;

    <T extends FieldValuesWithPrevious.FunctorWithPrevious> T applyWithPrevious(T functor) throws Exception;

    <T extends FieldValues.Functor> T applyOnPrevious(T functor) throws Exception;

    default <T extends FieldValues.Functor> T apply(T functor) throws Exception {
        applyWithPrevious(new FieldValuesWithPrevious.FunctorWithPrevious() {
            public void process(Field field, Object value, Object previousValue) throws Exception {
                functor.process(field, value);
            }
        });
        return functor;
    }

    default <T extends FieldValuesWithPrevious.FunctorWithPrevious> T safeApplyWithPrevious(T functor) {
        try {
            return applyWithPrevious(functor);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    default <T extends FieldValueVisitor> T safeAcceptOnPrevious(T functor) {
        try {
            return acceptOnPrevious(functor);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    default FieldsValueWithPreviousScanner withoutKey() {
        if (this instanceof WithoutKeyFieldsValueWithPreviousScanner) {
            return this;
        }
        return new WithoutKeyFieldsValueWithPreviousScanner(this);
    }

    class WithoutKeyFieldsValueWithPreviousScanner implements FieldsValueWithPreviousScanner {
        private FieldsValueWithPreviousScanner scanner;

        public WithoutKeyFieldsValueWithPreviousScanner(FieldsValueWithPreviousScanner scanner) {
            this.scanner = scanner;
        }

        public <T extends FieldValueVisitor> T acceptOnPrevious(T functor) throws Exception {
            scanner.acceptOnPrevious(functor.withoutKey());
            return functor;
        }

        public <T extends FieldValueVisitor> T safeAcceptOnPrevious(T functor) {
            scanner.safeAcceptOnPrevious(functor.withoutKey());
            return functor;
        }

        public <T extends FieldValuesWithPrevious.FunctorWithPrevious> T applyWithPrevious(T functor) throws Exception {
            scanner.applyWithPrevious(functor.previousWithoutKey());
            return functor;
        }

        public <T extends FieldValuesWithPrevious.FunctorWithPrevious> T safeApplyWithPrevious(T functor) {
            scanner.safeApplyWithPrevious(functor.previousWithoutKey());
            return functor;
        }

        public <T extends FieldValues.Functor> T applyOnPrevious(T functor) throws Exception {
            scanner.applyOnPrevious(functor.withoutKeyField());
            return functor;
        }

        public <T extends FieldValues.Functor> T apply(T functor) throws Exception {
            scanner.apply(functor.withoutKeyField());
            return functor;
        }

        public <T extends FieldValues.Functor> T safeApply(T functor) {
            scanner.safeApply(functor.withoutKeyField());
            return functor;
        }

        public <T extends FieldValueVisitor> T accept(T functor) throws Exception {
            scanner.accept(functor.withoutKey());
            return functor;
        }

        public <CTX, T extends FieldValueVisitorWithContext<CTX>> T accept(T functor, CTX ctx) throws Exception {
            scanner.accept(functor.withoutKey(), ctx);
            return functor;
        }

        public <T extends FieldValueVisitor> T safeAccept(T functor) {
            scanner.safeAccept(functor.withoutKey());
            return functor;
        }
    }
}
