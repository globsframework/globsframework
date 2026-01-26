package org.globsframework.core.utils.container.specific;

import org.globsframework.core.model.Glob;
import org.globsframework.core.model.Key;
import org.globsframework.core.model.format.GlobPrinter;
import org.globsframework.core.utils.Utils;
import org.globsframework.core.utils.container.hash.HashContainer;

import java.util.Collections;
import java.util.Iterator;

public class HashOneGlobKeyContainer implements HashContainer<Key, Glob> {
    private Glob value;

    public HashOneGlobKeyContainer() {
    }

    public HashOneGlobKeyContainer(Glob value) {
        this.value = value;
    }

    public HashOneGlobKeyContainer(Key key, Glob value) {
        checkEqual(key, value);
        this.value = value;
    }

    static void checkEqual(Key key, Glob value) {
        if (value != null && !key.equals(value.getKey())) {
            throw new RuntimeException("Bug inserting a glob with a different key " + GlobPrinter.toString(key.asFieldValues()) + " " + GlobPrinter.toString(value));
        }
    }

    public Glob get(Key key) {
        return value != null && Utils.equal(key, this.value.getKey()) ? value : null;
    }

    public HashContainer<Key, Glob> put(Key key, Glob value) {
        checkEqual(key, value);
        if (this.value == null || Utils.equal(key, this.value.getKey())) {
            this.value = value;
            return this;
        } else {
            return new HashTwoGlobKeyContainer(this, value);
        }
    }

    public boolean isEmpty() {
        return value == null;
    }

    public Iterator<Glob> values() {
        if (value == null) {
            return Collections.emptyIterator();
        }
        return new OneStepIterator();
    }

    public HashContainer<Key, Glob> duplicate() {
        return new HashOneGlobKeyContainer(value);
    }

    public TwoElementIterator<Key, Glob> entryIterator() {
        return new TwoElementIterator<Key, Glob>() {
            boolean isNext = value != null;

            public boolean next() {
                boolean isNext = this.isNext;
                this.isNext = false;
                return isNext;
            }

            public Key getKey() {
                return value.getKey();
            }

            public Glob getValue() {
                return value;
            }
        };
    }

    public Glob remove(Key key) {
        if (this.value != null && Utils.equalWithHash(key, this.value.getKey())) {
            Glob oldValue = value;
            value = null;
            return oldValue;
        }
        return null;
    }

    public int size() {
        return value == null ? 0 : 1;
    }

    public <E extends Functor<Key, Glob>> E forEach(E functor) {
        if (value != null) {
            functor.apply(value.getKey(), value);
        }
        return functor;
    }

    public boolean containsKey(Key key) {
        return value != null && Utils.equalWithHash(value.getKey(), key);
    }

    public <E extends FunctorAndRemove<Key, Glob>> E applyAndRemoveIfTrue(E functor) {
        if (value != null) {
            if (functor.apply(value.getKey(), value)) {
                value = null;
            }
        }
        return functor;
    }

    public Key getKey() {
        return value.getKey();
    }

    public Glob getValue() {
        return value;
    }

    private class OneStepIterator implements Iterator<Glob> {
        boolean hasNext = true;

        public boolean hasNext() {
            if (hasNext) {
                return true;
            }
            return false;
        }

        public Glob next() {
            Glob value1 = value;
            hasNext = false;
            return value1;
        }

        public void remove() {
            value = null;
        }
    }

}
