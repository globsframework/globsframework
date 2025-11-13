package org.globsframework.core.metamodel.annotations;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.GlobTypeBuilderFactory;
import org.globsframework.core.metamodel.fields.IntegerField;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.Key;
import org.globsframework.core.model.KeyBuilder;

public class DefaultInteger {
    public static final GlobType TYPE;

    public static final IntegerField VALUE;

    @InitUniqueKey
    public static final Key KEY;

    public static Glob create(int value) {
        return TYPE.instantiate().set(VALUE, value);
    }

    public static Glob create(DefaultInteger_ defaultDouble) {
        return TYPE.instantiate().set(VALUE, defaultDouble.value());
    }

    static {
        GlobTypeBuilder typeBuilder = GlobTypeBuilderFactory.create("DefaultInteger");
        TYPE = typeBuilder.unCompleteType();
        VALUE = typeBuilder.declareIntegerField("VALUE");
        typeBuilder.register(GlobCreateFromAnnotation.class, annotation -> create((DefaultInteger_) annotation));
        typeBuilder.complete();
        KEY = KeyBuilder.newEmptyKey(TYPE);

//        GlobTypeLoaderFactory.create(DefaultInteger.class, "DefaultInteger")
//                .register(GlobCreateFromAnnotation.class, annotation -> create((DefaultInteger_) annotation))
//                .load();
    }

}
