package org.globsframework.core.metamodel;

import org.globsframework.core.metamodel.annotations.*;
import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.metamodel.index.NotUniqueIndex;
import org.globsframework.core.metamodel.links.DirectLink;

public class DummyObject {

    public static final GlobType TYPE;

    @KeyField_
    @AutoIncrement_
    public static final IntegerField ID;

    @NamingField_
    public static final StringField NAME;

    public static final DoubleField VALUE;
    public static final IntegerField COUNT;
    public static final BooleanField PRESENT;
    public static final IntegerField DATE;
    public static final BytesField PASSWORD;
    @ContainmentLink_
    public static final IntegerField LINK_ID;

    public static DirectLink LINK;

    @Target(DummyObject2.class)
    public static final IntegerField LINK2_ID;

    public static DirectLink LINK2;

    //  public static UniqueIndex NAME_INDEX;
    public static NotUniqueIndex DATE_INDEX;
    static {
        final GlobTypeBuilder globTypeBuilder = GlobTypeBuilderFactory.create("dummyObject");
        ID = globTypeBuilder.declareIntegerField("id", KeyField.ZERO, AutoIncrement.INSTANCE);
        NAME = globTypeBuilder.declareStringField("name", NamingField.UNIQUE_GLOB);
        VALUE = globTypeBuilder.declareDoubleField("value");
        COUNT = globTypeBuilder.declareIntegerField("count");
        PRESENT = globTypeBuilder.declareBooleanField("present");
        DATE = globTypeBuilder.declareIntegerField("date");
        PASSWORD = globTypeBuilder.declareBytesField("password");
        LINK_ID = globTypeBuilder.declareIntegerField("linkId");
        LINK2_ID = globTypeBuilder.declareIntegerField("link2Id");
        DATE_INDEX = globTypeBuilder.addNotUniqueIndex("dateIndex", DATE);

        globTypeBuilder.register(MutableGlobLinkModel.LinkRegister.class,
                mutableGlobLinkModel -> {
                    LINK = LINK != null ? LINK : mutableGlobLinkModel.getDirectLinkBuilder(null, "link")
                            .add(LINK_ID, DummyObject.ID)
                            .publish();
                    LINK2 = LINK2 != null ? LINK2 : mutableGlobLinkModel.getDirectLinkBuilder(null, "link2")
                            .add(LINK2_ID, DummyObject2.ID)
                            .publish();
                });
        TYPE = globTypeBuilder.build();
    }
}
