package org.globsframework.core.metamodel.data;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class DataVisitorTest {

    @Test
    @Disabled
    public void name() {
        DataVisitor dataVisitor = null;

        dataVisitor
                .pushObject()
                .attribute("toto").push(1)
                .attribute("titi")
                .pushObject()
                .attribute("a").push(3.14)
                .attribute("values").pushArray().push(2.2).push(3.3).endArray()
                .endObject()
                .endObject();


//        .;
    }
}
