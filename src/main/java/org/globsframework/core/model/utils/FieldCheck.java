package org.globsframework.core.model.utils;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.metamodel.fields.impl.AbstractField;
import org.globsframework.core.model.Glob;
import org.globsframework.core.utils.exceptions.InvalidParameter;

import static org.globsframework.core.metamodel.fields.impl.AbstractField.impl;

public class FieldCheck {

    public interface CheckGlob {
        boolean shouldCheck = !Boolean.getBoolean("globsframework.field.no.check");
    }

    public static void checkIsKeyOf(Field field, GlobType type) {
        if (CheckGlob.shouldCheck) {
            check(field, type);
            if (!impl(field).isKeyField()) {
                throwKeyError(field);
            }
        } else {
            assert impl(field).getGlobType() == type;
        }
    }

    public static void checkIsKeyOf(Field field, GlobType type, Object value) {
        if (CheckGlob.shouldCheck) {
            check(field, type);
            impl(field).fastCheckValue(value);
            if (!impl(field).isKeyField()) {
                throwKeyError(field);
            }
        } else {
            assert impl(field).getGlobType() == type;
            assert impl(field).isKeyField();
            assert impl(field).fastCheckValue(value);
        }
    }

    private static void throwKeyError(Field field) {
        throw new RuntimeException(field + " is not the a key field for " + field.getGlobType().describe());
    }

    static public void check(Field field, GlobType type) {
        if (CheckGlob.shouldCheck) {
            if (impl(field).getGlobType() != type) {
                throwFieldError(impl(field), type);
            }
        } else {
            assert impl(field).getGlobType() == type;
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
            if (impl(field).getGlobType() != type) {
                throwFieldError(impl(field), type);
            }
            impl(field).fastCheckValue(value);
        } else {
            assert impl(field).getGlobType() == type;
            assert impl(field).fastCheckValue(value);
        }
    }

    static public void checkValue(Field field, Object value) {
        if (CheckGlob.shouldCheck) {
            impl(field).fastCheckValue(value);
        }
    }

    public static void throwFieldError(AbstractField field, GlobType type) {
        throw new InvalidParameter("Field '" + field.getName() + "' is declared for type '" +
                                   field.getGlobType().describe() + "'\n but not for \n'" + type.describe() + "'");
    }

    private static void throwError(GlobType type, Glob data) {
        throw new InvalidParameter("Type '" + type.describe() + "' does not match data type '" + data.toString() + "'");
    }

}
