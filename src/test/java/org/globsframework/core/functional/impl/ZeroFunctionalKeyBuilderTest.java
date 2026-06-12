package org.globsframework.core.functional.impl;

import org.globsframework.core.functional.FunctionalKey;
import org.globsframework.core.functional.FunctionalKeyBuilder;
import org.globsframework.core.functional.FunctionalKeyBuilderFactory;
import org.globsframework.core.metamodel.DummyObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ZeroFunctionalKeyBuilderTest {

    @Test
    void basic() {
        final FunctionalKeyBuilderFactory functionalKeyBuilderFactory = FunctionalKeyBuilderFactory.create(DummyObject.TYPE);
        final FunctionalKeyBuilder functionalKeyBuilder = functionalKeyBuilderFactory.create();
        final FunctionalKey k1 = functionalKeyBuilder.create().create();
        final FunctionalKey k2 = functionalKeyBuilder.create().create();
        Assertions.assertEquals(k1, k2);
    }
}