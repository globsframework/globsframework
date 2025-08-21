package org.globsframework.core.metamodel.annotations;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeLoaderFactory;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class AnnotationsHelperTest {

    static public class Type1 {
        public static GlobType TYPE;

        static {
            GlobTypeLoaderFactory.createAndLoad(Type1.class);
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
