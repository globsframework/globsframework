package org.globsframework.core.utils;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.metamodel.index.*;
import org.globsframework.core.model.Glob;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Generates the java source that declares a {@link GlobType} : the same code one would write by hand
 * with a {@link org.globsframework.core.metamodel.GlobTypeBuilder} (see DummyObject in the tests).
 * <p>
 * Annotations are the hard part : an annotation is a {@link Glob}, and the java class that holds its
 * {@code GlobType TYPE} (and therefore the import and the static factory to call) is not referenced by the
 * metamodel. It is found back by introspection, in that order :
 * <ol>
 *     <li>an explicit mapping given through {@link #declare(GlobType, Class)},</li>
 *     <li>a lookup of {@code <package>.<GlobType name>} over the registered packages then over every
 *     package already loaded in the class loader (the holder class is necessarily loaded, since its static
 *     block is what created the GlobType we are looking at).</li>
 * </ol>
 * Once the class is known, the code to rebuild the annotation is chosen by trying, and checking the result :
 * a public static {@code Glob} constant with the same values ({@code AutoIncrement.INSTANCE},
 * {@code KeyField.ZERO}), else a static {@code createXXX(...)} whose invocation gives back the same values
 * ({@code MaxSize.create(255)}), else the generic {@code MaxSize.TYPE.instantiate().set(MaxSize.VALUE, 255)}.
 */
public class GenerateBuilder {
    private final Map<GlobType, Class<?>> holders = new HashMap<>();
    private final Map<GlobType, String> javaNames = new HashMap<>();
    private final Set<String> searchPackages = new LinkedHashSet<>();
    private boolean scanLoadedPackages = true;

    public static String generate(String packageName, String className, GlobType globType) {
        return new GenerateBuilder().generateSource(packageName, className, globType);
    }

    /**
     * Declares the java class holding a GlobType, when introspection can not find it back
     * (or to avoid the lookup).
     */
    public GenerateBuilder declare(GlobType globType, Class<?> javaClass) {
        holders.put(globType, javaClass);
        javaNames.put(globType, javaClass.getName());
        return this;
    }

    /**
     * Declares the fully qualified name of the class holding a GlobType that is not (yet) on the classpath,
     * typically another type being generated in the same batch.
     */
    public GenerateBuilder declare(GlobType globType, String fullyQualifiedName) {
        javaNames.put(globType, fullyQualifiedName);
        return this;
    }

    public GenerateBuilder addSearchPackage(String packageName) {
        searchPackages.add(packageName);
        return this;
    }

    public GenerateBuilder scanLoadedPackages(boolean scan) {
        scanLoadedPackages = scan;
        return this;
    }

    public String generateSource(String packageName, String className, GlobType globType) {
        return new Generation(packageName, className, globType).run();
    }

    /**
     * Regenerates the declaration of the types held by the given classes :
     * {@code GenerateBuilder org.globsframework.core.metamodel.DummyObject [-d outputDir]}.
     */
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.err.println("usage : GenerateBuilder <class holding a GlobType> ... [-d <output dir>]");
            System.exit(1);
        }
        Path outputDir = null;
        List<String> classNames = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            if ("-d".equals(args[i])) {
                outputDir = Path.of(args[++i]);
            } else {
                classNames.add(args[i]);
            }
        }
        GenerateBuilder generateBuilder = new GenerateBuilder();
        for (String className : classNames) {
            Class<?> aClass = Class.forName(className);
            GlobType globType = typeOf(aClass);
            if (globType == null) {
                throw new RuntimeException("No 'public static GlobType' found on " + className);
            }
            String source = generateBuilder.generateSource(aClass.getPackageName(), aClass.getSimpleName(), globType);
            if (outputDir == null) {
                System.out.println(source);
            } else {
                Path target = outputDir.resolve(aClass.getName().replace('.', '/') + ".java");
                Files.createDirectories(target.getParent());
                Files.writeString(target, source, StandardCharsets.UTF_8);
                System.out.println(target);
            }
        }
    }

    // ------------------------------------------------------------------ generation

    private class Generation {
        private final String packageName;
        private final String className;
        private final GlobType globType;
        private final Set<String> imports = new TreeSet<>();
        private final Map<String, String> names = new HashMap<>();
        private final Map<Field, String> constantNames = new LinkedHashMap<>();
        private final Set<String> usedConstantNames = new HashSet<>();
        private final Set<String> unresolved = new TreeSet<>();

        Generation(String packageName, String className, GlobType globType) {
            this.packageName = packageName;
            this.className = className;
            this.globType = globType;
        }

        String run() {
            usedConstantNames.add("TYPE");
            names.put(className, packageName == null || packageName.isEmpty() ? className : packageName + "." + className);
            for (Field field : globType.getFields()) {
                constantNames.put(field, newConstantName(field.getName()));
            }
            String body = body();       // fills imports
            StringBuilder out = new StringBuilder();
            if (packageName != null && !packageName.isEmpty()) {
                out.append("package ").append(packageName).append(";\n\n");
            }
            for (String anImport : imports) {
                out.append("import ").append(anImport).append(";\n");
            }
            if (!imports.isEmpty()) {
                out.append("\n");
            }
            out.append(body);
            return out.toString();
        }

        private String body() {
            StringBuilder declarations = new StringBuilder();
            StringBuilder init = new StringBuilder();

            String builder = use("org.globsframework.core.metamodel.GlobTypeBuilder");
            String factory = use("org.globsframework.core.metamodel.GlobTypeBuilderFactory");
            init.append("        ").append(builder).append(" typeBuilder = ")
                    .append(factory).append(".create(").append(literal(globType.getName())).append(");\n");

            globType.streamAnnotations()
                    .sorted(Comparator.comparing(glob -> glob.getType().getName()))
                    .forEach(annotation -> init.append("        typeBuilder.addAnnotation(")
                            .append(annotation(annotation)).append(");\n"));

            for (Field field : globType.getFields()) {
                declarations.append("    public static final ").append(fieldClass(field))
                        .append(" ").append(constantNames.get(field)).append(";\n");
                init.append("        ").append(constantNames.get(field)).append(" = typeBuilder.")
                        .append(declareCall(field)).append(";\n");
            }

            for (Index index : globType.getIndices()) {
                String constant = newConstantName(index.getName());
                declarations.append("    public static final ").append(indexClass(index))
                        .append(" ").append(constant).append(";\n");
                init.append("        ").append(constant).append(" = typeBuilder.")
                        .append(indexCall(index)).append(";\n");
            }

            init.append("        TYPE = typeBuilder.build();\n");

            StringBuilder out = new StringBuilder();
            if (!unresolved.isEmpty()) {
                out.append("// unresolved type(s) : ").append(String.join(", ", unresolved)).append("\n");
            }
            out.append("public class ").append(className).append(" {\n");
            out.append("    public static final ").append(use("org.globsframework.core.metamodel.GlobType"))
                    .append(" TYPE;\n\n");
            out.append(declarations).append("\n");
            out.append("    static {\n").append(init).append("    }\n");
            out.append("}\n");
            return out.toString();
        }

        // --------------------------------------------------------- fields

        private String declareCall(Field field) {
            StringBuilder call = new StringBuilder();
            call.append(declareMethod(field)).append("(").append(literal(field.getName()));
            switch (field.getDataType()) {
                case Glob:
                    call.append(", () -> ").append(typeRef(((GlobField<?>) field).getTargetType())).append(".TYPE");
                    break;
                case GlobArray:
                    call.append(", () -> ").append(typeRef(((GlobArrayField<?>) field).getTargetType())).append(".TYPE");
                    break;
                case GlobUnion:
                    call.append(", ").append(suppliers(((GlobUnionField) field).getTargetTypes()));
                    break;
                case GlobUnionArray:
                    call.append(", ").append(suppliers(((GlobArrayUnionField) field).getTargetTypes()));
                    break;
            }
            field.streamAnnotations()
                    .sorted(Comparator.comparing(glob -> glob.getType().getName()))
                    .forEach(annotation -> call.append(", ").append(annotation(annotation)));
            return call.append(")").toString();
        }

        private String suppliers(Collection<GlobType> types) {
            return "new " + use("java.util.function.Supplier") + "[]{" +
                    types.stream().map(type -> "() -> " + typeRef(type) + ".TYPE").collect(Collectors.joining(", ")) +
                    "}";
        }

        private String fieldClass(Field field) {
            String simpleName = switch (field.getDataType()) {
                case String -> "StringField";
                case StringArray -> "StringArrayField";
                case Double -> "DoubleField";
                case DoubleArray -> "DoubleArrayField";
                case BigDecimal -> "BigDecimalField";
                case BigDecimalArray -> "BigDecimalArrayField";
                case Long -> "LongField";
                case LongArray -> "LongArrayField";
                case Integer -> "IntegerField";
                case IntegerArray -> "IntegerArrayField";
                case Boolean -> "BooleanField";
                case BooleanArray -> "BooleanArrayField";
                case Date -> "DateField";
                case DateTime -> "DateTimeField";
                case Bytes -> "BytesField";
                case Glob -> "GlobField";
                case GlobArray -> "GlobArrayField";
                case GlobUnion -> "GlobUnionField";
                case GlobUnionArray -> "GlobArrayUnionField";
            };
            String name = use("org.globsframework.core.metamodel.fields." + simpleName);
            if (field instanceof GlobField<?> globField) {
                return name + "<" + generic(globField.getTargetType()) + ">";
            }
            if (field instanceof GlobArrayField<?> globArrayField) {
                return name + "<" + generic(globArrayField.getTargetType()) + ">";
            }
            return name;
        }

        private String generic(GlobType targetType) {
            String javaName = javaName(targetType);
            return javaName == null ? "?" : use(javaName);
        }

        private String declareMethod(Field field) {
            return switch (field.getDataType()) {
                case String -> "declareStringField";
                case StringArray -> "declareStringArrayField";
                case Double -> "declareDoubleField";
                case DoubleArray -> "declareDoubleArrayField";
                case BigDecimal -> "declareBigDecimalField";
                case BigDecimalArray -> "declareBigDecimalArrayField";
                case Long -> "declareLongField";
                case LongArray -> "declareLongArrayField";
                case Integer -> "declareIntegerField";
                case IntegerArray -> "declareIntegerArrayField";
                case Boolean -> "declareBooleanField";
                case BooleanArray -> "declareBooleanArrayField";
                case Date -> "declareDateField";
                case DateTime -> "declareDateTimeField";
                case Bytes -> "declareBytesField";
                case Glob -> "declareGlobField";
                case GlobArray -> "declareGlobArrayField";
                case GlobUnion -> "declareGlobUnionField";
                case GlobUnionArray -> "declareGlobUnionArrayField";
            };
        }

        // --------------------------------------------------------- indices

        private String indexClass(Index index) {
            String simpleName = index instanceof UniqueIndex ? "UniqueIndex" :
                    index instanceof NotUniqueIndex ? "NotUniqueIndex" :
                            index instanceof MultiFieldUniqueIndex ? "MultiFieldUniqueIndex" : "MultiFieldNotUniqueIndex";
            return use("org.globsframework.core.metamodel.index." + simpleName);
        }

        private String indexCall(Index index) {
            String method = index instanceof UniqueIndex ? "addUniqueIndex" :
                    index instanceof NotUniqueIndex ? "addNotUniqueIndex" :
                            index instanceof MultiFieldUniqueIndex ? "addMultiFieldUniqueIndex" : "addMultiFieldNotUniqueIndex";
            String fields = index.fields().map(constantNames::get).collect(Collectors.joining(", "));
            return method + "(" + literal(index.getName()) + ", " + fields + ")";
        }

        // --------------------------------------------------------- annotations

        private String annotation(Glob annotation) {
            GlobType type = annotation.getType();
            Class<?> holder = findHolder(type);
            if (holder == null) {
                unresolved.add(type.getName());
                return "/* TODO unknown java class for annotation " + type.getName() + " : " + annotation + " */ null";
            }
            String constant = findConstant(holder, annotation);
            if (constant != null) {
                return use(holder.getName()) + "." + constant;
            }
            String create = findCreate(holder, annotation);
            if (create != null) {
                return create;
            }
            return instantiate(holder, annotation);
        }

        private String findConstant(Class<?> holder, Glob annotation) {
            for (java.lang.reflect.Field field : holder.getFields()) {
                if (Modifier.isStatic(field.getModifiers()) && Glob.class.isAssignableFrom(field.getType())) {
                    try {
                        Glob value = (Glob) field.get(null);
                        if (value != null && sameValues(value, annotation)) {
                            return field.getName();
                        }
                    } catch (IllegalAccessException e) {
                        // ignored : not accessible, try the next one
                    }
                }
            }
            return null;
        }

        private String findCreate(Class<?> holder, Glob annotation) {
            List<Field> setFields = Arrays.stream(annotation.getType().getFields())
                    .filter(annotation::isSet)
                    .collect(Collectors.toList());
            List<Method> candidates = Arrays.stream(holder.getMethods())
                    .filter(method -> Modifier.isStatic(method.getModifiers()))
                    .filter(method -> method.getName().startsWith("create"))
                    .filter(method -> Glob.class.isAssignableFrom(method.getReturnType()))
                    .filter(method -> method.getParameterCount() <= setFields.size())
                    .sorted(Comparator.comparingInt(Method::getParameterCount))
                    .collect(Collectors.toList());
            for (Method method : candidates) {
                List<Field> args = matchArguments(method, annotation, setFields);
                if (args == null) {
                    continue;
                }
                try {
                    Object[] values = args.stream().map(annotation::getValue).toArray();
                    Object result = method.invoke(null, values);
                    if (result instanceof Glob glob && sameValues(glob, annotation)) {
                        return use(holder.getName()) + "." + method.getName() + "(" +
                                args.stream().map(field -> literal(field, annotation.getValue(field)))
                                        .collect(Collectors.joining(", ")) + ")";
                    }
                } catch (Exception e) {
                    // ignored : that method was not the right one
                }
            }
            return null;
        }

        /**
         * Greedily maps the values of the annotation to the parameters of the method, in order.
         * The result of the invocation is checked afterwards, so a wrong guess is harmless.
         */
        private List<Field> matchArguments(Method method, Glob annotation, List<Field> setFields) {
            List<Field> remaining = new ArrayList<>(setFields);
            List<Field> args = new ArrayList<>();
            for (Class<?> parameterType : method.getParameterTypes()) {
                Field found = null;
                for (Field field : remaining) {
                    Object value = annotation.getValue(field);
                    if (value != null && box(parameterType).isInstance(value)) {
                        found = field;
                        break;
                    }
                }
                if (found == null) {
                    return null;
                }
                remaining.remove(found);
                args.add(found);
            }
            return args;
        }

        private String instantiate(Class<?> holder, Glob annotation) {
            StringBuilder out = new StringBuilder();
            String holderName = use(holder.getName());
            out.append(holderName).append(".TYPE.instantiate()");
            for (Field field : annotation.getType().getFields()) {
                if (!annotation.isSet(field)) {
                    continue;
                }
                Object value = annotation.getValue(field);
                String constant = fieldConstant(holder, field);
                if (constant != null) {
                    out.append(".set(").append(holderName).append(".").append(constant);
                } else {
                    out.append(".setValue(").append(holderName).append(".TYPE.getField(")
                            .append(literal(field.getName())).append(")");
                }
                out.append(", ").append(literal(field, value)).append(")");
            }
            return out.toString();
        }

        private String fieldConstant(Class<?> holder, Field target) {
            for (java.lang.reflect.Field field : holder.getFields()) {
                if (Modifier.isStatic(field.getModifiers()) && Field.class.isAssignableFrom(field.getType())) {
                    try {
                        if (field.get(null) == target) {
                            return field.getName();
                        }
                    } catch (IllegalAccessException e) {
                        // ignored
                    }
                }
            }
            return null;
        }

        // --------------------------------------------------------- values

        private String literal(Field field, Object value) {
            if (value == null) {
                return "null";
            }
            return switch (field.getDataType()) {
                case String -> literal((String) value);
                case Integer -> value.toString();
                case Long -> value + "L";
                case Double -> doubleLiteral((Double) value);
                case Boolean -> value.toString();
                case BigDecimal -> "new " + use("java.math.BigDecimal") + "(" + literal(value.toString()) + ")";
                case Date -> use("java.time.LocalDate") + ".parse(" + literal(value.toString()) + ")";
                case DateTime -> use("java.time.ZonedDateTime") + ".parse(" + literal(value.toString()) + ")";
                case Bytes -> array("byte", value, v -> "(byte) " + v);
                case StringArray -> array("String", value, v -> literal((String) v));
                case IntegerArray -> array("int", value, Object::toString);
                case LongArray -> array("long", value, v -> v + "L");
                case DoubleArray -> array("double", value, v -> doubleLiteral((Double) v));
                case BooleanArray -> array("boolean", value, Object::toString);
                case BigDecimalArray -> array(use("java.math.BigDecimal"), value,
                        v -> "new " + use("java.math.BigDecimal") + "(" + literal(v.toString()) + ")");
                case Glob, GlobUnion -> annotation((Glob) value);
                case GlobArray, GlobUnionArray -> array(use("org.globsframework.core.model.Glob"), value,
                        v -> annotation((Glob) v));
            };
        }

        private String array(String componentType, Object array, java.util.function.Function<Object, String> toLiteral) {
            StringBuilder out = new StringBuilder("new ").append(componentType).append("[]{");
            int length = java.lang.reflect.Array.getLength(array);
            for (int i = 0; i < length; i++) {
                if (i != 0) {
                    out.append(", ");
                }
                Object value = java.lang.reflect.Array.get(array, i);
                out.append(value == null ? "null" : toLiteral.apply(value));
            }
            return out.append("}").toString();
        }

        private String doubleLiteral(Double value) {
            if (value.isNaN()) {
                return "Double.NaN";
            }
            if (value.isInfinite()) {
                return value > 0 ? "Double.POSITIVE_INFINITY" : "Double.NEGATIVE_INFINITY";
            }
            return value.toString();
        }

        private String literal(String value) {
            if (value == null) {
                return "null";
            }
            StringBuilder out = new StringBuilder("\"");
            for (int i = 0; i < value.length(); i++) {
                char c = value.charAt(i);
                switch (c) {
                    case '"' -> out.append("\\\"");
                    case '\\' -> out.append("\\\\");
                    case '\n' -> out.append("\\n");
                    case '\r' -> out.append("\\r");
                    case '\t' -> out.append("\\t");
                    default -> {
                        if (c < 0x20 || c > 0x7e) {
                            out.append(String.format("\\u%04x", (int) c));
                        } else {
                            out.append(c);
                        }
                    }
                }
            }
            return out.append("\"").toString();
        }

        // --------------------------------------------------------- names & imports

        private String typeRef(GlobType type) {
            String javaName = javaName(type);
            if (javaName == null) {
                unresolved.add(type.getName());
                return "/* TODO unknown java class for " + type.getName() + " */";
            }
            return use(javaName);
        }

        private String javaName(GlobType type) {
            String javaName = javaNames.get(type);
            if (javaName != null) {
                return javaName;
            }
            Class<?> holder = findHolder(type);
            return holder == null ? null : holder.getName();
        }

        /**
         * Registers an import and returns the name to use in the generated code.
         */
        private String use(String fullyQualifiedName) {
            int lastDot = fullyQualifiedName.lastIndexOf('.');
            String simpleName = fullyQualifiedName.substring(lastDot + 1);
            String aPackage = lastDot == -1 ? "" : fullyQualifiedName.substring(0, lastDot);
            String known = names.get(simpleName);
            if (known != null) {
                return known.equals(fullyQualifiedName) ? simpleName : fullyQualifiedName;
            }
            names.put(simpleName, fullyQualifiedName);
            if (!aPackage.equals(packageName) && !aPackage.equals("java.lang")) {
                imports.add(fullyQualifiedName);
            }
            return simpleName;
        }

        private String newConstantName(String name) {
            String constant = toConstantName(name);
            String unique = constant;
            for (int i = 2; usedConstantNames.contains(unique); i++) {
                unique = constant + "_" + i;
            }
            usedConstantNames.add(unique);
            return unique;
        }
    }

    // ------------------------------------------------------------------ introspection

    /**
     * Finds back the java class holding the {@code GlobType TYPE} of a type.
     */
    public Class<?> findHolder(GlobType globType) {
        if (holders.containsKey(globType)) {
            return holders.get(globType);
        }
        Class<?> found = fromPackages(globType);
        holders.put(globType, found);
        return found;
    }


    private Class<?> fromPackages(GlobType globType) {
        List<String> names = new ArrayList<>();
        String simpleName = globType.getName().substring(globType.getName().lastIndexOf('.') + 1);
        names.add(simpleName);
        if (!simpleName.isEmpty() && Character.isLowerCase(simpleName.charAt(0))) {
            names.add(Character.toUpperCase(simpleName.charAt(0)) + simpleName.substring(1));
        }
        for (String packageName : packagesToSearch()) {
            for (String name : names) {
                Class<?> found = load(packageName + "." + name, globType);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    private Collection<String> packagesToSearch() {
        Set<String> packages = new LinkedHashSet<>(searchPackages);
        if (scanLoadedPackages) {
            for (ClassLoader loader = Thread.currentThread().getContextClassLoader();
                 loader != null; loader = loader.getParent()) {
                for (Package aPackage : loader.getDefinedPackages()) {
                    packages.add(aPackage.getName());
                }
            }
        }
        return packages;
    }

    private Class<?> load(String className, GlobType globType) {
        try {
            Class<?> aClass = Class.forName(className, true, Thread.currentThread().getContextClassLoader());
            return typeOf(aClass) == globType ? aClass : null;
        } catch (Throwable e) {
            return null;
        }
    }

    private static GlobType typeOf(Class<?> aClass) {
        for (java.lang.reflect.Field field : aClass.getFields()) {
            if (Modifier.isStatic(field.getModifiers()) && field.getType() == GlobType.class) {
                try {
                    return (GlobType) field.get(null);
                } catch (IllegalAccessException e) {
                    return null;
                }
            }
        }
        return null;
    }

    private static Class<?> box(Class<?> type) {
        if (!type.isPrimitive()) {
            return type;
        }
        if (type == int.class) return Integer.class;
        if (type == long.class) return Long.class;
        if (type == double.class) return Double.class;
        if (type == boolean.class) return Boolean.class;
        if (type == byte.class) return Byte.class;
        if (type == short.class) return Short.class;
        if (type == char.class) return Character.class;
        if (type == float.class) return Float.class;
        return type;
    }

    /**
     * Compares all the values, whereas Glob.equals only compares the key fields (and so returns true
     * for any two globs of a type without key, which is the case of most annotations).
     */
    public static boolean sameValues(Glob glob, Glob other) {
        if (glob.getType() != other.getType()) {
            return false;
        }
        for (Field field : glob.getType().getFields()) {
            if (glob.isSet(field) != other.isSet(field)
                    || !field.valueEqual(glob.getValue(field), other.getValue(field))) {
                return false;
            }
        }
        return true;
    }

    public static String toConstantName(String name) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isUpperCase(c) && i > 0 && !Character.isUpperCase(name.charAt(i - 1))
                    && out.length() > 0 && out.charAt(out.length() - 1) != '_') {
                out.append('_');
            }
            out.append(Character.isJavaIdentifierPart(c) ? Character.toUpperCase(c) : '_');
        }
        if (out.length() == 0 || !Character.isJavaIdentifierStart(out.charAt(0))) {
            out.insert(0, '_');
        }
        return out.toString();
    }
}
