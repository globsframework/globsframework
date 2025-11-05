package org.globsframework.core.metamodel;

import org.globsframework.core.metamodel.annotations.*;
import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.metamodel.index.NotUniqueIndex;
import org.globsframework.core.metamodel.links.DirectLink;

public class DummyObject {

    public static GlobType TYPE;

    @KeyField_
    @AutoIncrement_
    public static IntegerField ID;

    @NamingField_
    public static StringField NAME;

    public static DoubleField VALUE;
    public static IntegerField COUNT;
    public static BooleanField PRESENT;
    public static IntegerField DATE;
    public static BytesField PASSWORD;

    @ContainmentLink_
    public static IntegerField LINK_ID;

    public static DirectLink LINK;

    @Target(DummyObject2.class)
    public static IntegerField LINK2_ID;

    public static DirectLink LINK2;

    //  public static UniqueIndex NAME_INDEX;
    public static NotUniqueIndex DATE_INDEX;

    static {
        GlobTypeLoader loader = GlobTypeLoaderFactory.create(DummyObject.class, true)
                .register(MutableGlobLinkModel.LinkRegister.class,
                        mutableGlobLinkModel -> {
                            LINK = mutableGlobLinkModel.getDirectLinkBuilder(LINK)
                                    .add(LINK_ID, DummyObject.ID)
                                    .publish();
                            LINK2 = mutableGlobLinkModel.getDirectLinkBuilder(LINK2)
                                    .add(LINK2_ID, DummyObject2.ID)
                                    .publish();
                        })
                .load();
//    loader.defineUniqueIndex(NAME_INDEX, NAME);
        loader.defineNonUniqueIndex(DATE_INDEX, DATE);
    }
}
