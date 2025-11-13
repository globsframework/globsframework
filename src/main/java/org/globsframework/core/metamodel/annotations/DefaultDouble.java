package org.globsframework.core.metamodel.annotations;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.GlobTypeBuilderFactory;
import org.globsframework.core.metamodel.fields.DoubleField;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.Key;
import org.globsframework.core.model.KeyBuilder;

public class DefaultDouble {
    public static final GlobType TYPE;

    public static final DoubleField VALUE;

    @InitUniqueKey
    public static final Key KEY;

    public static Glob create(double value) {
        return TYPE.instantiate().set(VALUE, value);
    }

    static {
        GlobTypeBuilder typeBuilder = GlobTypeBuilderFactory.create("DefaultDouble");
        TYPE = typeBuilder.unCompleteType();
        VALUE = typeBuilder.declareDoubleField("VALUE");
        typeBuilder.register(GlobCreateFromAnnotation.class, annotation -> create(((DefaultDouble_) annotation).value()));
        typeBuilder.complete();
        KEY = KeyBuilder.newEmptyKey(TYPE);
//        GlobTypeLoader loader = GlobTypeLoaderFactory.create(DefaultDouble.class, "DefaultDouble");
//        loader.register(GlobCreateFromAnnotation.class, annotation -> create(((DefaultDouble_) annotation).value()))
//                .load();
    }
}
