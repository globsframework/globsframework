package org.globsframework.core.metamodel;

import org.globsframework.core.metamodel.annotations.ContainmentLink;
import org.globsframework.core.metamodel.annotations.ContainmentLink_;
import org.globsframework.core.metamodel.annotations.KeyField;
import org.globsframework.core.metamodel.annotations.KeyField_;
import org.globsframework.core.metamodel.fields.IntegerField;
import org.globsframework.core.metamodel.links.Link;

public class DummyObjectWithLinks {

    public static GlobType TYPE;

    @KeyField_
    public static IntegerField ID;

    public static IntegerField TARGET_ID_1;
    public static IntegerField TARGET_ID_2;

    public static IntegerField PARENT_ID;
    public static IntegerField SIBLING_ID;

    public static Link COMPOSITE_LINK;

    @ContainmentLink_
    public static Link PARENT_LINK;

    public static Link SIBLING_LINK;

    static {
        GlobTypeBuilder builder = GlobTypeBuilderFactory.create("dummyObjectWithLinks");
        ID = builder.declareIntegerField("id", KeyField.ZERO);
        TARGET_ID_1 = builder.declareIntegerField("targetId1");
        TARGET_ID_2 = builder.declareIntegerField("targetId2");
        PARENT_ID = builder.declareIntegerField("parentId");
        SIBLING_ID = builder.declareIntegerField("siblingId");

        builder.register(MutableGlobLinkModel.LinkRegister.class,
                (linkModel) -> {
                    COMPOSITE_LINK = COMPOSITE_LINK != null ? COMPOSITE_LINK : linkModel.getLinkBuilder("DummyObjectWithLinks", "compositeLink")
                            .add(TARGET_ID_1, DummyObjectWithCompositeKey.ID1)
                            .add(TARGET_ID_2, DummyObjectWithCompositeKey.ID2)
                            .publish();

                    PARENT_LINK = PARENT_LINK != null ? PARENT_LINK : linkModel.getLinkBuilder("DummyObjectWithLinks", "PARENT_LINK", ContainmentLink.UNIQUE_GLOB)
                            .add(PARENT_ID, DummyObject.ID)
                            .publish();

                    SIBLING_LINK = SIBLING_LINK != null ? SIBLING_LINK : linkModel.getLinkBuilder("DummyObjectWithLinks", "SIBLING_LINK")
                            .add(SIBLING_ID, DummyObjectWithLinks.ID)
                            .publish();
                });
        TYPE = builder.build();
    }
}
