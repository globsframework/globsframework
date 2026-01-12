package org.globsframework.core.metamodel.utils;

import org.globsframework.core.metamodel.*;
import org.globsframework.core.metamodel.annotations.*;
import org.globsframework.core.metamodel.fields.IntegerField;
import org.globsframework.core.metamodel.impl.DefaultGlobModel;
import org.globsframework.core.metamodel.links.Link;
import org.globsframework.core.utils.Strings;
import org.globsframework.core.utils.TestUtils;
import org.globsframework.core.utils.exceptions.InvalidData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultGlobModelTest {
    private GlobModel inner = new DefaultGlobModel(DummyObject.TYPE);
    private GlobModel model = new DefaultGlobModel(inner, DummyObject2.TYPE);

    @Test
    public void testStandardCase() throws Exception {
        TestUtils.assertSetEquals(inner.getAll(), DummyObject.TYPE);
        TestUtils.assertSetEquals(model.getAll(), DummyObject.TYPE, DummyObject2.TYPE);

        assertEquals(DummyObject.TYPE, inner.getType(DummyObject.TYPE.getName()));
        assertEquals(DummyObject.TYPE, model.getType(DummyObject.TYPE.getName()));
        assertEquals(DummyObject2.TYPE, model.getType(DummyObject2.TYPE.getName()));
    }

    public static class LargeLinkCycle1 {
        public static GlobType TYPE;

        @KeyField_
        public static IntegerField ID;

        @Target(LargeLinkCycle2.class)
        public static IntegerField LINK_ID;

        public static Link LINK;

        static {
            final GlobTypeBuilder globTypeBuilder = GlobTypeBuilderFactory.create("LargeLinkCycle1");
            ID = globTypeBuilder.declareIntegerField("id", KeyField.ZERO);
            LINK_ID = globTypeBuilder.declareIntegerField("linkId");
            globTypeBuilder
                    .register(MutableGlobLinkModel.LinkRegister.class, mutableGlobLinkModel ->
                            LINK = mutableGlobLinkModel.getLinkBuilder("default", "link")
                                    .add(LINK_ID, LargeLinkCycle2.ID)
                                    .publish());
            TYPE = globTypeBuilder.build();
        }
    }

    public static class LargeLinkCycle2 {
        public static GlobType TYPE;

        @KeyField_
        public static IntegerField ID;

        @Target(LargeLinkCycle3.class)
        public static IntegerField LINK_ID;

        public static Link LINK;

        static {
            GlobTypeBuilder globTypeBuilder = GlobTypeBuilderFactory.create("LargeLinkCycle2");
            ID = globTypeBuilder.declareIntegerField("id", KeyField.ZERO);
            LINK_ID = globTypeBuilder.declareIntegerField("linkId");
            globTypeBuilder
                    .register(MutableGlobLinkModel.LinkRegister.class, mutableGlobLinkModel ->
                            LINK = mutableGlobLinkModel.getLinkBuilder(null, "link")
                                    .add(LINK_ID, LargeLinkCycle3.ID)
                                    .publish());
            TYPE = globTypeBuilder.build();

        }
    }

    public static class LargeLinkCycle3 {
        public static GlobType TYPE;

        @KeyField_
        public static IntegerField ID;

        @Target(LargeLinkCycle1.class)
        public static IntegerField LINK_ID;

        public static Link LINK;

        static {
            GlobTypeBuilder globTypeBuilder = GlobTypeBuilderFactory.create("LargeLinkCycle3");
            ID = globTypeBuilder.declareIntegerField("id", KeyField.ZERO);
            LINK_ID = globTypeBuilder.declareIntegerField("linkId");
            globTypeBuilder
                    .register(MutableGlobLinkModel.LinkRegister.class, mutableGlobLinkModel ->
                            LINK = mutableGlobLinkModel.getLinkBuilder("default", "link")
                                    .add(LINK_ID, LargeLinkCycle1.ID)
                                    .publish());
            TYPE = globTypeBuilder.build();

        }
    }

    @Test
    public void testDependencies() throws Exception {
        GlobModel model =
                GlobModelBuilder.create(LargeLinkCycle1.TYPE, LargeLinkCycle2.TYPE, LargeLinkCycle3.TYPE).get();
        GlobTypeDependencies dependencies = model.getDependencies();
        TestUtils.assertEquals(dependencies.getCreationSequence(), LargeLinkCycle3.TYPE, LargeLinkCycle2.TYPE,
                LargeLinkCycle1.TYPE);
        TestUtils.assertEquals(dependencies.getUpdateSequence(), LargeLinkCycle3.TYPE, LargeLinkCycle2.TYPE,
                LargeLinkCycle1.TYPE);
        TestUtils.assertEquals(dependencies.getDeletionSequence(), LargeLinkCycle1.TYPE, LargeLinkCycle2.TYPE,
                LargeLinkCycle3.TYPE);

        assertTrue(dependencies.needsPostUpdate(LargeLinkCycle3.TYPE));
        assertFalse(dependencies.needsPostUpdate(LargeLinkCycle1.TYPE));
        assertFalse(dependencies.needsPostUpdate(LargeLinkCycle2.TYPE));
    }

    @Test
    public void testDependenciesWithInnerModel() throws Exception {
        GlobModel inner = GlobModelBuilder.create(LargeLinkCycle1.TYPE).get();
        GlobModel model = GlobModelBuilder.create(inner, LargeLinkCycle2.TYPE, LargeLinkCycle3.TYPE).get();

        GlobTypeDependencies dependencies = model.getDependencies();
        TestUtils.assertEquals(dependencies.getCreationSequence(), LargeLinkCycle3.TYPE, LargeLinkCycle2.TYPE,
                LargeLinkCycle1.TYPE);
        TestUtils.assertEquals(dependencies.getUpdateSequence(), LargeLinkCycle3.TYPE, LargeLinkCycle2.TYPE,
                LargeLinkCycle1.TYPE);
        TestUtils.assertEquals(dependencies.getDeletionSequence(), LargeLinkCycle1.TYPE, LargeLinkCycle2.TYPE,
                LargeLinkCycle3.TYPE);

        assertTrue(dependencies.needsPostUpdate(LargeLinkCycle3.TYPE));
        assertFalse(dependencies.needsPostUpdate(LargeLinkCycle1.TYPE));
        assertFalse(dependencies.needsPostUpdate(LargeLinkCycle2.TYPE));
    }

    public static class LargeLinkCycleWithRequiredFieldError1 {
        public static GlobType TYPE;

        @KeyField_
        public static IntegerField ID;

        @Required_
        @Target(LargeLinkCycleWithRequiredFieldError2.class)
        public static IntegerField LINK_ID;

        public static Link LINK;

        static {
            final GlobTypeBuilder globTypeBuilder = GlobTypeBuilderFactory.create("largeLinkCycleWithRequiredFieldError1");
            ID = globTypeBuilder.declareIntegerField("id", KeyField.ZERO);
            LINK_ID = globTypeBuilder.declareIntegerField("linkId", Required.UNIQUE_GLOB);
            globTypeBuilder
                    .register(MutableGlobLinkModel.LinkRegister.class, mutableGlobLinkModel ->
                            LINK = LINK != null ? LINK : mutableGlobLinkModel.getLinkBuilder("default", "link")
                                    .add(LINK_ID, LargeLinkCycleWithRequiredFieldError2.ID)
                                    .publish());
            TYPE = globTypeBuilder.build();
        }
    }

    public static class LargeLinkCycleWithRequiredFieldError2 {
        public static GlobType TYPE;

        @KeyField_
        public static IntegerField ID;

        @Required_
        @Target(LargeLinkCycleWithRequiredFieldError3.class)
        public static IntegerField LINK_ID;

        public static Link LINK;

        static {
            final GlobTypeBuilder globTypeBuilder = GlobTypeBuilderFactory.create("largeLinkCycleWithRequiredFieldError2");
            ID = globTypeBuilder.declareIntegerField("id", KeyField.ZERO);
            LINK_ID = globTypeBuilder.declareIntegerField("linkId", Required.UNIQUE_GLOB);
            globTypeBuilder
                    .register(MutableGlobLinkModel.LinkRegister.class, mutableGlobLinkModel ->
                            LINK = LINK != null ? LINK : mutableGlobLinkModel.getLinkBuilder("default", "link")
                                    .add(LINK_ID, LargeLinkCycleWithRequiredFieldError3.ID)
                                    .publish());
            TYPE = globTypeBuilder.build();

        }
    }

    public static class LargeLinkCycleWithRequiredFieldError3 {
        public static GlobType TYPE;

        @KeyField_
        public static IntegerField ID;

        @Required_
        @Target(LargeLinkCycleWithRequiredFieldError1.class)
        public static IntegerField LINK_ID;

        public static Link LINK;

        static {
            final GlobTypeBuilder globTypeBuilder = GlobTypeBuilderFactory.create("largeLinkCycleWithRequiredFieldError3");
            ID = globTypeBuilder.declareIntegerField("id", KeyField.ZERO);
            LINK_ID = globTypeBuilder.declareIntegerField("linkId", Required.UNIQUE_GLOB);
            globTypeBuilder
                    .register(MutableGlobLinkModel.LinkRegister.class, mutableGlobLinkModel ->
                            LINK = LINK != null ? LINK : mutableGlobLinkModel.getLinkBuilder("default", "link")
                                    .add(LINK_ID, LargeLinkCycleWithRequiredFieldError1.ID)
                                    .publish());
            TYPE = globTypeBuilder.build();

        }
    }

    @Test
    public void testDependenciesWithLinkCycleWithRequiredFieldError() throws Exception {
        try {
            GlobModelBuilder.create(LargeLinkCycleWithRequiredFieldError1.TYPE, LargeLinkCycleWithRequiredFieldError2.TYPE, LargeLinkCycleWithRequiredFieldError3.TYPE)
                    .get();
            fail();
        } catch (InvalidData e) {
            assertEquals("Cycles found with required fields:" + Strings.LINE_SEPARATOR
                            + "'largeLinkCycleWithRequiredFieldError1' = 'largeLinkCycleWithRequiredFieldError1.linkId'"
                            + Strings.LINE_SEPARATOR
                            + "'largeLinkCycleWithRequiredFieldError2' = 'largeLinkCycleWithRequiredFieldError2.linkId'"
                            + Strings.LINE_SEPARATOR
                    , e.getMessage());
        }
    }
}
