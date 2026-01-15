package org.globsframework.core.metamodel.links.impl;

import org.globsframework.core.metamodel.Annotations;
import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.annotations.Required;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.metamodel.links.DirectLink;
import org.globsframework.core.metamodel.links.FieldMappingFunction;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.Key;
import org.globsframework.core.model.KeyBuilder;
import org.globsframework.core.utils.collections.Pair;
import org.globsframework.core.utils.exceptions.InvalidParameter;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultDirectLink extends AbstractLink implements DirectLink {
    private final List<Pair<Field, Field>> mappings;
    private final GlobType sourceType;
    private final GlobType targetType;

    public DefaultDirectLink(List<Pair<Field, Field>> mappings, String modelName, String name, boolean required) {
        super(modelName, name, required);
        this.mappings = mappings;
        Pair<Field, Field> mapping = mappings.get(0);
        sourceType = mapping.getFirst().getGlobType();
        targetType = mapping.getSecond().getGlobType();
    }

    public GlobType getSourceType() {
        return sourceType;
    }

    public GlobType getTargetType() {
        return targetType;
    }


    public <T extends FieldMappingFunction> T apply(T functor) {
        mappings.forEach(pair -> functor.process(pair.getFirst(), pair.getSecond()));
        return functor;
    }

    public boolean isContainment() {
        return false;
    }

    public Key getTargetKey(Glob source) {
        KeyBuilder keyBuilder = KeyBuilder.init(targetType);
        mappings.forEach(pair -> keyBuilder.setObject(pair.getSecond(), source.getValue(pair.getSecond())));
        return keyBuilder.get();
    }

    public static DirectLink create(List<Pair<Field, Field>> mappings, String modelName, String name, boolean required) {
        Field first = null;
        Field second = null;
        for (Pair<Field, Field> mapping : mappings) {
            first = first == null ? mapping.getFirst() : first;
            if (first.getGlobType() != mapping.getFirst().getGlobType()) {
                throw new InvalidParameter("field source must come from the same type " + first.getFullName() + " != " + mapping.getFirst().getFullName());
            }
            second = second == null ? mapping.getSecond() : second;
            if (second.getGlobType() != mapping.getSecond().getGlobType()) {
                throw new InvalidParameter("field target must come from the same type " + second.getFullName() + " != " + mapping.getSecond().getFullName());
            }
        }
        if (second.getGlobType().getKeyFields().length != mappings.size()) {
            Set<Field> keySet = mappings.stream().map(Pair::getSecond).collect(Collectors.toSet());
            List<Field> missing = Arrays.stream(second.getGlobType().getKeyFields())
                    .filter(field -> !keySet.contains(field)).collect(Collectors.toList());
            throw new InvalidParameter("All key field of target must be references. Missing : " + missing);
        }
        if (mappings.size() == 1) {
            return new DefaultDirectSingleLink(first, second, modelName, name, required);
        } else {
            return new DefaultDirectLink(mappings, modelName, name, required);
        }
    }
}
