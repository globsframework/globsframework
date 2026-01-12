package org.globsframework.core.metamodel.utils;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.annotations.FieldName;
import org.globsframework.core.metamodel.annotations.NamingField;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.metamodel.fields.StringField;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.format.GlobPrinter;
import org.globsframework.core.utils.exceptions.InvalidParameter;
import org.globsframework.core.utils.exceptions.ItemAmbiguity;
import org.globsframework.core.utils.exceptions.ItemNotFound;
import org.globsframework.core.utils.exceptions.TooManyItems;

public class GlobTypeUtils {

    public static GlobType getType(Class targetClass) throws InvalidParameter {
        for (java.lang.reflect.Field field : targetClass.getFields()) {
            if (field.getType().equals(GlobType.class)) {
                try {
                    return (GlobType) field.get(null);
                } catch (Exception e) {
                    throw new InvalidParameter("Cannot access GlobType field in class " + targetClass.getName(), e);
                }
            }
        }
        throw new InvalidParameter("Class " + targetClass.getName() + " does not define a GlobType");
    }

    public static StringField findNamingField(GlobType type) {
        Field field = type.findFieldWithAnnotation(NamingField.KEY);
        if (field != null) {
            return stringField(field, type);
        }
        return null;
    }

    public static StringField getNamingField(GlobType type) throws ItemNotFound, ItemAmbiguity, InvalidParameter {
        Field field = type.getFieldWithAnnotation(NamingField.KEY);
        return stringField(field, type);
    }

    private static StringField stringField(Field field, GlobType type) {
        if (!(field instanceof StringField)) {
            throw new InvalidParameter("Naming field of type '" + type + "' should be a StringField");
        }
        return (StringField) field;
    }

    public static Field findNamedField(GlobType type, String name) {
        Field fieldWithAnnotation = findFieldWithAnnotation(type, FieldName.create(name));
        if (fieldWithAnnotation == null) {
            fieldWithAnnotation = type.findField(name);
        }
        return fieldWithAnnotation;
    }

    public static Field findFieldWithAnnotation(GlobType type, Glob glob) {
        Field foundField = null;
        for (Field field : type.getFields()) {
            if (field.hasAnnotation(glob.getKey())) {
                Glob annotation = field.getAnnotation(glob.getKey());
                if (isSame(annotation, glob)) {
                    if (foundField != null) {
                        throw new TooManyItems("Found multiple field with " + GlobPrinter.toString(glob) + " => " +
                                field + " and " + foundField);
                    }
                    foundField = field;
                }
            }
        }
        return foundField;
    }

    private static boolean isSame(Glob annotation, Glob glob) {
        for (Field field : annotation.getType().getFields()) {
            if (!field.valueEqual(annotation.getValue(field), glob.getValue(field))) {
                return false;
            }
        }
        return true;
    }

}
