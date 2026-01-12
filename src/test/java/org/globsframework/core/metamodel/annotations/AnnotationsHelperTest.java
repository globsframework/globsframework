package org.globsframework.core.metamodel.annotations;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.GlobTypeBuilderFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

public class AnnotationsHelperTest {

    static public class Type1 {
        public static GlobType TYPE;

        static {
            final GlobTypeBuilder globTypeBuilder = GlobTypeBuilderFactory.create("Type1");
            TYPE = globTypeBuilder.build();
        }
    }

    static public class Type2 {
        public static GlobType OTHER_TYPE;
    }

    static public class Type3 {
        public static GlobType TYPE;
        public static GlobType OTHER_TYPE;
    }

    @Test
    public void findGlobType() {
        AnnotationsHelper.getType(Type1.class);
        try {
            AnnotationsHelper.getType(Type2.class);
            fail();
        } catch (RuntimeException e) {
        }
        try {
            AnnotationsHelper.getType(Type3.class);
            fail();
        } catch (RuntimeException e) {
        }
    }
}
