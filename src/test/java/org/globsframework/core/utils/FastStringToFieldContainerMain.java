package org.globsframework.core.utils;

import org.globsframework.core.metamodel.DummyObjectWithDefaultValues;
import org.globsframework.core.metamodel.fields.Field;
import org.junit.jupiter.api.Test;


public class FastStringToFieldContainerMain {
    private FastStringToFieldContainer fastStringToFieldContainer
            = new FastStringToFieldContainer(DummyObjectWithDefaultValues.TYPE.getFields());

    @Test
    public void test() {
        double d = 0;
        d = d + loop();
        d = d + loop();
        d = d + loop();
        NanoChrono nanoChrono = new NanoChrono();
        d = d + loop();
        System.out.println("FastStringToFieldContainerMain.test " + nanoChrono.getElapsedTimeInMS() + "    " + d);
    }

    private double loop() {
        double d = 0.;
        for (int i = 0; i < 100000; i++) {
            for (Field field : DummyObjectWithDefaultValues.TYPE.getFields()) {
                Field ff = fastStringToFieldContainer.get(field.getName());
//            Field ff = DummyObjectWithDefaultValues.TYPE.getField(field.getName());
                d += ff.getIndex();
            }
        }
        return d;
    }
}
