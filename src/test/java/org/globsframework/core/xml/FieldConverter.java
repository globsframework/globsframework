package org.globsframework.core.xml;

import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.utils.exceptions.InvalidParameter;

import java.text.*;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class FieldConverter {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.0#",
            new DecimalFormatSymbols(Locale.US));
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd");
    private static final DateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    private DateFormat dateFormat = DATE_FORMAT;
    private XmlStringifierVisitor xmlStringifierVisitor;
    private FieldStringifierVisitor fieldStringifierVisitor;
    private boolean ignoreError = false;

    public FieldConverter() {
        xmlStringifierVisitor = new XmlStringifierVisitor();
        fieldStringifierVisitor = new FieldStringifierVisitor();
    }

    public FieldConverter(boolean ignoreError) {
        this.ignoreError = ignoreError;
        xmlStringifierVisitor = new XmlStringifierVisitor();
        fieldStringifierVisitor = new FieldStringifierVisitor();
    }

    public String toString(Field field, Object value) {
        if (value == null) {
            return "(null)";
        }
        fieldStringifierVisitor.setValue(value);
        field.safeAccept(fieldStringifierVisitor);
        return fieldStringifierVisitor.getStringValue();
    }

    public Object toObject(Field field, String stringValue) {
        try {
            xmlStringifierVisitor.setStringValue(stringValue);
            field.safeAccept(xmlStringifierVisitor);
            return xmlStringifierVisitor.getValue();
        } catch (Exception e) {
            if (ignoreError) {
                return field.getDefaultValue();
            }
            throw new InvalidParameter("'" + stringValue + "' is not a proper value for field '" + field.getName() +
                    "' in type '" + field.getGlobType().getName() + "'", e);
        }
    }

    public void setDateFormat(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String toString(Date date) {
        return dateFormat.format(date);
    }

    public Date toDate(String value) {
        try {
            return dateFormat.parse(value);
        } catch (ParseException e) {
            if (ignoreError) {
                return null;
            }
            throw new InvalidParameter("'" + value + "' is not a properly formatted date", e);
        }
    }

    public Date toTimestamp(String value) {
        try {
            return TIMESTAMP_FORMAT.parse(value);
        } catch (ParseException e) {
            if (ignoreError) {
                return null;
            }
            throw new InvalidParameter("'" + value + "' is not a properly formatted timestamp", e);
        }
    }

    private class XmlStringifierVisitor extends FieldVisitor.AbstractWithErrorVisitor {
        private String stringValue;
        private Object value;

        public XmlStringifierVisitor(String stringValue) {
            this.stringValue = stringValue;
        }

        private XmlStringifierVisitor() {
        }

        public void setStringValue(String stringValue) {
            this.stringValue = stringValue;
            value = null;
        }

        public void visitBoolean(BooleanField field) throws Exception {
            value = stringValue.equalsIgnoreCase("true");
        }

        public void visitBytes(BytesField field) throws Exception {
            value = stringValue.getBytes();
        }

        public void visitString(StringField field) throws Exception {
            value = stringValue;
        }

        public void visitStringArray(StringArrayField field) throws Exception {
            if (stringValue != null) {
                if (stringValue.isEmpty()) {
                    value = new String[0];
                } else {
                    value = stringValue.split(",");
                }
            } else {
                value = null;
            }
        }

        public void visitLongArray(LongArrayField field) throws Exception {
            if (stringValue != null) {
                if (stringValue.isEmpty()) {
                    value = new long[0];
                } else {
                    value = Arrays.stream(stringValue.split(","))
                            .mapToLong(Long::parseLong)
                            .toArray();
                }
            } else {
                value = null;
            }
        }

        public void visitDouble(DoubleField field) throws Exception {
            value = Double.parseDouble(stringValue);
        }

        public void visitInteger(IntegerField field) throws Exception {
            value = Integer.parseInt(stringValue);
        }

        public void visitLong(LongField field) throws Exception {
            value = Long.parseLong(stringValue);
        }

        public Object getValue() {
            return value;
        }
    }

    private class FieldStringifierVisitor extends FieldVisitor.AbstractWithErrorVisitor {
        private Object value;
        private String stringValue;

        private FieldStringifierVisitor() {
        }

        public void setValue(Object value) {
            this.value = value;
            stringValue = null;
        }

        public String getStringValue() {
            return stringValue;
        }

        public void visitBoolean(BooleanField field) throws Exception {
            if (((Boolean) value)) {
                stringValue = "true";
            } else {
                stringValue = "false";
            }
        }

        public void visitBytes(BytesField field) throws Exception {
            stringValue = new String((byte[]) value);
        }

        public void visitString(StringField field) throws Exception {
            stringValue = (String) value;
        }

        public void visitDouble(DoubleField field) throws Exception {
            if (((Double) value) == 0.0) {
                stringValue = "0.0";
            }
            stringValue = DECIMAL_FORMAT.format(value);
        }

        public void visitInteger(IntegerField field) throws Exception {
            stringValue = Integer.toString(((Integer) value));
        }

        public void visitLong(LongField field) throws Exception {
            stringValue = Long.toString(((Long) value));
        }

    }
}
