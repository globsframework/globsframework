package org.globsframework.core.metamodel;

import org.globsframework.core.metamodel.annotations.NamingField;
import org.globsframework.core.utils.TestUtils;
import org.junit.jupiter.api.Test;

public class GlobTypeTest {

    @Test
    public void testRetrieveFieldFromAnnotation() {
        TestUtils.assertContains(DummyObjectWithMaxSizeString.TYPE
                .getFieldsWithAnnotation(NamingField.KEY), DummyObjectWithMaxSizeString.TEXT);

    }

}
