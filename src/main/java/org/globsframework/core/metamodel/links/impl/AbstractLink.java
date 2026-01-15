package org.globsframework.core.metamodel.links.impl;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.links.Link;

public abstract class AbstractLink  implements Link {
    private final String modelName;
    private final String name;
    private final boolean required;

    public AbstractLink(String modelName, String name, boolean required) {
        this.modelName = modelName;
        this.name = name;
        this.required = required;
    }
    public boolean isRequired() {
        return required;
    }


    public String getLinkModelName() {
        return modelName;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return toString(name, getSourceType(), getTargetType());
    }

    public static String toString(String name, GlobType sourceType, GlobType targetType) {
        return name + "[" + sourceType.getName() + " => " +
                targetType.getName() + "]";
    }

}
