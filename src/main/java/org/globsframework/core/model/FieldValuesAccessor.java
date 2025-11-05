package org.globsframework.core.model;

import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.utils.Strings;
import org.globsframework.core.utils.exceptions.ItemNotFound;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;


public interface FieldValuesAccessor {
    Glob[] EMPTY_GLOB_ARRAY = new Glob[0];
    boolean[] EMPTY_BOOLEAN_ARRAY = new boolean[0];
    int[] EMPTY_INTEGER_ARRAY = new int[0];
    long[] EMPTY_LONG_ARRAY = new long[0];
    double[] EMPTY_DOUBLE_ARRAY = new double[0];
    BigDecimal[] BIG_DECIMAL_GLOB_ARRAY = new BigDecimal[0];
    String[] EMPTY_STRING_ARRAY = new String[0];

    boolean isSet(Field field) throws ItemNotFound;

    default boolean isNotSet(Field field) throws ItemNotFound {
        return !isSet(field);
    }

    boolean isNull(Field field) throws ItemNotFound;

    default boolean isNotNull(Field field) throws ItemNotFound {
        return !isNull(field);
    }

    Object getValue(Field field) throws ItemNotFound;

    Double get(DoubleField field) throws ItemNotFound;

    double[] get(DoubleArrayField field) throws ItemNotFound;

    Integer get(IntegerField field) throws ItemNotFound;

    int get(IntegerField field, int valueIfNull) throws ItemNotFound;

    int[] get(IntegerArrayField field) throws ItemNotFound;

    String get(StringField field) throws ItemNotFound;

    String[] get(StringArrayField field) throws ItemNotFound;

    Boolean get(BooleanField field) throws ItemNotFound;

    boolean get(BooleanField field, boolean defaultIfNull);

    boolean[] get(BooleanArrayField field);

    boolean isTrue(BooleanField field) throws ItemNotFound;

    Long get(LongField field) throws ItemNotFound;

    long get(LongField field, long valueIfNull) throws ItemNotFound;

    long[] get(LongArrayField field) throws ItemNotFound;

    BigDecimal get(BigDecimalField field) throws ItemNotFound;

    BigDecimal[] get(BigDecimalArrayField field) throws ItemNotFound;

    LocalDate get(DateField field) throws ItemNotFound;

    ZonedDateTime get(DateTimeField field) throws ItemNotFound;

    byte[] get(BytesField field) throws ItemNotFound;

    Glob get(GlobField field) throws ItemNotFound;

    Glob[] get(GlobArrayField field) throws ItemNotFound;

    Glob get(GlobUnionField field) throws ItemNotFound;

    Glob[] get(GlobArrayUnionField field) throws ItemNotFound;

    default String get(StringField field, String valueIfNull) throws ItemNotFound {
        String value = get(field);
        return value != null ? value : valueIfNull;
    }

    default String getOrDefault(StringField field, String valueIfEmpty) throws ItemNotFound {
        String value = get(field);
        return value != null && !value.isEmpty() ? value : valueIfEmpty;
    }

    default double get(DoubleField field, double valueIfNull) throws ItemNotFound{
        Double value = get(field);
        return value != null ? value : valueIfNull;
    }

    default ZonedDateTime get(DateTimeField field, ZonedDateTime valueIfNull) throws ItemNotFound {
        ZonedDateTime value = get(field);
        return value != null ? value : valueIfNull;
    }

    default LocalDate get(DateField field, LocalDate valueIfNull) throws ItemNotFound {
        LocalDate value = get(field);
        return value != null ? value : valueIfNull;
    }

    default BigDecimal get(BigDecimalField field, BigDecimal valueIfNull) throws ItemNotFound {
        BigDecimal bigDecimal = get(field);
        return bigDecimal != null ? bigDecimal : valueIfNull;
    }

    default byte[] get(BytesField field, byte[] valueIfNull) throws ItemNotFound {
        byte[] value = get(field);
        return value != null ? value : valueIfNull;
    }


    default NullableOptional<String> getNullableOpt(StringField field) {
        return new NullableOptional<>(isSet(field), get(field));
    }

    default NullableOptional<String[]> getNullableOpt(StringArrayField field) {
        return new NullableOptional<>(isSet(field), get(field));
    }

    default NullableOptional<Integer> getNullableOpt(IntegerField field) {
        return new NullableOptional<>(isSet(field), get(field));
    }

    default NullableOptional<int[]> getNullableOpt(IntegerArrayField field) {
        return new NullableOptional<>(isSet(field), get(field));
    }

    default NullableOptional<Long> getNullableOpt(LongField field) {
        return new NullableOptional<>(isSet(field), get(field));
    }

    default NullableOptional<long[]> getNullableOpt(LongArrayField field) {
        return new NullableOptional<>(isSet(field), get(field));
    }

    default NullableOptional<Double> getNullableOpt(DoubleField field) {
        return new NullableOptional<>(isSet(field), get(field));
    }

    default NullableOptional<double[]> getNullableOpt(DoubleArrayField field) {
        return new NullableOptional<>(isSet(field), get(field));
    }

    default NullableOptional<BigDecimal> getNullableOpt(BigDecimalField field) {
        return new NullableOptional<>(isSet(field), get(field));
    }

    default NullableOptional<BigDecimal[]> getNullableOpt(BigDecimalArrayField field) {
        return new NullableOptional<>(isSet(field), get(field));
    }

    default NullableOptional<LocalDate> getNullableOpt(DateField field) {
        return new NullableOptional<>(isSet(field), get(field));
    }

    default NullableOptional<ZonedDateTime> getNullableOpt(DateTimeField field) {
        return new NullableOptional<>(isSet(field), get(field));
    }

    default NullableOptional<Boolean> getNullableOpt(BooleanField field) {
        return new NullableOptional<>(isSet(field), get(field));
    }

    default NullableOptional<boolean[]> getNullableOpt(BooleanArrayField field) {
        return new NullableOptional<>(isSet(field), get(field));
    }

    default NullableOptional<Glob> getNullableOpt(GlobField field) {
        return new NullableOptional<>(isSet(field), get(field));
    }

    default NullableOptional<Glob[]> getNullableOpt(GlobArrayField field) {
        return new NullableOptional<>(isSet(field), get(field));
    }

    default Optional<String> getOpt(StringField field) {
        return Optional.ofNullable(get(field));
    }

    default Optional<String> getOptNotEmpty(StringField field) {
        final String value = get(field);
        return Strings.isNullOrEmpty(value) ? Optional.empty() : Optional.of(value);
    }

    default Optional<LocalDate> getOpt(DateField field) {
        return Optional.ofNullable(get(field));
    }

    default Optional<ZonedDateTime> getOpt(DateTimeField field) {
        return Optional.ofNullable(get(field));
    }

    default Optional<Long> getOpt(LongField field) {
        return Optional.ofNullable(get(field));
    }

    default Optional<Integer> getOpt(IntegerField field) {
        return Optional.ofNullable(get(field));
    }

    default Optional<Double> getOpt(DoubleField field) {
        return Optional.ofNullable(get(field));
    }

    default Optional<Boolean> getOpt(BooleanField field) {
        return Optional.ofNullable(get(field));
    }

    default Optional<String[]> getOpt(StringArrayField field) {
        return Optional.ofNullable(get(field));
    }

    default Optional<double[]> getOpt(DoubleArrayField field) {
        return Optional.ofNullable(get(field));
    }

    default Optional<long[]> getOpt(LongArrayField field) {
        return Optional.ofNullable(get(field));
    }

    default Optional<int[]> getOpt(IntegerArrayField field) {
        return Optional.ofNullable(get(field));
    }

    default Optional<boolean[]> getOpt(BooleanArrayField field) {
        return Optional.ofNullable(get(field));
    }

    default Optional<BigDecimal> getOpt(BigDecimalField field) {
        return Optional.ofNullable(get(field));
    }

    default Optional<BigDecimal[]> getOpt(BigDecimalArrayField field) {
        return Optional.ofNullable(get(field));
    }

    default Optional<Object> getOptValue(Field field) {
        return Optional.ofNullable(getValue(field));
    }

    default Optional<Glob> getOpt(GlobField field) {
        return Optional.ofNullable(get(field));
    }

    default Optional<Glob> getOptional(GlobField field) {
        return getOpt(field);
    }

    default Optional<Glob> getOpt(GlobUnionField field) {
        return Optional.ofNullable(get(field));
    }

    default Optional<Glob> getOptional(GlobUnionField field) {
        return getOpt(field);
    }

    default Optional<Glob[]> getOpt(GlobArrayField field) {
        return Optional.ofNullable(get(field));
    }

    default Optional<Glob[]> getOpt(GlobArrayUnionField field) {
        return Optional.ofNullable(get(field));
    }


    default boolean[] getOrEmpty(BooleanArrayField field) {
        boolean[] d = get(field);
        return d != null ? d : EMPTY_BOOLEAN_ARRAY;
    }

    default int[] getOrEmpty(IntegerArrayField field) {
        int[] d = get(field);
        return d != null ? d : EMPTY_INTEGER_ARRAY;
    }

    default int[] getOrDefault(IntegerArrayField field, int[] defaultValues) {
        int[] d = get(field);
        return d != null ? d : defaultValues;
    }

    default long[] getOrEmpty(LongArrayField field) {
        long[] d = get(field);
        return d != null ? d : EMPTY_LONG_ARRAY;
    }

    default double[] getOrEmpty(DoubleArrayField field) {
        double[] d = get(field);
        return d != null ? d : EMPTY_DOUBLE_ARRAY;
    }

    default BigDecimal[] getOrEmpty(BigDecimalArrayField field) {
        BigDecimal[] globs = get(field);
        return globs != null ? globs : BIG_DECIMAL_GLOB_ARRAY;
    }

    default String[] getOrEmpty(StringArrayField field) {
        String[] d = get(field);
        return d != null ? d : EMPTY_STRING_ARRAY;
    }

    default Stream<String> stream(StringArrayField field) {
        final String[] d = get(field);
        return d != null ? Stream.of(d) : Stream.empty();
    }

    default IntStream stream(IntegerArrayField field) {
        final int[] d = get(field);
        return d != null ? IntStream.of(d) : IntStream.empty();
    }

    default LongStream stream(LongArrayField field) {
        final long[] d = get(field);
        return d != null ? LongStream.of(d) : LongStream.empty();
    }

    default DoubleStream stream(DoubleArrayField field) {
        final double[] d = get(field);
        return d != null ? DoubleStream.of(d) : DoubleStream.empty();
    }

    default Stream<Glob> stream(GlobArrayField field) {
        final Glob[] d = get(field);
        return d != null ? Stream.of(d) : Stream.empty();
    }

    default Stream<Glob> stream(GlobArrayUnionField field) {
        final Glob[] d = get(field);
        return d != null ? Stream.of(d) : Stream.empty();
    }

    default NullableOptional<String[]> getNullableOrEmptyOpt(StringArrayField field) {
        return new NullableOptional<>(isSet(field), getOrEmpty(field));
    }

    default NullableOptional<int[]> getNullableOrEmptyOpt(IntegerArrayField field) {
        return new NullableOptional<>(isSet(field), getOrEmpty(field));
    }

    default NullableOptional<double[]> getNullableOrEmptyOpt(DoubleArrayField field) {
        return new NullableOptional<>(isSet(field), getOrEmpty(field));
    }

    default NullableOptional<long[]> getNullableOrEmptyOpt(LongArrayField field) {
        return new NullableOptional<>(isSet(field), getOrEmpty(field));
    }

    default NullableOptional<boolean[]> getNullableOrEmptyOpt(BooleanArrayField field) {
        return new NullableOptional<>(isSet(field), getOrEmpty(field));
    }

    default NullableOptional<BigDecimal[]> getNullableOrEmptyOpt(BigDecimalArrayField field) {
        return new NullableOptional<>(isSet(field), getOrEmpty(field));
    }

    default NullableOptional<Glob[]> getNullableOrEmptyOpt(GlobArrayField field) {
        return new NullableOptional<>(isSet(field), getOrEmpty(field));
    }

    default Glob[] getOrEmpty(GlobArrayUnionField field) {
        Glob[] globs = get(field);
        return globs != null ? globs : EMPTY_GLOB_ARRAY;
    }

    default Glob[] getOrEmpty(GlobArrayField field) {
        Glob[] globs = get(field);
        return globs != null ? globs : EMPTY_GLOB_ARRAY;
    }

    default String getNotNull(StringField field) {
        String value = get(field);
        if (value == null) {
            throw new NullPointerException(field.getFullName() + " should not be null.");
        }
        return value;
    }

    default String getNotEmpty(StringField field) {
        String value = get(field);
        if (value == null) {
            throw new NullPointerException(field.getFullName() + " should not be null.");
        }
        if (value.isEmpty()) {
            throw new RuntimeException(field.getFullName() + " should not be empty.");
        }
        return value;
    }

    default Glob[] getNotNull(GlobArrayUnionField field) {
        Glob[] value = get(field);
        if (value == null) {
            throw new NullPointerException(field.getFullName() + " should not be null.");
        }
        return value;
    }

    default Glob[] getNotNull(GlobArrayField field) {
        Glob[] value = get(field);
        if (value == null) {
            throw new NullPointerException(field.getFullName() + " should not be null.");
        }
        return value;
    }

    default Glob getNotNull(GlobUnionField field) {
        Glob value = get(field);
        if (value == null) {
            throw new NullPointerException(field.getFullName() + " should not be null.");
        }
        return value;
    }

    default Glob getNotNull(GlobField field) {
        Glob value = get(field);
        if (value == null) {
            throw new NullPointerException(field.getFullName() + " should not be null.");
        }
        return value;
    }

    default Double getNotNull(DoubleField field) {
        Double value = get(field);
        if (value == null) {
            throw new NullPointerException(field.getFullName() + " should not be null.");
        }
        return value;
    }

    default Long getNotNull(LongField field) {
        Long value = get(field);
        if (value == null) {
            throw new NullPointerException(field.getFullName() + " should not be null.");
        }
        return value;
    }

    default Integer getNotNull(IntegerField field) {
        Integer value = get(field);
        if (value == null) {
            throw new NullPointerException(field.getFullName() + " should not be null.");
        }
        return value;
    }

    default ZonedDateTime getNotNull(DateTimeField field) {
        ZonedDateTime value = get(field);
        if (value == null) {
            throw new NullPointerException(field.getFullName() + " should not be null.");
        }
        return value;
    }

    default LocalDate getNotNull(DateField field) {
        LocalDate value = get(field);
        if (value == null) {
            throw new NullPointerException(field.getFullName() + " should not be null.");
        }
        return value;
    }

    default double[] getNotNull(DoubleArrayField field) {
        double[] value = get(field);
        if (value == null) {
            throw new NullPointerException(field.getFullName() + " should not be null.");
        }
        return value;
    }

    default String[] getNotNull(StringArrayField field) {
        String[] value = get(field);
        if (value == null) {
            throw new NullPointerException(field.getFullName() + " should not be null.");
        }
        return value;
    }

}
