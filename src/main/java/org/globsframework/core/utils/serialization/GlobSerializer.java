package org.globsframework.core.utils.serialization;

import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.model.Glob;

import java.time.LocalDate;
import java.time.ZonedDateTime;

public class GlobSerializer {
    private final SerializedOutput output;

    public GlobSerializer(SerializedOutput output) {
        this.output = output;
    }

    public void writeGlob(Glob glob) {
        output.writeUtf8String(glob.getType().getName());
        for (Field field : glob.getType().getFields()) {
            field.safeAccept(OutputStreamFieldVisitor.INSTANCE, glob, output);
        }
    }

    public void writeKnowGlob(Glob glob) {
        for (Field field : glob.getType().getFields()) {
            field.safeAccept(OutputStreamFieldVisitor.INSTANCE, glob, output);
        }
    }

    private static class OutputStreamFieldVisitor implements FieldVisitorWithTwoContext<Glob, SerializedOutput> {
        public final static OutputStreamFieldVisitor INSTANCE = new OutputStreamFieldVisitor();

        public void visitInteger(IntegerField field, Glob glob, SerializedOutput output) {
            output.writeInteger(glob.get(field));
        }

        public void visitIntegerArray(IntegerArrayField field, Glob glob, SerializedOutput output) {
            output.write(glob.get(field));
        }

        public void visitDouble(DoubleField field, Glob glob, SerializedOutput output) {
            output.writeDouble(glob.get(field));
        }

        public void visitDoubleArray(DoubleArrayField field, Glob glob, SerializedOutput output) {
            output.write(glob.get(field));
        }

        public void visitBigDecimal(BigDecimalField field, Glob glob, SerializedOutput output) {
            output.write(glob.get(field));
        }

        public void visitBigDecimalArray(BigDecimalArrayField field, Glob glob, SerializedOutput output) {
            output.write(glob.get(field));
        }

        public void visitString(StringField field, Glob glob, SerializedOutput output) {
            output.writeUtf8String(glob.get(field));
        }

        public void visitStringArray(StringArrayField field, Glob glob, SerializedOutput output) {
            output.write(glob.get(field));
        }

        public void visitBoolean(BooleanField field, Glob glob, SerializedOutput output) {
            output.writeBoolean(glob.get(field));
        }

        public void visitBooleanArray(BooleanArrayField field, Glob glob, SerializedOutput output) {
            output.write(glob.get(field));
        }

        public void visitBlob(BlobField field, Glob glob, SerializedOutput output) {
            output.writeBytes(glob.get(field));
        }


        public void writeKnowGlob(Glob glob, SerializedOutput output) {
            for (Field field : glob.getType().getFields()) {
                field.safeAccept(this, glob, output);
            }
        }

        public void visitGlob(GlobField field, Glob data, SerializedOutput output) throws Exception {
            Glob glob = data.get(field);
            output.write(glob != null);
            if (glob != null) {
                writeKnowGlob(glob, output);
            }
        }

        public void visitGlobArray(GlobArrayField field, Glob data, SerializedOutput output) throws Exception {
            Glob[] globs = data.get(field);
            if (globs == null) {
                output.write(-1);
            } else {
                output.write(globs.length);
                for (Glob glob : globs) {
                    output.write(glob != null);
                    if (glob != null) {
                        writeKnowGlob(glob, output);
                    }
                }
            }

        }
        public void writeGlob(Glob glob, SerializedOutput output) {
            output.writeUtf8String(glob.getType().getName());
            for (Field field : glob.getType().getFields()) {
                field.safeAccept(this, glob, output);
            }
        }

        public void visitUnionGlob(GlobUnionField field, Glob data, SerializedOutput output) throws Exception {
            Glob glob = data.get(field);
            output.write(glob != null);
            if (glob != null) {
                writeGlob(glob, output);
            }
        }

        public void visitUnionGlobArray(GlobArrayUnionField field, Glob data, SerializedOutput output) throws Exception {
            Glob[] globs = data.get(field);
            if (globs == null) {
                output.write(-1);
            } else {
                output.write(globs.length);
                for (Glob glob : globs) {
                    output.write(glob != null);
                    if (glob != null) {
                        writeGlob(glob, output);
                    }
                }
            }
        }

        public void visitLong(LongField field, Glob glob, SerializedOutput output) {
            output.writeLong(glob.get(field));
        }

        public void visitLongArray(LongArrayField field, Glob glob, SerializedOutput output) {
            output.write(glob.get(field));
        }

        public void visitDate(DateField field, Glob glob, SerializedOutput output) {
            writeDate(glob.get(field), output);
        }

        public void visitDateTime(DateTimeField field, Glob glob, SerializedOutput output) {
            writeDateTime(glob.get(field), output);
        }
    }

    private static void writeDate(LocalDate date, SerializedOutput output) {
        if (date == null) {
            output.write(Integer.MIN_VALUE);
        } else {
            output.write(date.getYear());
            output.write(date.getMonthValue());
            output.write(date.getDayOfMonth());
        }
    }

    private static void writeDateTime(ZonedDateTime date, SerializedOutput output) {
        if (date == null) {
            output.write(Integer.MIN_VALUE);
        } else {
            output.write(date.getYear());
            output.write(date.getMonthValue());
            output.write(date.getDayOfMonth());
            output.write(date.getHour());
            output.write(date.getMinute());
            output.write(date.getSecond());
            output.write(date.getNano());
            output.writeUtf8String(date.getZone().getId());
        }
    }

}
