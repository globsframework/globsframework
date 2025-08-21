package org.globsframework.core.functional.impl;

import org.globsframework.core.functional.FunctionalKeyBuilder;
import org.globsframework.core.functional.FunctionalKeyBuilderFactory;
import org.globsframework.core.functional.FunctionalKeyRepository;
import org.globsframework.core.metamodel.DummyObject;
import org.globsframework.core.metamodel.DummyObject2;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.model.Glob;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DefaultFunctionalKeyRepositoryTest {


    @Test
    public void testQuery() {
        DefaultFunctionalKeyRepository linkManager =
                new DefaultFunctionalKeyRepository((type) -> Stream.<Glob>of(
                        DummyObject.TYPE.instantiate().set(DummyObject.ID, 1).set(DummyObject.NAME, "a1")
                                .set(DummyObject.COUNT, 1),
                        DummyObject.TYPE.instantiate().set(DummyObject.ID, 2).set(DummyObject.NAME, "a1")
                                .set(DummyObject.COUNT, 2),
                        DummyObject.TYPE.instantiate().set(DummyObject.ID, 3).set(DummyObject.NAME, "a3")
                                .set(DummyObject.COUNT, 1),
                        DummyObject2.TYPE.instantiate().set(DummyObject2.ID, 1).set(DummyObject2.LABEL, "l1"),
                        DummyObject2.TYPE.instantiate().set(DummyObject2.ID, 2).set(DummyObject2.LABEL, "l2")
                ).filter(g -> g.getType() == type));

        checkUnique(linkManager);

        FunctionalKeyBuilderFactory multiFunctionalKeyBuilderFactory = FunctionalKeyBuilderFactory.create(DummyObject.TYPE);
        FunctionalKeyBuilder functionalKeyBuilderMuli = multiFunctionalKeyBuilderFactory.add(DummyObject.NAME).create();

        Collection<Glob> a11 = linkManager.get(functionalKeyBuilderMuli.create().set(DummyObject.NAME, "a1").getShared());
        assertEquals(2, a11.size());

        assertEquals(2, linkManager.getAll(functionalKeyBuilderMuli).count());
    }


    @Test
    public void testUniqueQuery() {
        DefaultUniqueFunctionalKeyRepository linkManager =
                new DefaultUniqueFunctionalKeyRepository((type) -> Stream.<Glob>of(
                        DummyObject.TYPE.instantiate().set(DummyObject.ID, 1).set(DummyObject.NAME, "a1")
                                .set(DummyObject.COUNT, 1),
                        DummyObject.TYPE.instantiate().set(DummyObject.ID, 2).set(DummyObject.NAME, "a1")
                                .set(DummyObject.COUNT, 2),
                        DummyObject.TYPE.instantiate().set(DummyObject.ID, 3).set(DummyObject.NAME, "a3")
                                .set(DummyObject.COUNT, 1),
                        DummyObject2.TYPE.instantiate().set(DummyObject2.ID, 1).set(DummyObject2.LABEL, "l1"),
                        DummyObject2.TYPE.instantiate().set(DummyObject2.ID, 2).set(DummyObject2.LABEL, "l2")
                ).filter(g -> g.getType() == type));

        checkUnique(linkManager);

    }

    private void checkUnique(FunctionalKeyRepository linkManager) {
        FunctionalKeyBuilder functionalKeyBuilder = FunctionalKeyBuilderFactory.create(DummyObject.TYPE)
                .add(DummyObject.NAME)
                .add(DummyObject.COUNT).create();

        Glob a1 = linkManager.get(functionalKeyBuilder.create().set(DummyObject.NAME, "a1")
                .set(DummyObject.COUNT, 2).getShared()).iterator().next();
        assertEquals(2, a1.get(DummyObject.ID).intValue());

        assertEquals(a1, linkManager.getUnique(functionalKeyBuilder.create().set(DummyObject.NAME, "a1")
                .set(DummyObject.COUNT, 2).getShared()));

        FunctionalKeyBuilder functionalKeyBuilder2 = FunctionalKeyBuilderFactory.create(DummyObject2.TYPE)
                .add(DummyObject2.LABEL).create();

        Glob l2 = linkManager.get(functionalKeyBuilder2.create().set(DummyObject2.LABEL, "l2").getShared()).iterator().next();
        assertEquals(2, l2.get(DummyObject2.ID).intValue());

        FunctionalKeyBuilderFactory functionalKeyBuilderFactory = FunctionalKeyBuilderFactory.create(DummyObject.TYPE);
        for (Field field : DummyObject.TYPE.getFields()) {
            functionalKeyBuilderFactory.add(field);
        }
        FunctionalKeyBuilder functionalKeyBuilderAll = functionalKeyBuilderFactory.create();

        Glob l3 = linkManager.get(functionalKeyBuilderAll.create().set(DummyObject.NAME, "a1")
                .set(DummyObject.ID, 2)
                .set(DummyObject.COUNT, 2).getShared()).iterator().next();
        assertEquals(2, l3.get(DummyObject.ID).intValue());

        assertEquals(3, linkManager.getAll(functionalKeyBuilder).count());

        FunctionalKeyBuilderFactory multiFunctionalKeyBuilderFactory = FunctionalKeyBuilderFactory.create(DummyObject.TYPE);
        FunctionalKeyBuilder functionalKeyBuilderMuli = multiFunctionalKeyBuilderFactory.add(DummyObject.NAME).create();

//        assertEquals(3, linkManager.getAll(functionalKeyBuilderMuli).count());
    }
}
