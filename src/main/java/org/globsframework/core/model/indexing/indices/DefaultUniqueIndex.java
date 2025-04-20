package org.globsframework.core.model.indexing.indices;

import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.format.GlobPrinter;
import org.globsframework.core.model.indexing.IndexedTable;

import java.util.*;

public class DefaultUniqueIndex implements IndexedTable {
    private Field field;
    private Map<Object, Glob> index = new HashMap<Object, Glob>();

    public DefaultUniqueIndex(Field field) {
        this.field = field;
    }

    public boolean remove(Field field, Object value, Glob glob) {
        if (index.get(value) == glob) {
            index.remove(value);
        }
        return false;
    }

    public void add(Field field, Object newValue, Object oldValue, Glob glob) {
        Glob oldGlob = index.remove(oldValue);
        if (oldGlob != null && oldGlob != glob) {
            index.put(oldValue, oldGlob);
        }
        index.put(newValue, glob);
    }

    public void add(Glob glob) {
        Glob put = index.put(glob.getValue(field), glob);
        if (put != null) {
            index.put(glob.getValue(field), put);
            throw new RuntimeException("Should be an unique index\n" +
                    "- value: " + glob.getValue(field) + "\n" +
                    "- field: " + field.getName() + "\n" +
                    "- type: " + field.getGlobType() + "\n" +
                    "- new: \n " +
                    GlobPrinter.toString(glob) + "\n" +
                    "- old: \n " +
                    GlobPrinter.toString(put));
        }
    }

    public List<Glob> findByIndex(Object value) {
        Glob glob = index.get(value);
        if (glob == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Collections.singletonList(glob));
    }

    public boolean remove(Glob glob) {
        Object value = glob.getValue(field);
        if (index.get(value) == glob) {
            index.remove(value);
        }
        return false;
    }

    public void removeAll() {
        index.clear();
    }
}
