package org.globsframework.core.utils.serialization;

import org.globsframework.core.metamodel.GlobModel;
import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.GlobInstantiator;
import org.globsframework.core.model.MutableGlob;

public class GlobDeSerializer {
    private final GlobInputStreamFieldVisitor fieldVisitorInput;
    private final GlobInstantiator instantiator;
    private final SerializedInput input;

    public GlobDeSerializer(SerializedInput input) {
        this.input = input;
        instantiator = GlobType::instantiate;
        fieldVisitorInput = new GlobInputStreamFieldVisitor(input, this);
    }

    public GlobDeSerializer(SerializedInput input, GlobInstantiator instantiator) {
        this.input = input;
        this.instantiator = instantiator;
        fieldVisitorInput = new GlobInputStreamFieldVisitor(input, this);
    }

    public Glob readGlob(GlobModel model) {
        GlobType globType = model.getType(input.readUtf8String());
        MutableGlob mutableGlob = globType.instantiate();
        for (Field field : globType.getFields()) {
            field.safeAccept(fieldVisitorInput, mutableGlob);
        }
        return mutableGlob;
    }

    public Glob readKnowGlob(GlobType type) {
        MutableGlob mutableGlob = instantiator.newGlob(type);
        for (Field field : type.getFields()) {
            field.safeAccept(fieldVisitorInput, mutableGlob);
        }
        return mutableGlob;
    }

}
