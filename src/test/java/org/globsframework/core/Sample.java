package org.globsframework.core;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilderFactory;
import org.globsframework.core.metamodel.GlobTypeLoaderFactory;
import org.globsframework.core.metamodel.annotations.*;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.metamodel.fields.IntegerField;
import org.globsframework.core.metamodel.fields.StringField;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.MutableGlob;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class Sample {

    @Test
    public void readmeDynamic() {
        GlobType type = GlobTypeBuilderFactory.create("aType")
                .addIntegerKey("id")
                .addStringField("string", NamingField.UNIQUE_GLOB)
                .addIntegerField("anInt", FieldName.create("int"))
                .addLongField("long")
                .addDoubleField("double")
                .addBytesField("blob")
                .addBooleanField("boolean")
                .get();

        MutableGlob data = type.instantiate();

        StringField stringField = type.getField("string").asStringField();
        data.set(stringField, "Hello");

        assertEquals("Hello", data.get(stringField));

        Field namingField = data.getType().findFieldWithAnnotation(NamingField.KEY);
        assertEquals("Hello", data.getValue(namingField));
        assertEquals("int", FieldName.getName(type.getField("anInt")));
    }

    public static class AType {
        public static GlobType TYPE;

        @KeyField_
        public static IntegerField ID;

        @NamingField_()
        public static StringField string;

        @FieldName_("int")
        public static IntegerField anInt;

        static {
            GlobTypeLoaderFactory.create(AType.class).load();
        }
    }

    @Test
    public void readStatic() {
        final Glob data = AType.TYPE.instantiate()
                .set(AType.string, "Hello")
                .set(AType.anInt, 2);

        assertEquals("Hello", data.get(AType.string));
        assertEquals(2, data.get(AType.anInt).intValue());

    }
}
