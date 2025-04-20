package org.globsframework.core.model.utils;

import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.metamodel.links.FieldMappingFunction;
import org.globsframework.core.metamodel.links.Link;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.GlobRepository;
import org.globsframework.core.model.Key;
import org.globsframework.core.utils.Strings;
import org.globsframework.core.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public class GlobMatchers {

    public static Predicate<Glob> ALL = new Predicate<Glob>() {
        public boolean test(Glob item) {
            return true;
        }

        public String toString() {
            return "all";
        }
    };

    public static Predicate<Glob> NONE = new Predicate<Glob>() {
        public boolean test(Glob item) {
            return false;
        }

        public String toString() {
            return "none";
        }
    };

    public static Predicate<Glob> fieldEquals(IntegerField field, Integer value) {
        return fieldEqualsObject(field, value);
    }

    public static Predicate<Glob> fieldIn(final IntegerField field, final Integer... values) {
        return new Predicate<Glob>() {
            public boolean test(Glob item) {
                Integer fieldValue = item.get(field);
                for (Integer value : values) {
                    if (Utils.equal(fieldValue, value)) {
                        return true;
                    }
                }
                return false;
            }

            public String toString() {
                return field.getFullName() + " in " + Arrays.toString(values);
            }
        };
    }

    public static Predicate<Glob> fieldEquals(StringField field, String value) {
        return fieldEqualsObject(field, value);
    }

    public static Predicate<Glob> fieldEqualsIgnoreCase(final StringField field, final String value) {
        return new Predicate<Glob>() {
            public boolean test(Glob glob) {
                return Utils.equalIgnoreCase(glob.get(field), value);
            }

            public String toString() {
                return field.getFullName() + " equals [ignoreCase] " + value;
            }
        };
    }

    public static Predicate<Glob> fieldEquals(DoubleField field, Double value) {
        return fieldEqualsObject(field, value);
    }

    public static Predicate<Glob> fieldEquals(BlobField field, byte[] value) {
        return fieldEqualsObject(field, value);
    }

    public static Predicate<Glob> isTrue(BooleanField field) {
        return fieldEqualsObject(field, true);
    }

    public static Predicate<Glob> isNotTrue(final BooleanField field) {
        return new Predicate<Glob>() {
            public boolean test(Glob item) {
                Boolean value = item.get(field);
                return value == null || value == false;
            }

            public String toString() {
                return field.getName() + " is not true";
            }
        };
    }

    public static Predicate<Glob> isFalse(BooleanField field) {
        return fieldEqualsObject(field, false);
    }

    public static Predicate<Glob> fieldEquals(BooleanField field, Boolean value) {
        return fieldEqualsObject(field, value);
    }

    public static Predicate<Glob> fieldEquals(LongField field, Long value) {
        return fieldEqualsObject(field, value);
    }

    public static Predicate<Glob> fieldEqualsObject(Field field, Object value) {
        return new SingleFieldMatcher(field, value);
    }

    public static Predicate<Glob> fieldContainsIgnoreCaseAndAccents(final StringField field, final String value) {
        if (Strings.isNullOrEmpty(value)) {
            return ALL;
        }
        final String rawValue = normalize(value);
        return new Predicate<Glob>() {
            public boolean test(Glob item) {
                String actual = item.get(field);
                return actual != null && normalize(actual).contains(rawValue);
            }

            public String toString() {
                return "field " + field.getFullName() + " contains[ignoreCase+Accents] " + value;
            }
        };
    }

    private static String normalize(String value) {
        return Strings.unaccent(value.toLowerCase());
    }

    public static Predicate<Glob> contained(Field field, Object... values) {
        return contained(field, Arrays.asList(values));
    }

    public static Predicate<Glob> contained(Field field, Collection values) {
        if ((values == null) || values.isEmpty()) {
            return NONE;
        }
        if (values.size() == 1) {
            return new SingleFieldMatcher(field, values.iterator().next());
        }
        return new CollectionFieldMatcher(field, values);
    }

    /**
     * Accepts all Globs who are linked to a given Glob.
     */

    public static Predicate<Glob> linkedTo(Glob target, final Link link) {
        if (target == null) {
            return NONE;
        }
        return link.apply(new FieldMappingFunction() {
            Predicate<Glob> matcher = ALL;

            public void process(Field sourceField, Field targetField) {
                matcher = and(matcher, fieldEqualsObject(sourceField, target.getValue(targetField)));
            }
        }).matcher;
    }

    public static Predicate<Glob> linkTargetFieldEquals(final Link link, final Field targetField,
                                                        final Object targetFieldValue, GlobRepository repository) {
        return new Predicate<Glob>() {
            public boolean test(Glob item) {
                final Glob target = repository.findLinkTarget(item, link);
                if (target == null) {
                    return false;
                }
                Object value = target.getValue(targetField);
                return value != null ? value.equals(targetFieldValue) : targetFieldValue == null;
            }

            public String toString() {
                return link + " is linked to glob with " + targetField.getFullName() + " = " + targetFieldValue;
            }
        };
    }

    public static Predicate<Glob> linkedTo(final Link link, final Predicate<Glob> linkedObjectMatcher, GlobRepository repository) {
        return new Predicate<Glob>() {
            public boolean test(Glob item) {
                Glob target = repository.findLinkTarget(item, link);
                return (target != null) && linkedObjectMatcher.test(target);
            }

            public String toString() {
                return link + " is linked to matcher " + linkedObjectMatcher;
            }
        };
    }


    public static Predicate<Glob> isNull(final Field field) {
        return new Predicate<Glob>() {
            public boolean test(Glob glob) {
                return glob.getValue(field) == null;
            }

            public String toString() {
                return field.getFullName() + " is null";
            }
        };
    }

    public static Predicate<Glob> isNotNull(final Field field) {
        return new Predicate<Glob>() {
            public boolean test(Glob item) {
                return item.getValue(field) != null;
            }

            public String toString() {
                return field.getFullName() + " is not null";
            }
        };
    }

    public static Predicate<Glob> isNullOrEmpty(final StringField field) {
        return new Predicate<Glob>() {
            public boolean test(Glob item) {
                return Strings.isNullOrEmpty(item.get(field));
            }

            public String toString() {
                return field.getFullName() + " is null or empty";
            }
        };
    }

    public static Predicate<Glob> isNotEmpty(final StringField field) {
        return new Predicate<Glob>() {
            public boolean test(Glob item) {
                return Strings.isNotEmpty(item.get(field));
            }

            public String toString() {
                return field.getFullName() + " is not empty";
            }
        };
    }

    public static Predicate<Glob> and(List<Predicate<Glob>> matchers) {
        Predicate<Glob>[] array = matchers.toArray(new Predicate[0]);
        return and(array);
    }

    public static Predicate<Glob> and(final Predicate<Glob>... matchers) {
        for (Predicate<Glob> matcher : matchers) {
            if (NONE.equals(matcher)) {
                return NONE;
            }
        }
        List<Predicate<Glob>> significantMatchers = new ArrayList<Predicate<Glob>>();
        for (Predicate<Glob> matcher : matchers) {
            if ((matcher != null) && (matcher != ALL)) {
                significantMatchers.add(matcher);
            }
        }
        if (significantMatchers.isEmpty()) {
            return ALL;
        }
        if (significantMatchers.size() == 1) {
            return significantMatchers.get(0);
        }
        return new Predicate<Glob>() {
            public boolean test(Glob item) {
                for (Predicate<Glob> matcher : matchers) {
                    if (matcher != null && !matcher.test(item)) {
                        return false;
                    }
                }
                return true;
            }

            public String toString() {
                StringBuilder builder = new StringBuilder();
                builder.append("and {\n");
                for (Predicate<Glob> matcher : matchers) {
                    builder.append(matcher).append('\n');
                }
                builder.append("}");
                return builder.toString();
            }
        };
    }

    public static Predicate<Glob> or(final Predicate<Glob>... matchers) {
        List<Predicate<Glob>> significantMatchers = new ArrayList<Predicate<Glob>>();
        for (Predicate<Glob> matcher : matchers) {
            if ((matcher != null) && matcher.equals(ALL)) {
                return ALL;
            }
            if ((matcher != null) && (matcher != NONE)) {
                significantMatchers.add(matcher);
            }
        }
        if (significantMatchers.isEmpty()) {
            return ALL;
        }
        if (significantMatchers.size() == 1) {
            return significantMatchers.get(0);
        }
        return new Predicate<Glob>() {
            public boolean test(Glob glob) {
                for (Predicate<Glob> matcher : matchers) {
                    if (matcher.test(glob)) {
                        return true;
                    }
                }
                return false;
            }

            public String toString() {
                StringBuilder builder = new StringBuilder();
                builder.append("or {\n");
                for (Predicate<Glob> matcher : matchers) {
                    builder.append(matcher).append('\n');
                }
                builder.append("}");
                return builder.toString();
            }
        };
    }

    public static Predicate<Glob> not(final Predicate<Glob> matcher) {
        return new Predicate<Glob>() {
            public boolean test(Glob glob) {
                return !matcher.test(glob);
            }

            public String toString() {
                return "not(" + matcher + ")";
            }
        };
    }

    public static Predicate<Glob> keyEquals(final Key key) {
        return new Predicate<Glob>() {
            public boolean test(Glob item) {
                return key.equals(item.getKey());
            }

            public String toString() {
                return "key equals " + key;
            }
        };
    }

    public static Predicate<Glob> keyIn(final Collection<Key> keys) {
        return new Predicate<Glob>() {
            public boolean test(Glob item) {
                return keys.contains(item.getKey());
            }

            public String toString() {
                return "key in " + keys;
            }
        };
    }


    public static Predicate<Glob> fieldIn(final IntegerField field, final Collection<Integer> values) {
        return new Predicate<Glob>() {
            public boolean test(Glob item) {
                Integer value = item.get(field);
                return value != null && values.contains(value);
            }

            public String toString() {
                return field.getFullName() + " in " + values;
            }
        };
    }

    public static Predicate<Glob> fieldGreaterOrEqual(final IntegerField field, final int value) {
        return new Predicate<Glob>() {
            public boolean test(Glob item) {
                return item.get(field) >= value;
            }

            public String toString() {
                return field.getFullName() + " >= " + value;
            }
        };
    }

    public static Predicate<Glob> fieldStrictlyGreaterThan(final IntegerField field, final int value) {
        return new Predicate<Glob>() {
            public boolean test(Glob item) {
                return item.get(field) > value;
            }

            public String toString() {
                return field.getFullName() + " > " + value;
            }
        };
    }

    public static Predicate<Glob> fieldStrictlyLessThan(final IntegerField field, final int value) {
        return new Predicate<Glob>() {
            public boolean test(Glob item) {
                return item.get(field) < value;
            }

            public String toString() {
                return field.getFullName() + " < " + value;
            }
        };
    }

    public static Predicate<Glob> fieldLessOrEqual(final IntegerField field, final int value) {
        return new Predicate<Glob>() {
            public boolean test(Glob item) {
                return item.get(field) <= value;
            }

            public String toString() {
                return field.getFullName() + " <= " + value;
            }
        };
    }

    public static Predicate<Glob> fieldContained(final Field field, final Collection values) {
        return new Predicate<Glob>() {
            public boolean test(Glob item) {
                return values.contains(item.getValue(field));
            }

            public String toString() {
                return field.getFullName() + " in " + values;
            }
        };

    }


    private static class SingleFieldMatcher implements Predicate<Glob> {
        private Field field;
        private Object value;

        private SingleFieldMatcher(Field field, Object value) {
            this.field = field;
            this.value = value;
        }

        public boolean test(Glob glob) {
            return Utils.equal(value, glob.getValue(field));
        }

        public String toString() {
            return field.getFullName() + " == " + value;
        }
    }

    private static class CollectionFieldMatcher implements Predicate<Glob> {
        private Field field;
        private Collection values;

        private CollectionFieldMatcher(Field field, Collection values) {
            this.field = field;
            this.values = values;
        }

        public boolean test(Glob glob) {
            return values.contains(glob.getValue(field));
        }

        public String toString() {
            return field.getFullName() + " in " + values;
        }
    }

}
