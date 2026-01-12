package org.globsframework.core.metamodel;

import org.globsframework.core.metamodel.annotations.KeyField;
import org.globsframework.core.metamodel.annotations.KeyField_;
import org.globsframework.core.metamodel.annotations.Required;
import org.globsframework.core.metamodel.annotations.Required_;
import org.globsframework.core.metamodel.fields.IntegerField;
import org.globsframework.core.metamodel.fields.StringField;
import org.globsframework.core.metamodel.links.Link;

public class DummyObjectWithRequiredLink {
    public static GlobType TYPE;

    @KeyField_
    public static IntegerField ID;

    public static IntegerField TARGET_ID;

    public static StringField NAME;

    @Required_
    public static Link LINK;

    static {
        GlobTypeBuilder builder = GlobTypeBuilderFactory.create("dummyObjectWithRequiredLink");
        ID = builder.declareIntegerField("id", KeyField.ZERO);
        TARGET_ID = builder.declareIntegerField("target_id");
        NAME = builder.declareStringField("name");

        builder.register(MutableGlobLinkModel.LinkRegister.class, (linkModel) -> {
            LINK = LINK != null ? LINK : linkModel.getDirectLinkBuilder("DummyObjectWithRequiredLink", "LINK", Required.UNIQUE_GLOB)
                    .add(TARGET_ID, DummyObject.ID)
                    .publish();
        });
        TYPE = builder.build();
    }
}
