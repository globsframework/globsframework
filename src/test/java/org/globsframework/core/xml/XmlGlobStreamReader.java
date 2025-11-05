package org.globsframework.core.xml;

import org.globsframework.core.metamodel.GlobModel;
import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.MutableGlob;
import org.globsframework.core.streams.GlobStream;
import org.globsframework.core.streams.accessors.*;
import org.globsframework.saxstack.parser.DefaultXmlNode;
import org.globsframework.saxstack.parser.ExceptionHolder;
import org.globsframework.saxstack.parser.SaxStackParser;
import org.globsframework.saxstack.parser.XmlNode;
import org.globsframework.saxstack.utils.XmlUtils;
import org.xml.sax.Attributes;

import java.io.StringReader;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;

public class XmlGlobStreamReader {
    private GlobModel globModel;
    private String xml;
    private XmlGlobStream xmlMoStream;

    private XmlGlobStreamReader(String xml, final GlobModel globModel) {
        this.xml = xml;
        this.globModel = globModel;
    }

    private GlobStream parse() {
        xmlMoStream = new XmlGlobStream();
        SaxStackParser.parse(XmlUtils.getXmlReader(), new RootProxyNode(), new StringReader(xml));
        return xmlMoStream;
    }

    private void add(Glob mo) {
        xmlMoStream.add(mo);
    }

    public static GlobStream parse(String xml, final GlobModel globModel) {
        return new XmlGlobStreamReader("<root>" + xml + "</root>", globModel).parse();
    }

    private static class XmlGlobStream implements GlobStream {
        private Set<GlobType> globType = new HashSet<>();
        private List<Glob> globs = new ArrayList<>();
        private Iterator<Glob> iterator;
        private Glob current;
        private Map<Field, Accessor> accessors = new HashMap();

        public XmlGlobStream() {
        }

        public boolean next() {
            if (iterator == null) {
                iterator = globs.iterator();
            }
            if (iterator.hasNext()) {
                current = iterator.next();
                return true;
            }
            return false;
        }

        public Collection<Field> getFields() {
            return accessors.keySet();
        }

        public Accessor getAccessor(Field field) {
            return accessors.get(field);
        }

        public void close() {
        }

        public void add(Glob glob) {
            if (globs.isEmpty() || !globType.contains(glob.getType())) {
                globType.add(glob.getType());
                for (Field field : glob.getType().getFields()) {
                    AccessorDataTypeVisitor dataTypeVisitor = new AccessorDataTypeVisitor(this);
                    field.safeAccept(dataTypeVisitor);
                    accessors.put(field, dataTypeVisitor.getAccessor());
                }
            }
            globs.add(glob);
        }

        private static class AccessorDataTypeVisitor extends FieldVisitor.AbstractWithErrorVisitor {
            private XmlGlobStream stream;
            private Accessor accessor;

            public AccessorDataTypeVisitor(XmlGlobStream stream) {
                this.stream = stream;
            }

            public void visitBoolean(BooleanField field) throws Exception {
                accessor = new XmlBooleanAccessor(stream, field);
            }

            public void visitBytes(BytesField field) throws Exception {
                accessor = new XmlBytesAccessor(stream, field);
            }

            public void visitString(StringField field) throws Exception {
                accessor = new XmlStringAccessor(stream, field);
            }

            public void visitDouble(DoubleField field) throws Exception {
                accessor = new XmlDoubleAccessor(stream, field);
            }

            public void visitInteger(IntegerField field) throws Exception {
                accessor = new XmlIntegerAccessor(stream, field);
            }

            public void visitLong(LongField field) throws Exception {
                accessor = new XmlLongAccessor(stream, field);
            }

            public void visitDate(DateField field) throws Exception {
                accessor = new XmlDateAccessor(stream, field);
            }

            public void visitDateTime(DateTimeField field) throws Exception {
                accessor = new XmlDateTimeAccessor(stream, field);
            }

            public void visitStringArray(StringArrayField field) throws Exception {
                accessor = new XmlStringArrayAccessor(stream, field);
            }

            public void visitLongArray(LongArrayField field) throws Exception {
                accessor = new XmlLongArrayAccessor(stream, field);
            }

            public Accessor getAccessor() {
                return accessor;
            }


            private static class XmlBytesAccessor implements BytesAccessor {
                private XmlGlobStream xmlMoStream;
                private BytesField field;

                public XmlBytesAccessor(XmlGlobStream xmlMoStream, BytesField field) {
                    this.xmlMoStream = xmlMoStream;
                    this.field = field;
                }

                public Object getObjectValue() {
                    return getValue();
                }

                public byte[] getValue() {
                    return xmlMoStream.current.get(field);
                }
            }

            private static class XmlStringAccessor implements StringAccessor {
                private XmlGlobStream xmlMoStream;
                private StringField field;

                public XmlStringAccessor(XmlGlobStream xmlMoStream, StringField field) {
                    this.xmlMoStream = xmlMoStream;
                    this.field = field;
                }

                public String getString() {
                    return xmlMoStream.current.get(field);
                }

                public Object getObjectValue() {
                    return getString();
                }
            }

            private static class XmlBooleanAccessor implements BooleanAccessor {
                private XmlGlobStream xmlMoStream;
                private BooleanField field;

                public XmlBooleanAccessor(XmlGlobStream xmlMoStream, BooleanField field) {
                    this.xmlMoStream = xmlMoStream;
                    this.field = field;
                }

                public Boolean getBoolean() {
                    return xmlMoStream.current.get(field);
                }

                public boolean getValue(boolean valueIfNull) {
                    return getBoolean();
                }

                public Object getObjectValue() {
                    return getBoolean();
                }
            }

            private static class XmlDoubleAccessor implements DoubleAccessor {
                private XmlGlobStream xmlMoStream;
                private DoubleField field;

                public XmlDoubleAccessor(XmlGlobStream xmlMoStream, DoubleField field) {
                    this.xmlMoStream = xmlMoStream;
                    this.field = field;
                }

                public Double getDouble() {
                    return xmlMoStream.current.get(field);
                }

                public double getValue(double valueIfNull) {
                    Double value = getDouble();
                    return value == null ? valueIfNull : value;
                }

                public boolean wasNull() {
                    return getDouble() == null;
                }

                public Object getObjectValue() {
                    return getDouble();
                }
            }

            private class XmlIntegerAccessor implements IntegerAccessor {
                private XmlGlobStream xmlMoStream;
                private IntegerField field;

                public XmlIntegerAccessor(XmlGlobStream xmlMoStream, IntegerField field) {
                    this.xmlMoStream = xmlMoStream;
                    this.field = field;
                }

                public Integer getInteger() {
                    return xmlMoStream.current.get(field);
                }

                public int getValue(int valueIfNull) {
                    Integer value = getInteger();
                    return value == null ? valueIfNull : value;
                }

                public boolean wasNull() {
                    return getInteger() == null;
                }

                public Object getObjectValue() {
                    return getInteger();
                }
            }

            private class XmlLongAccessor implements LongAccessor {
                private XmlGlobStream xmlMoStream;
                private LongField field;

                public XmlLongAccessor(XmlGlobStream xmlMoStream, LongField field) {
                    this.xmlMoStream = xmlMoStream;
                    this.field = field;
                }

                public Long getLong() {
                    return xmlMoStream.current.get(field);
                }

                public long getValue(long valueIfNull) {
                    Long value = getLong();
                    return value == null ? valueIfNull : value;
                }

                public boolean wasNull() {
                    return getLong() == null;
                }

                public Object getObjectValue() {
                    return getLong();
                }
            }

            private class XmlDateAccessor implements DateAccessor {
                private XmlGlobStream xmlMoStream;
                private DateField field;

                public XmlDateAccessor(XmlGlobStream xmlMoStream, DateField field) {
                    this.xmlMoStream = xmlMoStream;
                    this.field = field;
                }

                public LocalDate getDate() {
                    return xmlMoStream.current.get(field);
                }

                public boolean wasNull() {
                    return getDate() == null;
                }

                public Object getObjectValue() {
                    return getDate();
                }
            }

            private class XmlDateTimeAccessor implements DateTimeAccessor {
                private XmlGlobStream xmlMoStream;
                private DateTimeField field;

                public XmlDateTimeAccessor(XmlGlobStream xmlMoStream, DateTimeField field) {
                    this.xmlMoStream = xmlMoStream;
                    this.field = field;
                }

                public ZonedDateTime getDateTime() {
                    return xmlMoStream.current.get(field);
                }

                public boolean wasNull() {
                    return getDateTime() == null;
                }

                public Object getObjectValue() {
                    return getDateTime();
                }
            }

            private class XmlStringArrayAccessor implements StringArrayAccessor {
                private final XmlGlobStream xmlMoStream;
                private final StringArrayField field;

                public XmlStringArrayAccessor(XmlGlobStream xmlMoStream, StringArrayField field) {
                    this.xmlMoStream = xmlMoStream;
                    this.field = field;
                }

                public String[] getString() {
                    return xmlMoStream.current.get(field);
                }

                public Object getObjectValue() {
                    return getString();
                }
            }

            private class XmlLongArrayAccessor implements LongArrayAccessor {
                private final XmlGlobStream xmlMoStream;
                private final LongArrayField field;

                public XmlLongArrayAccessor(XmlGlobStream xmlMoStream, LongArrayField field) {
                    this.xmlMoStream = xmlMoStream;
                    this.field = field;
                }

                public long[] getValues() {
                    return xmlMoStream.current.get(field);
                }

                public Object getObjectValue() {
                    return getValues();
                }
            }
        }

    }

    class RootProxyNode extends DefaultXmlNode implements XmlSingleGlobParser.GlobAdder {

        public RootProxyNode() {
        }

        public XmlNode getSubNode(String childName, Attributes xmlAttrs, String uri, String fullName) throws ExceptionHolder {
            try {
                if (childName.equals("root")) {
                    return this;
                } else {
                    XmlSingleGlobParser.parse(childName, xmlAttrs, globModel, this);
                    return this;
                }
            } catch (Exception e) {
                throw new ExceptionHolder(e);
            }
        }

        public void add(MutableGlob mo) {
            XmlGlobStreamReader.this.add(mo);
        }
    }
}
