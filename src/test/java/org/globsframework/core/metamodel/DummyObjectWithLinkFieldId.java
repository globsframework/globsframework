package org.globsframework.core.metamodel;

import org.globsframework.core.metamodel.annotations.*;
import org.globsframework.core.metamodel.fields.IntegerField;
import org.globsframework.core.metamodel.links.Link;

public class DummyObjectWithLinkFieldId {
    public static GlobType TYPE;

    @KeyField_
    public static IntegerField LINK_ID;

    @Target(DummyObject.class)
    public static Link LINK;

    static {
        GlobTypeBuilder builder = GlobTypeBuilderFactory.create("dummyObjectWithLinkFieldId");
        LINK_ID = builder.declareIntegerField("linkId", KeyField.ZERO);

        builder.register(MutableGlobLinkModel.LinkRegister.class,
                        (linkModel) ->
                                LINK = LINK != null ? LINK :
                                        linkModel.getDirectLinkBuilder("ANY", "linkName")
                                        .add(LINK_ID, DummyObject.ID).publish());
        TYPE = builder.build();
    }
}
