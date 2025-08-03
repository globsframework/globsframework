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
        OutputStreamFieldVisitor visitor = new OutputStreamFieldVisitor(glob, output);
        for (Field field : glob.getType().getFields()) {
            field.safeAccept(visitor);
        }
    }

    public void writeKnowGlob(Glob glob) {
        OutputStreamFieldVisitor visitor = new OutputStreamFieldVisitor(glob, output);
        for (Field field : glob.getType().getFields()) {
            field.safeAccept(visitor);
        }
    }

    private class OutputStreamFieldVisitor implements FieldVisitor {
        private Glob glob;
        private final SerializedOutput output;

        public OutputStreamFieldVisitor(Glob glob, SerializedOutput output) {
            this.glob = glob;
            this.output = output;
        }

        public void visitInteger(IntegerField field) {
            output.writeInteger(glob.get(field));
        }

        public void visitIntegerArray(IntegerArrayField field) {
            output.write(glob.get(field));
        }

        public void visitDouble(DoubleField field) {
            output.writeDouble(glob.get(field));
        }

        public void visitDoubleArray(DoubleArrayField field) {
            output.write(glob.get(field));
        }

        public void visitBigDecimal(BigDecimalField field) {
            output.write(glob.get(field));
        }

        public void visitBigDecimalArray(BigDecimalArrayField field) {
            output.write(glob.get(field));
        }

        public void visitString(StringField field) {
            output.writeUtf8String(glob.get(field));
        }

        public void visitStringArray(StringArrayField field) {
            output.write(glob.get(field));
        }

        public void visitBoolean(BooleanField field) {
            output.writeBoolean(glob.get(field));
        }

        public void visitBooleanArray(BooleanArrayField field) {
            output.write(glob.get(field));
        }

        public void visitBlob(BlobField field) {
            output.writeBytes(glob.get(field));
        }

        public void visitGlob(GlobField field) throws Exception {
            Glob glob = this.glob.get(field);
            output.write(glob != null);
            if (glob != null) {
                writeKnowGlob(glob);
            }
        }

        public void visitGlobArray(GlobArrayField field) throws Exception {
            Glob[] globs = glob.get(field);
            if (globs == null) {
                output.write(-1);
            } else {
                output.write(globs.length);
                for (Glob glob : globs) {
                    output.write(glob != null);
                    if (glob != null) {
                        writeKnowGlob(glob);
                    }
                }
            }

        }

        public void visitUnionGlob(GlobUnionField field) throws Exception {
            Glob glob = this.glob.get(field);
            output.write(glob != null);
            if (glob != null) {
                writeGlob(glob);
            }
        }

        public void visitUnionGlobArray(GlobArrayUnionField field) throws Exception {
            Glob[] globs = glob.get(field);
            if (globs == null) {
                output.write(-1);
            } else {
                output.write(globs.length);
                for (Glob glob : globs) {
                    output.write(glob != null);
                    if (glob != null) {
                        writeGlob(glob);
                    }
                }
            }
        }

        public void visitLong(LongField field) {
            output.writeLong(glob.get(field));
        }

        public void visitLongArray(LongArrayField field) {
            output.write(glob.get(field));
        }

        public void visitDate(DateField field) {
            writeDate(glob.get(field), output);
        }

        public void visitDateTime(DateTimeField field) {
            writeDateTime(glob.get(field), output);
        }
    }


    private void writeDate(LocalDate date, SerializedOutput output) {
        if (date == null) {
            output.write(Integer.MIN_VALUE);
        } else {
            output.write(date.getYear());
            output.write(date.getMonthValue());
            output.write(date.getDayOfMonth());
        }
    }

    private void writeDateTime(ZonedDateTime date, SerializedOutput output) {
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
