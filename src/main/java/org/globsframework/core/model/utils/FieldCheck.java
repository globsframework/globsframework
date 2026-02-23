package org.globsframework.core.model.utils;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.model.Glob;
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
        } else {
            assert field.getGlobType() == type;
        }
    }

    public static void checkIsKeyOf(Field field, GlobType type, Object value) {
        if (CheckGlob.shouldCheck) {
            check(field, type);
            field.checkValue(value);
            if (!field.isKeyField()) {
                throwKeyError(field);
            }
        } else {
            assert field.getGlobType() == type;
            assert field.isKeyField();
            assert field.checkValue(value);
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
        } else {
            assert field.getGlobType() == type;
        }
    }

    static public void check(GlobType type, Glob glob) {
        if (CheckGlob.shouldCheck) {
            if (glob.getType() != type) {
                throwError(type, glob);
            }
        } else {
            assert glob.getType() == type;
        }
    }

    static public void check(Field field, GlobType type, Object value) {
        if (CheckGlob.shouldCheck) {
            if (field.getGlobType() != type) {
                throwFieldError(field, type);
            }
            field.checkValue(value);
        } else {
            assert field.getGlobType() == type;
            assert field.checkValue(value);
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

    private static void throwError(GlobType type, Glob data) {
        throw new InvalidParameter("Type '" + type.describe() + "' does not match data type '" + data.toString() + "'");
    }

}
