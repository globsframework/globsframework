package org.globsframework.core.metamodel.annotations;

import org.globsframework.core.metamodel.GlobModel;
import org.globsframework.core.metamodel.impl.DefaultGlobModel;

public class AllCoreAnnotations {
    static final public GlobModel MODEL =
            new DefaultGlobModel(AutoIncrement.TYPE,
                    ContainmentLink.TYPE, DefaultBoolean.TYPE,
                    DefaultDouble.TYPE,
                    DefaultInteger.TYPE, DefaultLong.TYPE,
                    DefaultString.TYPE, DoublePrecision.TYPE,
                    FieldName.TYPE, KeyField.TYPE,
                    EnumAnnotation.TYPE,
                    LinkModelName.TYPE, MaxSize.TYPE, MultiLineText.TYPE,
                    IgnoredAnnotation.TYPE,
                    Comment.TYPE,
                    NamingField.TYPE, Required.TYPE,
                    NotUniqueIndex.TYPE, FunctionalFieldOrder.TYPE,
                    ArraySize.TYPE);
}
