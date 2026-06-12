package org.globsframework.core.metamodel.impl;

import org.globsframework.core.metamodel.DummyObject;
import org.globsframework.core.metamodel.fields.*;
import org.junit.jupiter.api.Test;

public class FieldTest {


    @Test
    void checkSwitchCaseCompile() {
        Field field = DummyObject.NAME;
        switch (field) {
            case BigDecimalArrayField bigDecimalArrayField -> {
            }
            case BigDecimalField bigDecimalField -> {
            }
            case BooleanArrayField booleanArrayField -> {
            }
            case BooleanField booleanField -> {
            }
            case BytesField bytesField -> {
            }
            case DateField dateField -> {
            }
            case DateTimeField dateTimeField -> {
            }
            case DoubleArrayField doubleArrayField -> {
            }
            case DoubleField doubleField -> {
            }
            case GlobArrayField globArrayField -> {
            }
            case GlobArrayUnionField globArrayUnionField -> {
            }
            case GlobField globField -> {
            }
            case GlobUnionField globUnionField -> {
            }
            case IntegerArrayField integerArrayField -> {
            }
            case IntegerField integerField -> {
            }
            case LongArrayField longArrayField -> {
            }
            case LongField longField -> {
            }
            case StringArrayField stringArrayField -> {
            }
            case StringField stringField -> {
            }
        }
    }
}
