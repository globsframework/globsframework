package org.globsframework.core.model.impl;

import org.globsframework.core.metamodel.*;
import org.globsframework.core.metamodel.annotations.KeyField;
import org.globsframework.core.metamodel.fields.IntegerField;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.Key;
import org.globsframework.core.model.KeyBuilder;
import org.globsframework.core.model.MutableGlob;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class CompositeKeyTest {

    @Test
    public void keyFieldNotAtBegin() throws Exception {
        GlobTypeBuilder builder = GlobTypeBuilderFactory.create("CompositeKey");
        builder.addStringField("field_1");
        IntegerField id1 = builder.declareIntegerField("id1", KeyField.ZERO);
        builder.addStringField("field_2");
        IntegerField id2 = builder.declareIntegerField("id2", KeyField.ONE);
        IntegerField id3 = builder.declareIntegerField("id3", KeyField.TWO);
        GlobType type = builder.build();
        Glob glob = type.instantiate().set(id1, 1).set(id2, 2).set(id3, 3);
        Key actual = KeyBuilder.init(type)
                .set(id1, 1)
                .set(id2, 2)
                .set(id3, 3)
                .get();
        assertEquals(glob.getKey(), actual);
        assertEquals(1, actual.get(id1).intValue());
        assertEquals(2, actual.get(id2).intValue());
        assertEquals(3, actual.get(id3).intValue());
    }

    @Test
    public void testCompare() {
        MutableGlob g1 = DummyObject.TYPE.instantiate()
                .set(DummyObject.ID, 1);
        MutableGlob g2 = DummyObject.TYPE.instantiate()
                .set(DummyObject.ID, 2);

        MutableGlob g3 = DummyObject2.TYPE.instantiate()
                .set(DummyObject2.ID, 1);

        MutableGlob g4 = DummyObjectWithCompositeKey.TYPE.instantiate().set(DummyObjectWithCompositeKey.ID1, 1)
                .set(DummyObjectWithCompositeKey.ID2, 2);

        assertEquals(-1, g1.getKey().compareTo(g2.getKey()));
        assertEquals(-1, g1.getKey().compareTo(g3.getKey()));
        assertEquals(-16, g1.getKey().compareTo(g4.getKey()));
        assertEquals(-16, g2.getKey().compareTo(g4.getKey()));
        assertEquals(0, g2.getKey().compareTo(g2.getKey()));
        assertEquals(1, g3.getKey().compareTo(g1.getKey()));
        assertEquals(16, g4.getKey().compareTo(g1.getKey()));
        assertEquals(0, g4.getKey().compareTo(g4.getKey()));
    }

}
