package org.globsframework.core.xml.tests;

import org.globsframework.core.metamodel.DummyObject;
import org.globsframework.core.metamodel.DummyObject2;
import org.globsframework.core.metamodel.DummyObjectWithCompositeKey;
import org.globsframework.core.metamodel.DummyObjectWithLinks;
import org.globsframework.core.model.GlobRepository;
import org.globsframework.core.model.GlobRepositoryBuilder;
import org.globsframework.core.model.GlobTestUtils;
import org.globsframework.core.model.utils.GlobBuilder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class XmlGlobWriterTest {
    private GlobRepository globRepository;

    @Test
    public void testStandardCase() throws Exception {
        globRepository =
                GlobRepositoryBuilder
                        .init()
                        .add(GlobBuilder.init(DummyObject.TYPE)
                                .set(DummyObject.ID, 1)
                                .set(DummyObject.NAME, "name1")
                                .get())
                        .add(GlobBuilder.init(DummyObject2.TYPE)
                                .set(DummyObject2.ID, 2)
                                .set(DummyObject2.LABEL, "label2")
                                .get())
                        .get();

        GlobTestUtils.assertEquals(globRepository,
                "<dummyObject id='1' name='name1'/>'" +
                        "<dummyObject2 id='2' label='label2'/>");
    }

    @Test
    public void testWithDate() throws Exception {
        globRepository =
                GlobRepositoryBuilder
                        .init()
                        .add(GlobBuilder.init(DummyObject.TYPE)
                                .set(DummyObject.ID, 1)
                                .set(DummyObject.DATE, 20061205)
                                .get())
                        .get();

        GlobTestUtils.assertEquals(globRepository,
                "<dummyObject id='1' date='20061205'/>");
    }


    @Disabled // TODO containment not defined correctly
    @Test
    public void testLinkField() throws Exception {
        globRepository =
                GlobRepositoryBuilder
                        .init()
                        .add(GlobBuilder.init(DummyObject.TYPE)
                                .set(DummyObject.ID, 1)
                                .set(DummyObject.LINK_ID, 2)
                                .get())
                        .add(GlobBuilder.init(DummyObject.TYPE)
                                .set(DummyObject.ID, 2)
                                .set(DummyObject.NAME, "name2")
                                .get())
                        .get();

        GlobTestUtils.assertEquals(globRepository,
                "<dummyObject id='1' linkId='2' linkName='name2'/>'" +
                        "<dummyObject id='2' name='name2'/>");
    }

    @Test
    public void testLinkWithCompositeTarget() throws Exception {
        globRepository =
                GlobRepositoryBuilder
                        .init()
                        .add(GlobBuilder.init(DummyObjectWithCompositeKey.TYPE)
                                .set(DummyObjectWithCompositeKey.ID1, 1)
                                .set(DummyObjectWithCompositeKey.ID2, 2)
                                .set(DummyObjectWithCompositeKey.NAME, "foo")
                                .get())
                        .add(GlobBuilder.init(DummyObjectWithLinks.TYPE)
                                .set(DummyObjectWithLinks.ID, 1)
                                .set(DummyObjectWithLinks.TARGET_ID_1, 1)
                                .set(DummyObjectWithLinks.TARGET_ID_2, 2)
                                .get())
                        .get();

        GlobTestUtils.assertEquals(globRepository,
                "<dummyObjectWithCompositeKey id1='1' id2='2' name='foo'/>'" +
                        "<dummyObjectWithLinks id='1' targetId1='1' targetId2='2'/>");
    }

    @Test
    public void testLinkWithNoNamingField() throws Exception {
        globRepository =
                GlobRepositoryBuilder
                        .init()
                        .add(GlobBuilder.init(DummyObjectWithLinks.TYPE)
                                .set(DummyObjectWithLinks.ID, 1)
                                .get())
                        .add(GlobBuilder.init(DummyObjectWithLinks.TYPE)
                                .set(DummyObjectWithLinks.ID, 2)
                                .set(DummyObjectWithLinks.SIBLING_ID, 1)
                                .get())
                        .get();

        GlobTestUtils.assertEquals(globRepository,
                "<dummyObjectWithLinks id='1'/>'" +
                        "<dummyObjectWithLinks id='2' siblingId='1'/>");
    }

    @Test
    @Disabled
    public void testContainmentLink() throws Exception {
        globRepository =
                GlobRepositoryBuilder
                        .init()
                        .add(GlobBuilder.init(DummyObjectWithLinks.TYPE)
                                .set(DummyObjectWithLinks.ID, 1)
                                .set(DummyObjectWithLinks.PARENT_ID, 2)
                                .get())
                        .add(GlobBuilder.init(DummyObject.TYPE)
                                .set(DummyObject.ID, 2)
                                .get())
                        .get();

        GlobTestUtils.assertEquals(globRepository, DummyObject.TYPE,
                "<dummyObject id='2'>'" +
                        "    <dummyObjectWithLinks id='1'/>" +
                        "</dummyObject>");

    }

    @Test
    public void testLinkWithNoTarget() throws Exception {
        globRepository =
                GlobRepositoryBuilder
                        .init()
                        .add(GlobBuilder.init(DummyObject.TYPE)
                                .set(DummyObject.ID, 1)
                                .set(DummyObject.LINK_ID, 2)
                                .get())
                        .add(GlobBuilder.init(DummyObject.TYPE)
                                .set(DummyObject.ID, 2)
                                .set(DummyObject.NAME, "name2")
                                .get())
                        .get();

        GlobTestUtils.assertEquals(globRepository,
                "<dummyObject id='1' linkId='2'/>'" +
                        "<dummyObject id='2' name='name2'/>");
    }

}
