package org.globsframework.core.model.impl;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.GlobTypeBuilderFactory;
import org.globsframework.core.metamodel.annotations.KeyField;
import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.MutableGlob;
import org.globsframework.core.model.utils.FieldCheck;
import org.globsframework.core.utils.exceptions.InvalidParameter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * What {@code AbstractDefaultGlob} still checks once the typed setters skip the value check — their Java signature
 * guarantees the value's class — while {@code setValue} keeps it: the Glob-valued setters check the target type
 * themselves.
 */
class SetterValueCheckTest {

    public static class A {
        public static final GlobType TYPE;
        public static final IntegerField X;

        static {
            GlobTypeBuilder builder = GlobTypeBuilderFactory.create("A");
            X = builder.declareIntegerField("x");
            TYPE = builder.build();
        }
    }

    public static class B {
        public static final GlobType TYPE;
        public static final IntegerField Y;

        static {
            GlobTypeBuilder builder = GlobTypeBuilderFactory.create("B");
            Y = builder.declareIntegerField("y");
            TYPE = builder.build();
        }
    }

    public static class Out {
        public static final GlobType TYPE;
        public static final IntegerField K;
        public static final IntegerField I;
        public static final BigDecimalField BD;
        public static final BigDecimalArrayField BDS;
        public static final GlobField<?> G;
        public static final GlobArrayField<?> GA;
        public static final GlobUnionField U;
        public static final GlobArrayUnionField UA;

        static {
            GlobTypeBuilder builder = GlobTypeBuilderFactory.create("Out");
            K = builder.declareIntegerField("k", KeyField.ZERO);
            I = builder.declareIntegerField("i");
            BD = builder.declareBigDecimalField("bd");
            BDS = builder.declareBigDecimalArrayField("bds");
            G = builder.declareGlobField("g", () -> A.TYPE);
            GA = builder.declareGlobArrayField("ga", () -> A.TYPE);
            @SuppressWarnings("unchecked") Supplier<GlobType>[] targets = new Supplier[]{() -> A.TYPE};
            U = builder.declareGlobUnionField("u", targets);
            UA = builder.declareGlobUnionArrayField("ua", targets);
            TYPE = builder.build();
        }
    }

    private static class SubBigDecimal extends BigDecimal {
        SubBigDecimal() {
            super("1");
        }
    }

    @BeforeEach
    void checksOn() {
        assumeTrue(FieldCheck.CheckGlob.shouldCheck, "run with the field checks on");
    }

    @Test
    void globFieldChecksItsTargetType() {
        assertDoesNotThrow(() -> Out.TYPE.instantiate().set(Out.G, A.TYPE.instantiate()));
        assertDoesNotThrow(() -> Out.TYPE.instantiate().set(Out.G, (Glob) null));
        assertThrows(InvalidParameter.class, () -> Out.TYPE.instantiate().set(Out.G, B.TYPE.instantiate()));
        assertThrows(InvalidParameter.class, () -> Out.TYPE.instantiate().setValue(Out.G, B.TYPE.instantiate()));
    }

    @Test
    void unionFieldChecksItsTargetTypes() {
        assertDoesNotThrow(() -> Out.TYPE.instantiate().set(Out.U, A.TYPE.instantiate()));
        assertDoesNotThrow(() -> Out.TYPE.instantiate().set(Out.U, (Glob) null));
        assertThrows(InvalidParameter.class, () -> Out.TYPE.instantiate().set(Out.U, B.TYPE.instantiate()));
    }

    @Test
    void unionFieldCheckBuildsItsLazyTargetsFirst() {
        // isValidType on a fresh type, before anything has asked for the target types
        GlobTypeBuilder builder = GlobTypeBuilderFactory.create("FreshUnion");
        @SuppressWarnings("unchecked") Supplier<GlobType>[] targets = new Supplier[]{() -> A.TYPE};
        GlobUnionField union = builder.declareGlobUnionField("u", targets);
        GlobType type = builder.build();
        assertDoesNotThrow(() -> type.instantiate().set(union, A.TYPE.instantiate()));
    }

    @Test
    void globArrayFieldsCheckEachElement() {
        assertDoesNotThrow(() -> Out.TYPE.instantiate().set(Out.GA, new Glob[]{A.TYPE.instantiate()}));
        assertThrows(InvalidParameter.class, () -> Out.TYPE.instantiate().set(Out.GA, new Glob[]{B.TYPE.instantiate()}));
        assertDoesNotThrow(() -> Out.TYPE.instantiate().set(Out.UA, new Glob[]{A.TYPE.instantiate()}));
        assertThrows(InvalidParameter.class, () -> Out.TYPE.instantiate().set(Out.UA, new Glob[]{B.TYPE.instantiate()}));
    }

    @Test
    void setValueChecksTheValueClass() {
        assertDoesNotThrow(() -> Out.TYPE.instantiate().setValue(Out.I, 3));
        assertThrows(InvalidParameter.class, () -> Out.TYPE.instantiate().setValue(Out.I, "3"));
        assertThrows(InvalidParameter.class, () -> Out.TYPE.instantiate().setValue(Out.I, 3L));
    }

    @Test
    void bigDecimalSettersAndSetValueAcceptASubclass() {
        // BigDecimal is not final: a subclass is a BigDecimal, typed setter and setValue both accept it
        assertDoesNotThrow(() -> Out.TYPE.instantiate().set(Out.BD, BigDecimal.ONE));
        assertDoesNotThrow(() -> Out.TYPE.instantiate().set(Out.BD, new SubBigDecimal()));
        assertDoesNotThrow(() -> Out.TYPE.instantiate().setValue(Out.BD, new SubBigDecimal()));
        assertDoesNotThrow(() -> Out.TYPE.instantiate().set(Out.BDS, new BigDecimal[]{BigDecimal.ONE}));
        assertDoesNotThrow(() -> Out.TYPE.instantiate().set(Out.BDS, new SubBigDecimal[]{new SubBigDecimal()}));
        assertDoesNotThrow(() -> Out.TYPE.instantiate().setValue(Out.BDS, new SubBigDecimal[]{new SubBigDecimal()}));
        // anything else is still refused by setValue
        assertThrows(InvalidParameter.class, () -> Out.TYPE.instantiate().setValue(Out.BD, 1.0));
        assertThrows(InvalidParameter.class, () -> Out.TYPE.instantiate().setValue(Out.BDS, new Double[]{1.0}));
    }

    @Test
    void typedSettersStillCheckTheFieldsType() {
        assertThrows(InvalidParameter.class, () -> Out.TYPE.instantiate().set(A.X, 3));
        assertThrows(InvalidParameter.class, () -> Out.TYPE.instantiate().setValue(A.X, 3));
    }

    @Test
    void aKeyCannotChangeOnceHashed() {
        MutableGlob glob = Out.TYPE.instantiate().set(Out.K, 1);
        glob.hashCode();
        assertThrows(RuntimeException.class, () -> glob.set(Out.K, 2));
        assertThrows(RuntimeException.class, () -> glob.setValue(Out.K, 2));
        assertDoesNotThrow(() -> glob.set(Out.I, 2));
    }
}
