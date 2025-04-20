package org.globsframework.core.model.format;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.metamodel.utils.GlobTypeComparator;
import org.globsframework.core.model.FieldsValueScanner;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.GlobRepository;
import org.globsframework.core.utils.Strings;
import org.globsframework.core.utils.TablePrinter;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;
import java.util.stream.Collectors;

import static org.globsframework.core.utils.Utils.sort;

public class GlobPrinter {

    public static String toString(final FieldsValueScanner fieldValues) {
        final StringBuilder builder = new StringBuilder();
        if (fieldValues == null) {
            return "null";
        }
        fieldValues.safeApply((field, value) ->
        {
            builder
                    .append("\"").append(field.getName())
                    .append("\":");
            field.toString(builder, value);
            builder.append(",");
        });
        final int length = builder.length();
        if (length > 0 && builder.charAt(length - 1) == ',') {
            builder.deleteCharAt(length - 1);
        }
        return builder.toString();
    }

    public static void print(GlobRepository repository, GlobType... types) {
        GlobPrinter.init(repository).showOnly(types).run();
    }

    public static void print(List<Glob> list) {
        GlobPrinter.init(list).run();
    }

    public static GlobPrinter init(GlobRepository repository) {
        return new GlobPrinter(repository);
    }

    public static GlobPrinter init(List<Glob> list) {
        return new GlobPrinter(list);
    }

    public static GlobPrinter init(Glob glob) {
        List<Glob> list = new ArrayList<>();
        list.add(glob);
        return new GlobPrinter(list);
    }

    public static void print(Glob glob) {
        print(glob, new OutputStreamWriter(System.out));
    }

    public static void print(Glob glob, Writer writer) {
        PrintWriter printer = new PrintWriter(writer);
        printer.println("===== " + glob.getNewKey() + " ======");

        List<Object[]> rows = new ArrayList<Object[]>();
        for (Field field : glob.getType().getFields()) {
            rows.add(new Object[]{field.getName(), glob.getValue(field)});
        }

        TablePrinter.print(new String[]{"Field", "Value"}, rows, true, printer);

        printer.println();
        printer.flush();
    }

    private GlobRepository repository;
    private Set<GlobType> types;
    private List<Glob> globs;
    private String[] filters;
    private List<Field> excludedFields = new ArrayList<Field>();
    private Map<GlobType, Field[]> fieldsForType = new HashMap<GlobType, Field[]>();

    private GlobPrinter(GlobRepository repository) {
        this.repository = repository;
        this.types = repository.getTypes();
        this.globs = repository.getAll();
    }

    private GlobPrinter(List<Glob> list) {
        this.types = list.stream().map(Glob::getType).collect(Collectors.toSet());
        this.globs = list;
    }

    public GlobPrinter showOnly(GlobType... shownTypes) {
        if (shownTypes.length > 0) {
            this.types = new HashSet<GlobType>(Arrays.asList(shownTypes));
        }
        return this;
    }

    public GlobPrinter setTextFilters(String... textFilters) {
        this.filters = textFilters;
        return this;
    }

    public GlobPrinter showFields(GlobType type, Field... fields) {
        fieldsForType.put(type, fields);
        return this;
    }

    public GlobPrinter exclude(Field... fields) {
        this.excludedFields.addAll(Arrays.asList(fields));
        return this;
    }

    public void run(Writer writer) {
        PrintWriter printer = new PrintWriter(writer);
        for (GlobType type : sort(types, GlobTypeComparator.INSTANCE)) {
            printType(type, globs.stream().filter(glob -> glob.getType() == type).collect(Collectors.toList()), printer);
        }
    }

    public void run() {
        run(new OutputStreamWriter(System.out));
    }

    public String toString() {
        StringWriter writer = new StringWriter();
        run(writer);
        return writer.toString();
    }

    private void printType(GlobType type, Collection<Glob> globs, PrintWriter printer) {
        printer.println("===== " + type.getName() + " ======");

        List<Object[]> rows = new ArrayList<Object[]>();
        String[] headerRow = createHeaderRow(type);
        for (Glob glob : globs) {
            String[] row = createRow(type, glob);
            if (filters != null) {
                boolean found = false;
                for (int i = 0; i < row.length && !found; i++) {
                    for (int j = 0; j < filters.length && !found; j++) {
                        if (row[i].contains(filters[j])) {
                            found = true;
                        }
                    }
                }
                if (found) {
                    rows.add(row);
                }
            } else {
                rows.add(row);
            }
        }

        TablePrinter.print(headerRow, rows, true, printer);

        printer.println();
        printer.flush();
    }

    private String[] createRow(GlobType type, Glob glob) {
        List<String> row = new ArrayList<String>();
        for (Field field : getFields(type)) {
            if (excludedFields.contains(field)) {
                continue;
            }
            row.add(getValue(glob, field, glob.getValue(field)));
        }
        return row.toArray(new String[row.size()]);
    }

    private Field[] getFields(GlobType type) {
        Field[] fields = fieldsForType.get(type);
        return fields != null ? fields : type.getFields();
    }

    private String getValue(Glob glob, Field field, Object value) {
        if (value == null) {
            return "";
        }
        if (field instanceof GlobField || field instanceof GlobUnionField) {
            return Arrays.toString(createRow(((Glob) value).getType(), ((Glob) value)));
        } else if (field instanceof GlobArrayField || field instanceof GlobArrayUnionField) {
            StringBuilder builder = new StringBuilder();
            for (Glob globChild : ((Glob[]) value)) {
                builder.append(Arrays.toString(createRow(globChild.getType(), globChild)));
                builder.append(",");
            }
            return builder.toString();
        }
        return Strings.toString(value);
    }

    private String[] createHeaderRow(GlobType type) {
        List<String> row = new ArrayList<String>();
        for (Field field : getFields(type)) {
            if (excludedFields.contains(field)) {
                continue;
            }
            row.add(field.getName());
        }
        return row.toArray(new String[row.size()]);
    }
}

