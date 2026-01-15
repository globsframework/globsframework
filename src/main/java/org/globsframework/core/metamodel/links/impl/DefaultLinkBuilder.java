package org.globsframework.core.metamodel.links.impl;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.MutableGlobLinkModel;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.metamodel.links.DirectLink;
import org.globsframework.core.metamodel.links.Link;
import org.globsframework.core.utils.collections.Pair;
import org.globsframework.core.utils.exceptions.InvalidParameter;

import java.util.ArrayList;
import java.util.List;

public abstract class DefaultLinkBuilder<T extends MutableGlobLinkModel.LinkBuilder<T>> implements MutableGlobLinkModel.LinkBuilder<T> {
    private final String modelName;
    private final String name;
    private final boolean required;
    private final List<Pair<Field, Field>> mappings = new ArrayList<>();
    private GlobType sourceType;
    private GlobType targetType;

    public DefaultLinkBuilder(String modelName, String name, boolean required) {
        this.modelName = modelName;
        this.name = name;
        this.required = required;
    }

    public String getModelName() {
        return modelName;
    }

    public String getName() {
        return name;
    }

    abstract T getT();

    public T add(Field sourceField, Field targetField) {
        if (sourceField == null) {
            throw new IllegalArgumentException("Source field for link " + name + " must not be null (circular reference " +
                                               "in static initialisation)");
        }
        if (targetField == null) {
            throw new InvalidParameter("Target field for link " + name + " must not be null (circular reference " +
                                       "in static initialisation)");
        }
        if (sourceType == null) {
            sourceType = sourceField.getGlobType();
        } else if (!sourceField.getGlobType().equals(sourceType)) {
            throw new InvalidParameter("Source field '" + sourceField + "' is not a field of type " +
                                       sourceType);
        }

        GlobType targetFieldType = targetField.getGlobType();
        if (targetType == null) {
            targetType = targetFieldType;
        } else if (!targetType.equals(targetFieldType)) {
            throw new InvalidParameter(
                    "Two different target types found for link '" + name + "' of type '" + sourceType.getName() +
                    "' (" + targetType.getName() + " and " + targetFieldType.getName() + ")");
        }
        mappings.add(Pair.makePair(sourceField, targetField));
        return getT();
    }

    public DirectLink asDirectLink() {
        if (mappings.isEmpty()) {
            throw new RuntimeException("No mapping defined for link " + modelName + " " + name);
        } else {
            return DefaultDirectLink.create(mappings, modelName, name, required);
        }
    }

    public Link asLink() {
        return asDirectLink();
    }
}
