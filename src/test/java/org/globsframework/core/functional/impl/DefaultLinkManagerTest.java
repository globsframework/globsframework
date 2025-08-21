package org.globsframework.core.functional.impl;

import org.globsframework.core.functional.FunctionalKeyBuilder;
import org.globsframework.core.functional.FunctionalKeyBuilderFactory;
import org.globsframework.core.metamodel.DummyObject;
import org.globsframework.core.metamodel.DummyObject2;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.model.Glob;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DefaultLinkManagerTest {


    @Test
    public void testQuery() {

        DefaultLinkManager linkManager =
                new DefaultLinkManager(Arrays.<Glob>asList(
                        DummyObject.TYPE.instantiate().set(DummyObject.ID, 1).set(DummyObject.NAME, "a1")
                                .set(DummyObject.COUNT, 1),
                        DummyObject.TYPE.instantiate().set(DummyObject.ID, 2).set(DummyObject.NAME, "a1")
                                .set(DummyObject.COUNT, 2),
                        DummyObject.TYPE.instantiate().set(DummyObject.ID, 3).set(DummyObject.NAME, "a3")
                                .set(DummyObject.COUNT, 1),
                        DummyObject2.TYPE.instantiate().set(DummyObject2.ID, 1).set(DummyObject2.LABEL, "l1"),
                        DummyObject2.TYPE.instantiate().set(DummyObject2.ID, 2).set(DummyObject2.LABEL, "l2")
                ));

        FunctionalKeyBuilder functionalKeyBuilder = FunctionalKeyBuilderFactory.create(DummyObject.TYPE)
                .add(DummyObject.NAME)
                .add(DummyObject.COUNT).create();

        Glob a1 = linkManager.get(functionalKeyBuilder.create().set(DummyObject.NAME, "a1")
                .set(DummyObject.COUNT, 2).getShared());
        assertEquals(2, a1.get(DummyObject.ID).intValue());

        FunctionalKeyBuilder functionalKeyBuilder2 = FunctionalKeyBuilderFactory.create(DummyObject2.TYPE)
                .add(DummyObject2.LABEL).create();

        Glob l2 = linkManager.get(functionalKeyBuilder2.create().set(DummyObject2.LABEL, "l2").getShared());
        assertEquals(2, l2.get(DummyObject2.ID).intValue());

        FunctionalKeyBuilderFactory functionalKeyBuilderFactory = FunctionalKeyBuilderFactory.create(DummyObject.TYPE);
        for (Field field : DummyObject.TYPE.getFields()) {
            functionalKeyBuilderFactory.add(field);
        }
        FunctionalKeyBuilder functionalKeyBuilderAll = functionalKeyBuilderFactory.create();

        Glob l3 = linkManager.get(functionalKeyBuilderAll.create().set(DummyObject.NAME, "a1")
                .set(DummyObject.ID, 2)
                .set(DummyObject.COUNT, 2).getShared());
        assertEquals(2, l3.get(DummyObject.ID).intValue());


    }
}
