package org.globsframework.core.utils.container.specific;

import org.globsframework.core.model.Glob;
import org.globsframework.core.model.Key;
import org.globsframework.core.utils.Utils;
import org.globsframework.core.utils.container.hash.HashContainer;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class HashMapGlobKeyContainer implements HashContainer<Key, Glob> {
    public static final double LOAD_FACTOR = 0.75;
    private static final int INITAL_SIZE = 8;
    static Values REPLACE = new Values(0);
    private Values values;
    private int size;

    public HashMapGlobKeyContainer(Hash4GlobKeyContainer hash4GlobKeyContainer) {
        this.values = new Values(INITAL_SIZE);
        Iterator<Glob> values = hash4GlobKeyContainer.values();
        while (values.hasNext()) {
            Glob next = values.next();
            put(next.getKey(), next);
        }
    }

    public HashMapGlobKeyContainer(int size) {
        this.values = new Values(roundUpToPowerOf2(size));
    }

    public HashMapGlobKeyContainer(HashMapGlobKeyContainer hashMapGlobKeyContainer) {
        this.values = hashMapGlobKeyContainer.values.duplicate();
        size = hashMapGlobKeyContainer.size;
    }

    private static int roundUpToPowerOf2(int number) {
        int rounded;
        return (rounded = Integer.highestOneBit(number)) != 0
                ? (Integer.bitCount(number) > 1) ? rounded << 1 : rounded
                : 1;
    }

    static int hash(Object k) {
        int h = k.hashCode();

        // This function ensures that hashCodes that differ only by
        // constant multiples at each bit position have a bounded
        // number of collisions (approximately 8 at default load factor).
        h ^= (h >>> 20) ^ (h >>> 12);
        return h ^ (h >>> 7) ^ (h >>> 4);
    }

    /**
     * Returns index for hash code h.
     */
    static int indexFor(int h, int length) {
        // assert Integer.bitCount(length) == 1 : "length must be a non-zero power of 2";
        return h & (length - 1);
    }

    private static Iterator<Glob> iterate(Values values) {
        return new GlobIterator(values);
    }

    public Glob get(Key key) {
        return values.getValue(hash(key), key);
    }

    public HashContainer<Key, Glob> put(Key key, Glob value) {
        Values put = values.put(hash(key), key, value);
        if (put != REPLACE) {
            values = put;
            size++;
        }
        return this;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public Iterator<Glob> values() {
        return iterate(values);
    }

    public HashContainer<Key, Glob> duplicate() {
        return new HashMapGlobKeyContainer(this);
    }

    public TwoElementIterator<Key, Glob> entryIterator() {
        final Iterator<Glob> values = values();
        return new KeyGlobTwoElementIterator(values);
    }

    public Glob remove(Key key) {
        Glob remove = values.remove(hash(key), key);
        if (remove != null) {
            size--;
        }
        return remove;
    }

    public int size() {
        return size;
    }

    public <E extends Functor<Key, Glob>> E forEach(E functor) {
        values.apply(functor);
        return functor;
    }

    public boolean containsKey(Key key) {
        return values.containsKey(key);
    }

    public <E extends FunctorAndRemove<Key, Glob>> E applyAndRemoveIfTrue(E functor) {
        size -= values.applyAndRemoveIfTrue(functor);
        return functor;
    }

    static class Values {
        Glob values[];
        Values next;
        int count;
        int threshold;

        public Values(int size) {
            values = new Glob[size];
            threshold = (int) (size * LOAD_FACTOR);
        }

        public Values(Values org) {
            this.values = Arrays.copyOf(org.values, org.values.length);
            if (org.next != null) {
                this.next = org.next.duplicate();
            }
            count = org.count;
            threshold = org.threshold;
        }

        Values duplicate() {
            return new Values(this);
        }

        public Glob getValue(int hash, Key key) {
            Glob glob = values[indexFor(hash, values.length)];
            if (glob == null) {
                return null;
            }
            if (Utils.equalWithHash(glob.getKey(), key)) {
                return glob;
            }
            if (next != null) {
                return next.getValue(hash, key);
            } else {
                return null;
            }
        }

        public Values put(int hash, Key key, Glob glob) {
            int index = indexFor(hash, values.length);
            Glob currentGlob = values[index];
            if (currentGlob == null) {
                count++;
                if (count >= threshold) {
                    return resizeAndAdd(hash, key, glob);
                } else {
                    values[index] = glob;
                    return this;
                }
            }
            return putValues(hash, key, glob, currentGlob);
        }

        private Values resizeAndAdd(int hash, Key key, Glob glob) {
            Values newSized = resize();
            newSized.put(hash, key, glob);
            return newSized;
        }

        private Values putValues(int hash, Key key, Glob glob, Glob currentGlob) {
            if (key.equals(currentGlob.getKey())) {
                values[indexFor(hash, values.length)] = glob;
                return REPLACE;
            } else {
                if (next == null) {
                    next = new Values(INITAL_SIZE);
                }
                Values put = next.put(hash, key, glob);
                if (put == REPLACE) {
                    return REPLACE;
                }
                if (count >= threshold) {
                    return resizeAndAdd(hash, key, glob);
                }
                next = put;
                count++;
                return this;
            }
        }

        private Values resize() {
            Values newSized = new Values(values.length << 1);
            Glob[] values = newSized.values;
            Iterator<Glob> iterate = iterate(this);
            newSized.count = count;
            while (iterate.hasNext()) {
                Glob next = iterate.next();
                int hash = hash(next.getKey());
                int i = indexFor(hash, values.length);
                if (values[i] == null) {
                    values[i] = next;
                } else {
                    if (newSized.next == null) {
                        newSized.next = new Values(INITAL_SIZE);
                    }
                    newSized.next = newSized.next.put(hash, next.getKey(), next);
                }
            }
            return newSized;
        }

        public <E extends Functor<Key, Glob>> void apply(E functor) {
            for (Glob value : values) {
                if (value != null) {
                    functor.apply(value.getKey(), value);
                }
            }
            if (next != null) {
                next.apply(functor);
            }
        }

        private Glob remove(int indexToMatch, int len) {
            for (Glob value : values) {
                if (value != null && indexFor(hash(value), len) == indexToMatch) {
                    remove(hash(value), value.getKey());
                    return value;
                }
            }
            if (next != null) {
                return next.remove(indexToMatch, len);
            }
            return null;
        }

        public Glob remove(int hash, Key key) {
            int i = indexFor(hash, values.length);
            Glob glob = values[i];
            if (glob == null) {
                return null;
            }
            if (Utils.equalWithHash(glob.getKey(), key)) {
                Glob newValue = null;
                if (next != null) {
                    newValue = next.remove(i, values.length);
                }
                values[i] = newValue;
                count--;
                return glob;
            }
            if (next != null) {
                Glob value = next.remove(hash, key);
                if (value != null) {
                    count--;
                } else {
                    return null;
                }
                return value;
            } else {
                return null;
            }
        }

        public <E extends FunctorAndRemove<Key, Glob>> int applyAndRemoveIfTrue(E functor) {
            int removed = 0;
            for (int i = 0; i < values.length; i++) {
                Glob value = values[i];
                if (value != null) {
                    if (functor.apply(value.getKey(), value)) {
                        if (next != null) {
                            values[i] = next.remove(i, values.length);
                            i--;
                        } else {
                            values[i] = null;
                        }
                        removed++;
                    }
                }
            }
            if (next != null) {
                removed += next.applyAndRemoveIfTrue(functor);
            }
            return removed;
        }

        public boolean containsKey(Key key) {
            for (Glob value : values) {
                if (value != null) {
                    if (value.getKey().equals(key)) {
                        return true;
                    }
                }
            }
            if (next != null) {
                return next.containsKey(key);
            }
            return false;
        }
    }

    private static class GlobIterator implements Iterator<Glob> {
        Values current;
        int nextPos;

        public GlobIterator(Values values) {
            current = values;
            goToNext();
        }

        private void goToNext() {
            while (current != null) {
                for (int i = nextPos; i < current.values.length; i++) {
                    if (current.values[i] != null) {
                        nextPos = i;
                        return;
                    }
                }
                nextPos = 0;
                current = current.next;
            }
            nextPos = -1;
        }

        public boolean hasNext() {
            return nextPos != -1;
        }

        public Glob next() {
            if (nextPos == -1) {
                throw new NoSuchElementException();
            }
            Glob current = this.current.values[nextPos];
            nextPos++;
            goToNext();
            return current;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private static class KeyGlobTwoElementIterator implements TwoElementIterator<Key, Glob> {
        private final Iterator<Glob> values;
        private Glob next;

        public KeyGlobTwoElementIterator(Iterator<Glob> values) {
            this.values = values;
        }

        public boolean next() {
            boolean hasNext = values.hasNext();
            if (hasNext) {
                next = values.next();
            }
            return hasNext;
        }

        public Key getKey() {
            return next.getKey();
        }

        public Glob getValue() {
            return next;
        }
    }
}
