package org.globsframework.core.functional.impl;

import org.globsframework.core.functional.FunctionalKeyBuilder;
import org.globsframework.core.functional.FunctionalKeyBuilderFactory;
import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.Field;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DefaultFunctionalKeyBuilderFactory implements FunctionalKeyBuilderFactory {
    private GlobType globType;
    private List<Field> keys = new ArrayList<>();

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
            if (keys.size() == 0) {
                throw new RuntimeException("No key in functional key for type " + globType);
            }
            if (keys.size() == 1) {
                return new OneFunctionalKeyBuilder(keys.get(0));
            }
            keys.sort(Comparator.comparingInt(Field::getIndex)); // ??supprimer le 11/12/2017
            if (keys.size() == 2) {
                return new TwoFunctionalKeyBuilder(keys.get(0), keys.get(1));
            }
            return new ManyFunctionalKeyBuilder(keys.toArray(new Field[0]));
        } finally {
            keys.clear();
        }
    }
}
