package org.globsframework.core.functional.impl;

import org.globsframework.core.functional.FunctionalKeyBuilder;
import org.globsframework.core.functional.FunctionalKeyBuilderFactory;
import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.Field;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DefaultFunctionalKeyBuilderFactory implements FunctionalKeyBuilderFactory {
    private final GlobType globType;
    private final List<Field> keys = new ArrayList<>();

    public DefaultFunctionalKeyBuilderFactory(GlobType globType) {
        this.globType = globType;
    }

    public FunctionalKeyBuilderFactory add(Field field) {
        if (globType != field.getGlobType()) {
            throw new RuntimeException("Bug : " + field.getFullName() + " is not own by " + globType.getName());
        }
        keys.add(field);
        return this;
    }

    public FunctionalKeyBuilder create() {
        try {
            if (keys.isEmpty()) {
                throw new RuntimeException("No key in functional key for type " + globType);
            }
            if (keys.size() == 1) {
                return new OneFunctionalKeyBuilder(keys.getFirst());
            }
            if (keys.size() == 2) {
                return new TwoFunctionalKeyBuilder(keys.get(0), keys.get(1));
            }
            return new ManyFunctionalKeyBuilder(keys.toArray(new Field[0]));
        } finally {
            keys.clear();
        }
    }
}
