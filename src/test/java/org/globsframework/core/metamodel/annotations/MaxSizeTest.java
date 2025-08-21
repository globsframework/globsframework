package org.globsframework.core.metamodel.annotations;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.GlobTypeBuilderFactory;
import org.globsframework.core.metamodel.fields.GlobField;
import org.globsframework.core.metamodel.fields.StringField;
import org.globsframework.core.model.MutableGlob;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class MaxSizeTest {

    @Test
    public void deepSize() {
        {
            MutableGlob g1 = Dummy_Level1.TYPE.instantiate()
                    .set(Dummy_Level1.VALUE_1, "123")
                    .set(Dummy_Level1.VALUE_2, "12345");
            MutableGlob mutableGlob = MaxSize.deepInPlaceTruncate(g1);
            assertEquals(mutableGlob.get(Dummy_Level1.VALUE_2), "123");
            assertEquals(mutableGlob.get(Dummy_Level1.VALUE_1), "123");

            MutableGlob g2 = Dummy_Level1.TYPE.instantiate()
                    .set(Dummy_Level1.VALUE_1, "12345")
                    .set(Dummy_Level1.VALUE_2, "12345");

            try {
                MaxSize.deepInPlaceTruncate(g2);
                fail();
            } catch (MaxSize.StringToLongException e) {
            }
        }
        {
            MutableGlob g1 = Dummy_Level1.TYPE.instantiate()
                    .set(Dummy_Level1.VALUE_1, "123")
                    .set(Dummy_Level1.VALUE_2, "12345");

            MutableGlob g2 = Dummy_Level1.TYPE.instantiate()
                    .set(Dummy_Level1.UNDER, g1);

            MaxSize.deepInPlaceTruncate(g2);

            assertEquals(g2.get(Dummy_Level1.UNDER).get(Dummy_Level1.VALUE_2), "123");

        }
        {
            MutableGlob g1 = Dummy_Level1.TYPE.instantiate()
                    .set(Dummy_Level1.VALUE_2, "éà");

            MutableGlob g2 = Dummy_Level1.TYPE.instantiate()
                    .set(Dummy_Level1.UNDER, g1);

            MaxSize.deepInPlaceTruncate(g2);

            assertEquals(g2.get(Dummy_Level1.UNDER).get(Dummy_Level1.VALUE_2), "é");

        }
    }

    public static class Dummy_Level1 {
        public static GlobType TYPE;

        @MaxSize_(3)
        public static StringField VALUE_1;

        @MaxSize_(value = 3, allow_truncate = true)
        public static StringField VALUE_2;

        @MaxSize_(value = 50, allow_truncate = true)
        public static StringField VALUE_3;

        @Target(Dummy_Level1.class)
        public static GlobField UNDER;

        static {
//            GlobTypeLoaderFactory.create(Dummy_Level1.class).load();
            GlobTypeBuilder typeBuilder = GlobTypeBuilderFactory.create("Dummy_Level1");
            TYPE = typeBuilder.unCompleteType();
            VALUE_1 = typeBuilder.declareStringField("VALUE_1", MaxSize.create(3));
            VALUE_2 = typeBuilder.declareStringField("VALUE_2", MaxSize.create(3, true));
            VALUE_3 = typeBuilder.declareStringField("VALUE_3", MaxSize.create(50, true));
            UNDER = typeBuilder.declareGlobField("UNDER", Dummy_Level1.TYPE);
            typeBuilder.get();
        }
    }
}
