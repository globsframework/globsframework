package org.globsframework.core.model.utils;

import org.globsframework.core.metamodel.*;
import org.globsframework.core.metamodel.annotations.KeyField_;
import org.globsframework.core.metamodel.annotations.Target;
import org.globsframework.core.metamodel.fields.IntegerField;
import org.globsframework.core.metamodel.fields.StringField;
import org.globsframework.core.metamodel.links.Link;
import org.globsframework.core.model.ChangeSet;
import org.globsframework.core.xml.XmlChangeSetParser;
import org.globsframework.core.xml.XmlChangeSetVisitor;
import org.globsframework.core.xml.tests.XmlTestUtils;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import java.io.StringReader;
import java.io.StringWriter;

public class ChangeSetSequencerTest {

    public static class ObjectWithCompositeKey {
        public static GlobType TYPE;

        @KeyField_
        public static IntegerField ID1;

        @KeyField_
        public static IntegerField ID2;

        public static StringField NAME;

        static {
            GlobTypeLoaderFactory.createAndLoad(ObjectWithCompositeKey.class, true);
        }
    }

    public static class LinkedToObjectWithCompositeKey {
        public static GlobType TYPE;

        @KeyField_
        public static IntegerField ID;

        public static IntegerField LINK1;

        public static IntegerField LINK2;

        public static Link LINK;

        static {
            GlobTypeLoader loader = GlobTypeLoaderFactory.create(LinkedToObjectWithCompositeKey.class, true);
            loader.register(MutableGlobLinkModel.LinkRegister.class, mutableGlobLinkModel -> {
                LINK = mutableGlobLinkModel.getDirectLinkBuilder(LINK)
                        .add(LinkedToObjectWithCompositeKey.LINK1, ObjectWithCompositeKey.ID1)
                        .add(LinkedToObjectWithCompositeKey.LINK2, ObjectWithCompositeKey.ID2)
                        .publish();
            });
            loader.load();
        }
    }

    @Test
    public void testSingleType() throws Exception {
        checkSequence("<changes>"
                        + "  <delete type='objectWithCompositeKey' id1='0' id2='3'/>"
                        + "  <create type='objectWithCompositeKey' id1='0' id2='1'/>"
                        + "  <update type='objectWithCompositeKey' id1='0' id2='2' name='newName'/>"
                        + "</changes>",
                "<changes>"
                        + "  <create type='objectWithCompositeKey' id1='0' id2='1'/>"
                        + "  <update type='objectWithCompositeKey' id1='0' id2='2' name='newName' _name='(null)'/>"
                        + "  <delete type='objectWithCompositeKey' id1='0' id2='3'/>"
                        + "</changes>");
    }

    @Test
    public void testSingleTypeWithUpdateOnCreate() throws Exception {
        checkSequence("<changes>"
                        + "  <create type='objectWithCompositeKey' id1='0' id2='1'/>"
                        + "  <update type='objectWithCompositeKey' id1='0' id2='1' name='newName'/>"
                        + "</changes>",
                "<changes>"
                        + "  <create type='objectWithCompositeKey' id1='0' id2='1' name='newName'/>"
                        + "</changes>");
    }

    public static class ObjectWithSelfReference {
        public static GlobType TYPE;

        @KeyField_
        public static IntegerField ID;

        public static IntegerField LINK_ID;

        public static Link LINK;

        static {
            GlobTypeLoader loader = GlobTypeLoaderFactory.create(ObjectWithSelfReference.class, true);
            loader.register(MutableGlobLinkModel.LinkRegister.class,
                    mutableGlobLinkModel -> LINK =
                            mutableGlobLinkModel.getDirectLinkBuilder(LINK)
                                    .add(LINK_ID, ID).publish());
            loader.load();
        }
    }

    @Test
    public void testObjectWithSelfReference() throws Exception {
        try {
            checkSequence("<changes>"
                            + "  <create type='objectWithSelfReference' id='0' linkId='1'/>"
                            + "  <create type='objectWithSelfReference' id='1' linkId='0'/>"
                            + "</changes>",
                    "<changes>"
                            + "  <create type='objectWithSelfReference' id='1'/>"
                            + "  <create type='objectWithSelfReference' id='0'/>"
                            + "  <update type='objectWithSelfReference' id='1' linkId='0' _linkId='(null)'/>"
                            + "  <update type='objectWithSelfReference' id='0' linkId='1' _linkId='(null)'/>"
                            + "</changes>");
        } catch (AssertionFailedError e) {
            // the order withing the create and update sequences may vary
            checkSequence("<changes>"
                            + "  <create type='objectWithSelfReference' id='0' linkId='1'/>"
                            + "  <create type='objectWithSelfReference' id='1' linkId='0'/>"
                            + "</changes>",
                    "<changes>"
                            + "  <create type='objectWithSelfReference' id='0'/>"
                            + "  <create type='objectWithSelfReference' id='1'/>"
                            + "  <update type='objectWithSelfReference' id='0' linkId='1' _linkId='(null)'/>"
                            + "  <update type='objectWithSelfReference' id='1' linkId='0' _linkId='(null)'/>"
                            + "</changes>");
        }
    }

    public static class LinkCycle1 {
        public static GlobType TYPE;

        @KeyField_
        public static IntegerField ID1;

        @KeyField_
        public static IntegerField ID2;

        @Target(ObjectWithCompositeKey.class)
        public static IntegerField LINK1;

        @Target(ObjectWithCompositeKey.class)
        public static IntegerField LINK2;

        public static Link LINK;

        static {
            GlobTypeLoader loader = GlobTypeLoaderFactory.create(LinkCycle1.class, true);
            loader.register(MutableGlobLinkModel.LinkRegister.class, mutableGlobLinkModel ->
                    LINK = mutableGlobLinkModel.getLinkBuilder(LINK)
                            .add(LINK1, LinkCycle2.ID1)
                            .add(LINK2, LinkCycle2.ID2).publish());
            loader.load();
        }
    }

    public static class LinkCycle2 {
        public static GlobType TYPE;

        @KeyField_
        public static IntegerField ID1;

        @KeyField_
        public static IntegerField ID2;

        @Target(ObjectWithCompositeKey.class)
        public static IntegerField LINK1;

        @Target(ObjectWithCompositeKey.class)
        public static IntegerField LINK2;

        public static Link LINK;

        static {
            GlobTypeLoader loader = GlobTypeLoaderFactory.create(LinkCycle2.class, true);
            loader.register(MutableGlobLinkModel.LinkRegister.class, mutableGlobLinkModel ->
                    LINK = mutableGlobLinkModel.getLinkBuilder(LINK)
                            .add(LINK1, LinkCycle1.ID1)
                            .add(LINK2, LinkCycle1.ID2).publish());
            loader.load();
        }
    }

    @Test
    public void testLink() throws Exception {
        checkSequence("<changes>"
                        + "  <create type='linkedToObjectWithCompositeKey' id='0' link1='1' link2='2'/>"
                        + "  <delete type='linkedToObjectWithCompositeKey' id='1' _link1='3' _link2='4'/>"
                        + "  <update type='linkedToObjectWithCompositeKey' id='2' link1='2' link2='3'/>"
                        + "  <delete type='objectWithCompositeKey' id1='3' id2='4'/>"
                        + "  <update type='objectWithCompositeKey' id1='2' id2='3' name='newName'/>"
                        + "  <create type='objectWithCompositeKey' id1='1' id2='2'/>"
                        + "</changes>",
                "<changes>"
                        + "  <create type='objectWithCompositeKey' id1='1' id2='2'/>"
                        + "  <create type='linkedToObjectWithCompositeKey' id='0' link1='1' link2='2'/>"
                        + "  <update type='objectWithCompositeKey' id1='2' id2='3'"
                        + "          name='newName' _name='(null)'/>"
                        + "  <update type='linkedToObjectWithCompositeKey' id='2'"
                        + "          link1='2' _link1='(null)' "
                        + "          link2='3' _link2='(null)'/>"
                        + "  <delete type='linkedToObjectWithCompositeKey' id='1' _link1='3' _link2='4'/>"
                        + "  <delete type='objectWithCompositeKey' id1='3' id2='4'/>"
                        + "</changes>");
    }

    @Test
    public void testLinkCycle() throws Exception {
        checkSequence("<changes>"
                        + "  <create type='linkCycle1' id1='0' id2='2' link1='1' link2='2'/>"
                        + "  <create type='linkCycle2' id1='1' id2='2' link1='1' link2='2'/>"
                        + "</changes>",
                "<changes>"
                        + "  <create type='linkCycle2' id1='1' id2='2'/>"
                        + "  <create type='linkCycle1' id1='0' id2='2'" +
                        "            link1='1' link2='2'/>"
                        + "  <update type='linkCycle2' id1='1' id2='2' " +
                        "            link1='1' _link1='(null)' " +
                        "            link2='2' _link2='(null)'/>"
                        + "</changes>");
    }

    public static class LargeLinkCycle1 {
        public static GlobType TYPE;

        @KeyField_
        public static IntegerField ID;

        //    @Target(LargeLinkCycle2.class)
        public static IntegerField LINK_ID;

        public static Link LINK;

        static {
            GlobTypeLoader loader = GlobTypeLoaderFactory.create(LargeLinkCycle1.class, true);
            loader.register(MutableGlobLinkModel.LinkRegister.class, mutableGlobLinkModel ->
                    LINK = mutableGlobLinkModel.getDirectLinkBuilder(LINK)
                            .add(LargeLinkCycle1.LINK_ID, LargeLinkCycle2.ID)
                            .publish());
            loader.load();
        }
    }

    public static class LargeLinkCycle2 {
        public static GlobType TYPE;

        @KeyField_
        public static IntegerField ID;

        //    @Target(LargeLinkCycle3.class)
        public static IntegerField LINK_ID;

        public static Link LINK;

        static {
            GlobTypeLoader loader = GlobTypeLoaderFactory.create(LargeLinkCycle2.class, true);
            loader.register(MutableGlobLinkModel.LinkRegister.class, mutableGlobLinkModel ->
                    LINK = mutableGlobLinkModel.getDirectLinkBuilder(LINK)
                            .add(LargeLinkCycle2.LINK_ID, LargeLinkCycle3.ID)
                            .publish());
            loader.load();
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
            GlobTypeLoader loader = GlobTypeLoaderFactory.create(LargeLinkCycle3.class, true);
            loader.register(MutableGlobLinkModel.LinkRegister.class, mutableGlobLinkModel ->
                    LINK = mutableGlobLinkModel.getDirectLinkBuilder(LINK)
                            .add(LINK_ID, LargeLinkCycle1.ID).publish());
            loader.load();
        }
    }

    @Test
    public void testLargeLinkCycle() throws Exception {
        checkSequence("<changes>"
                        + "  <create type='largeLinkCycle1' id='1' linkId='2'/>"
                        + "  <create type='largeLinkCycle2' id='2' linkId='3'/>"
                        + "  <create type='largeLinkCycle3' id='3' linkId='1'/>"
                        + "</changes>",
                "<changes>"
                        + "  <create type='largeLinkCycle3' id='3'/>"
                        + "  <create type='largeLinkCycle2' id='2' linkId='3'/>"
                        + "  <create type='largeLinkCycle1' id='1' linkId='2'/>"
                        + "  <update type='largeLinkCycle3' id='3' linkId='1' _linkId='(null)'/>"
                        + "</changes>");
    }

    private void checkSequence(String input, String expected) throws Exception {
        ChangeSet changeSet = XmlChangeSetParser.parse(Model.MODEL, new StringReader(input));

        StringWriter writer = new StringWriter();
        XmlChangeSetVisitor visitor = new XmlChangeSetVisitor(writer, 2);
        ChangeSetSequencer.process(changeSet, Model.MODEL, visitor);
        visitor.complete();

        XmlTestUtils.assertEquivalent(expected, writer.toString());
    }

    private static class Model {
        static final GlobModel MODEL = GlobModelBuilder.create(ObjectWithCompositeKey.TYPE,
                LinkedToObjectWithCompositeKey.TYPE, ObjectWithSelfReference.TYPE, LinkCycle1.TYPE, LinkCycle2.TYPE, LargeLinkCycle1.TYPE,
                LargeLinkCycle2.TYPE, LargeLinkCycle3.TYPE).get();
    }
}
