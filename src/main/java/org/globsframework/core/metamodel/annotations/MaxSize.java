package org.globsframework.core.metamodel.annotations;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.metamodel.impl.DefaultGlobTypeBuilder;
import org.globsframework.core.model.*;
import org.globsframework.core.utils.Strings;

import java.nio.charset.Charset;

public class MaxSize {
    static public final GlobType TYPE;

    static public final IntegerField VALUE;

    static public final BooleanField ALLOW_TRUNCATE;

    static public final StringField CHARSET;

    @InitUniqueKey
    static public final Key KEY;

    static {
        GlobTypeBuilder typeBuilder = new DefaultGlobTypeBuilder("MaxSize");
        VALUE = typeBuilder.declareIntegerField("value");
        ALLOW_TRUNCATE = typeBuilder.declareBooleanField("allow_truncate");
        CHARSET = typeBuilder.declareStringField("charSet");
        typeBuilder.register(GlobCreateFromAnnotation.class, annotation -> create((MaxSize_) annotation));
        TYPE = typeBuilder.build();
        KEY = KeyBuilder.newEmptyKey(TYPE);
    }

    static public String cut(Field field, FieldValuesAccessor value) {
        return value.getOpt(field.asStringField())
                .map(s -> cut(field, s))
                .orElse(null);
    }

    static public String cut(Field field, String value) {
        Glob annotation = field.findAnnotation(KEY);
        if (annotation != null && Strings.isNotEmpty(value) && annotation.get(VALUE) > 0) {
            Charset charsetName = Charset.forName(annotation.get(CHARSET));
            if (value.getBytes(charsetName).length > annotation.get(VALUE)) {
                if (annotation.get(ALLOW_TRUNCATE, false)) {
                    String substring = value.substring(0, Math.min(value.length(), annotation.get(VALUE)));
                    while (substring.getBytes(charsetName).length > annotation.get(VALUE)) {
                        substring = substring.substring(0, substring.length() - 1);
                    }
                    return substring;
                } else {
                    throw new StringToLongException(field.getFullName() + " => " + value);
                }
            } else {
                return value;
            }
        }
        return value;
    }

    static public boolean checkSize(Field field, String value) {
        Glob annotation = field.findAnnotation(KEY);
        if (annotation != null && Strings.isNotEmpty(value)) {
            if (value.length() > annotation.get(VALUE)) {
                return false;
            }
        }
        return true;
    }

    public static Glob create(MaxSize_ size) {
        return create(size.value(), size.allow_truncate(), size.charSet());
    }

    public static MutableGlob create(int maxSize, boolean truncate, String charset) {
        return TYPE.instantiate().set(VALUE, maxSize)
                .set(ALLOW_TRUNCATE, truncate)
                .set(CHARSET, charset);
    }

    public static MutableGlob create(int maxSize, boolean truncate) {
        return TYPE.instantiate().set(VALUE, maxSize)
                .set(ALLOW_TRUNCATE, truncate)
                .set(CHARSET, "UTF-8");
    }

    public static MutableGlob create(int maxSize) {
        return TYPE.instantiate().set(VALUE, maxSize)
                .set(ALLOW_TRUNCATE, false)
                .set(CHARSET, "UTF-8");
    }

    public static MutableGlob deepInPlaceTruncate(MutableGlob glob) {
        if (glob != null) {
            GlobType type = glob.getType();
            for (Field field : type.getFields()) {
                if (field instanceof StringField) {
                    StringField stringField = field.asStringField();
                    glob.set(stringField, cut(field, glob.get(stringField)));
                } else if (field instanceof StringArrayField) {
                    String[] strings = glob.get((StringArrayField) field);
                    if (strings != null) {
                        for (int i = 0; i < strings.length; i++) {
                            strings[i] = cut(field, strings[i]);
                        }
                    }
                } else if (field instanceof GlobField) {
                    deepInPlaceTruncate((MutableGlob) glob.get((GlobField) field));
                } else if (field instanceof GlobArrayField) {
                    Glob[] globs = glob.get(((GlobArrayField) field));
                    for (Glob value : globs) {
                        deepInPlaceTruncate((MutableGlob) value);
                    }
                } else if (field instanceof GlobUnionField) {
                    deepInPlaceTruncate((MutableGlob) glob.get((GlobUnionField) field));
                } else if (field instanceof GlobArrayUnionField) {
                    Glob[] globs = glob.get(((GlobArrayUnionField) field));
                    for (Glob value : globs) {
                        deepInPlaceTruncate((MutableGlob) value);
                    }
                }
            }
        }
        return glob;
    }

    static class StringToLongException extends RuntimeException {
        public StringToLongException(String value) {
            super(value);
        }
    }
}
