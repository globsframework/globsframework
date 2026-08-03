package org.globsframework.core.model.delta;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.model.*;

public class InDepthChangeSet extends DefaultChangeSet {

    protected DefaultDeltaGlob createDeltaGlob(Key key) {
        return new DefaultDeltaGlob(key);
    }

    public void processCreation(Key key, FieldsValueScanner values) {
        super.processCreation(key, values);
        process(values, key.getGlobType());
    }

    public void processCreation(GlobType type, FieldValues values) {
        super.processCreation(type, values);
        process(values, type);
    }

    private void process(FieldsValueScanner values, GlobType globType) {
        values.safeAccept(new FieldValueVisitor.AbstractFieldValueVisitor() {
            public void visitGlob(GlobField<?> field, Glob value) throws Exception {
                if (value != null) {
                    DefaultDeltaGlob deltaGlob = getGlob(value.getKey());
                    if (!deltaGlob.isModified()) {
                        processCreation(deltaGlob.getKey(), value);
                    }
                }
            }

            public void visitUnionGlob(GlobUnionField field, Glob value) throws Exception {
                if (value != null) {
                    DefaultDeltaGlob deltaGlob = getGlob(value.getKey());
                    if (!deltaGlob.isModified()) {
                        processCreation(deltaGlob.getKey(), value);
                    }
                }
            }

            public void visitUnionGlobArray(GlobArrayUnionField field, Glob[] value) throws Exception {
                if (value != null) {
                    for (Glob glob : value) {
                        DefaultDeltaGlob deltaGlob = getGlob(glob.getKey());
                        if (!deltaGlob.isModified()) {
                            processCreation(deltaGlob.getKey(), glob);
                        }
                    }
                }
            }

            public void visitGlobArray(GlobArrayField<?> field, Glob[] value) throws Exception {
                if (value != null) {
                    for (Glob glob : value) {
                        DefaultDeltaGlob deltaGlob = getGlob(glob.getKey());
                        if (!deltaGlob.isModified()) {
                            processCreation(deltaGlob.getKey(), glob);
                        }
                    }
                }
            }
        });
    }

    public void processUpdate(Key key, Field field, Object newValue, Object previousValue) {
        super.processUpdate(key, field, newValue, previousValue);

        propagateChanges(field, newValue, previousValue);
    }

    private void propagateChanges(Field field, Object newValue, Object previousValue) {
        if (field instanceof GlobField) {
            if (previousValue != null) {
                DefaultDeltaGlob glob1 = getGlob(((Glob) previousValue).getKey());
                if (!glob1.isModified()) {
                    processDeletion(glob1.getKey(), ((Glob) previousValue));
                }
            }
            if (newValue != null) {
                DefaultDeltaGlob glob1 = getGlob(((Glob) newValue).getKey());
                if (!glob1.isModified()) {
                    processCreation(glob1.getKey(), ((Glob) newValue));
                }
            }
        } else if (field instanceof GlobUnionField) {
            if (previousValue != null) {
                DefaultDeltaGlob glob1 = getGlob(((Glob) previousValue).getKey());
                if (!glob1.isModified()) {
                    processDeletion(glob1.getKey(), (Glob) previousValue);
                }
            }
            if (newValue != null) {
                DefaultDeltaGlob glob1 = getGlob(((Glob) newValue).getKey());
                if (!glob1.isModified()) {
                    processCreation(glob1.getKey(), (Glob) newValue);
                }
            }
        } else if (field instanceof GlobArrayUnionField) {
            if (previousValue != null) {
                for (Glob glob : ((Glob[]) previousValue)) {
                    DefaultDeltaGlob glob1 = getGlob(glob.getKey());
                    if (!glob1.isModified()) {
                        processDeletion(glob1.getKey(), glob);
                    }
                }
            }
            if (newValue != null) {
                for (Glob glob : ((Glob[]) newValue)) {
                    DefaultDeltaGlob glob1 = getGlob(glob.getKey());
                    if (!glob1.isModified()) {
                        processCreation(glob1.getKey(), glob);
                    }
                }
            }
        } else if (field instanceof GlobArrayField) {
            if (previousValue != null) {
                for (Glob glob : ((Glob[]) previousValue)) {
                    DefaultDeltaGlob glob1 = getGlob(glob.getKey());
                    if (!glob1.isModified()) {
                        processDeletion(glob1.getKey(), glob);
                    }
                }
            }
            if (newValue != null) {
                for (Glob glob : ((Glob[]) newValue)) {
                    DefaultDeltaGlob glob1 = getGlob(glob.getKey());
                    if (!glob1.isModified()) {
                        processCreation(glob1.getKey(), glob);
                    }
                }
            }
        }
    }

    public void processUpdate(Key key, FieldsValueWithPreviousScanner values) {
        super.processUpdate(key, values);
        values.safeApplyWithPrevious(new FieldValuesWithPrevious.FunctorWithPrevious() {
            public void process(Field field, Object value, Object previousValue) throws Exception {
                propagateChanges(field, value, previousValue);
            }
        });
    }

    public void processDeletion(Key key, FieldsValueScanner values) {
        super.processDeletion(key, values);
        values.safeAccept(new FieldValueVisitor.AbstractFieldValueVisitor() {
            public void visitGlob(GlobField<?> field, Glob value) throws Exception {
                if (value != null) {
                    DefaultDeltaGlob deltaGlob = getGlob(value.getKey());
                    if (!deltaGlob.isModified()) {
                        processDeletion(deltaGlob.getKey(), value);
                    }
                }
            }

            public void visitUnionGlob(GlobUnionField field, Glob value) throws Exception {
                if (value != null) {
                    DefaultDeltaGlob deltaGlob = getGlob(value.getKey());
                    if (!deltaGlob.isModified()) {
                        processDeletion(deltaGlob.getKey(), value);
                    }
                }
            }

            public void visitUnionGlobArray(GlobArrayUnionField field, Glob[] value) throws Exception {
                if (value != null) {
                    for (Glob glob : value) {
                        DefaultDeltaGlob deltaGlob = getGlob(glob.getKey());
                        if (!deltaGlob.isModified()) {
                            processDeletion(deltaGlob.getKey(), glob);
                        }
                    }
                }
            }

            public void visitGlobArray(GlobArrayField<?> field, Glob[] value) throws Exception {
                if (value != null) {
                    for (Glob glob : value) {
                        DefaultDeltaGlob deltaGlob = getGlob(glob.getKey());
                        if (!deltaGlob.isModified()) {
                            processDeletion(deltaGlob.getKey(), glob);
                        }
                    }
                }
            }
        });
    }
}
