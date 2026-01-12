package org.globsframework.core.model.utils;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.metamodel.fields.impl.AbstractField;
import org.globsframework.core.utils.exceptions.InvalidParameter;

public class FieldCheck {

    public interface CheckGlob {
        boolean shouldCheck = !Boolean.getBoolean("globsframework.field.no.check");
    }

    public static void checkIsKeyOf(Field field, GlobType type) {
        if (CheckGlob.shouldCheck) {
            check(field, type);
            if (!field.isKeyField()) {
                throwKeyError(field);
            }
        }
    }

    public static void checkIsKeyOf(Field field, GlobType type, Object value) {
        if (CheckGlob.shouldCheck) {
            check(field, type);
            field.checkValue(value);
            if (!field.isKeyField()) {
                throwKeyError(field);
            }
        }
    }

    private static void throwKeyError(Field field) {
        throw new RuntimeException(field + " is not the a key field for " + field.getGlobType().describe());
    }

    static public void check(Field field, GlobType type) {
        if (CheckGlob.shouldCheck) {
            if (field.getGlobType() != type) {
                throwFieldError(field, type);
            }
        }
    }

    static public void check(Field field, GlobType type, Object value) {
        if (CheckGlob.shouldCheck) {
            if (field.getGlobType() != type) {
                throwFieldError(field, type);
            }
            checkValue(field, value);
        }
    }

    static public void checkValue(Field field, Object value) {
        if (CheckGlob.shouldCheck) {
            field.checkValue(value);
        }
    }

    private static void throwFieldError(Field field, GlobType type) {
        throw new InvalidParameter("Field '" + field.getName() + "' is declared for type '" +
                field.getGlobType().describe() + "'\n but not for \n'" + type.describe() + "'");
    }

}
