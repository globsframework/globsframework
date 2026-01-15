package org.globsframework.core.metamodel.links.impl;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.metamodel.links.DirectSingleLink;
import org.globsframework.core.metamodel.links.FieldMappingFunction;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.Key;
import org.globsframework.core.model.KeyBuilder;

public class DefaultDirectSingleLink extends AbstractLink implements DirectSingleLink {
    private final Field sourceField;
    private final Field targetField;

    public DefaultDirectSingleLink(Field sourceField, Field targetField, String modelName, String name, boolean required) {
        super(modelName, name, required);
        this.sourceField = sourceField;
        this.targetField = targetField;
    }

    public GlobType getSourceType() {
        return sourceField.getGlobType();
    }

    public GlobType getTargetType() {
        return targetField.getGlobType();
    }


    public <T extends FieldMappingFunction> T apply(T functor) {
        functor.process(sourceField, targetField);
        return functor;
    }

    public boolean isContainment() {
        return false;
    }

    public Field getSourceField() {
        return sourceField;
    }

    public Field getTargetField() {
        return targetField;
    }

    public Key getTargetKey(Glob source) {
        return KeyBuilder.newKey(targetField, source.getValue(sourceField));
    }
}
