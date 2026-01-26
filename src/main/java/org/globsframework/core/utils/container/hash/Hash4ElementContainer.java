package org.globsframework.core.utils.container.hash;

import org.globsframework.core.utils.Utils;

import java.util.Collections;
import java.util.Iterator;

public class Hash4ElementContainer<T, D> implements HashContainer<T, D> {
    private T key1;
    private T key2;
    private T key3;
    private T key4;
    private D value1;
    private D value2;
    private D value3;
    private D value4;

    public Hash4ElementContainer() {
        key1 = (T) HashOneElementContainer.NULL;
        key2 = (T) HashOneElementContainer.NULL;
        key3 = (T) HashOneElementContainer.NULL;
        key4 = (T) HashOneElementContainer.NULL;
    }

    public Hash4ElementContainer(HashTwoElementContainer<T, D> elementContainer, T key, D value) {
        key1 = elementContainer.getKey1();
        value1 = elementContainer.getValue1();
        key2 = elementContainer.getKey2();
        value2 = elementContainer.getValue2();
        this.key3 = key;
        this.value3 = value;
        this.key4 = (T) HashOneElementContainer.NULL;
    }

    public Hash4ElementContainer(Hash4ElementContainer<T, D> tdHash4ElementContainer) {
        key1 = tdHash4ElementContainer.key1;
        value1 = tdHash4ElementContainer.value1;
        key2 = tdHash4ElementContainer.key2;
        value2 = tdHash4ElementContainer.value2;
        key3 = tdHash4ElementContainer.key3;
        value3 = tdHash4ElementContainer.value3;
        key4 = tdHash4ElementContainer.key4;
        value4 = tdHash4ElementContainer.value4;
    }

    public D get(T key) {
        return Utils.equalWithHash(key, this.key1) ? value1 : Utils.equalWithHash(key, this.key2) ? value2 :
                Utils.equalWithHash(key, this.key3) ? value3 : Utils.equalWithHash(key, this.key4) ? value4 : null;
    }

    public HashContainer<T, D> put(T key, D value) {
        if (key1 == HashOneElementContainer.NULL || Utils.equalWithHash(key, this.key1)) {
            key1 = key;
            value1 = value;
            return this;
        } else if (key2 == HashOneElementContainer.NULL || Utils.equalWithHash(key, this.key2)) {
            this.key2 = key;
            this.value2 = value;
            return this;
        } else if (key3 == HashOneElementContainer.NULL || Utils.equalWithHash(key, this.key3)) {
            key3 = key;
            value3 = value;
            return this;
        } else if (key4 == HashOneElementContainer.NULL || Utils.equalWithHash(key, this.key4)) {
            this.key4 = key;
            this.value4 = value;
            return this;
        } else {
            HashMapContainer<T, D> container = new HashMapContainer<T, D>(this.key1, this.value1);
            container.put(key2, value2);
            container.put(key3, value3);
            container.put(key4, value4);
            container.put(key, value);
            return container;
        }
    }

    public boolean isEmpty() {
        return key1 == HashOneElementContainer.NULL && key2 == HashOneElementContainer.NULL && key3 == HashOneElementContainer.NULL && key4 == HashOneElementContainer.NULL;
    }

    public Iterator<D> values() {
        if (isEmpty()) {
            return Collections.emptyIterator();
        }
        return new Iterator<D>() {
            int i = 0;
            D value;

            public boolean hasNext() {
                switch (i) {
                    case 0:
                        i = 0;
                        if (key1 != HashOneElementContainer.NULL) {
                            value = value1;
                            break;
                        }
                    case 1:
                        i = 1;
                        if (key2 != HashOneElementContainer.NULL) {
                            value = value2;
                            break;
                        }
                    case 2:
                        i = 2;
                        if (key3 != HashOneElementContainer.NULL) {
                            value = value3;
                            break;
                        }
                    case 3:
                        i = 3;
                        if (key4 != HashOneElementContainer.NULL) {
                            value = value4;
                            break;
                        }
                    default:
                        i = 5;
                        break;

                }
                return i < 4;
            }

            public D next() {
                i++;
                return value;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public HashContainer<T, D> duplicate() {
        return new Hash4ElementContainer<>(this);
    }

    public TwoElementIterator<T, D> entryIterator() {
        return new TwoElementIterator<T, D>() {
            int i = 0;
            T key;
            D value;

            public boolean next() {
                switch (i) {
                    case 0:
                        i++;
                        if (key1 != HashOneElementContainer.NULL) {
                            key = key1;
                            value = value1;
                            return true;
                        }
                    case 1:
                        i++;
                        if (key2 != HashOneElementContainer.NULL) {
                            key = key2;
                            value = value2;
                            return true;
                        }
                    case 2:
                        i++;
                        if (key3 != HashOneElementContainer.NULL) {
                            key = key3;
                            value = value3;
                            return true;
                        }
                    case 3:
                        i++;
                        if (key4 != HashOneElementContainer.NULL) {
                            key = key4;
                            value = value4;
                            return true;
                        }
                }
                return false;
            }

            public T getKey() {
                return key;
            }

            public D getValue() {
                return value;
            }
        };
    }

    public D remove(T key) {
        if (Utils.equalWithHash(key, this.key1)) {
            D oldValue = value1;
            key1 = (T) HashOneElementContainer.NULL;
            value1 = null;
            return oldValue;
        }
        if (Utils.equalWithHash(key, this.key2)) {
            D oldValue = value2;
            key2 = (T) HashOneElementContainer.NULL;
            value2 = null;
            return oldValue;
        }
        if (Utils.equalWithHash(key, this.key3)) {
            D oldValue = value3;
            key3 = (T) HashOneElementContainer.NULL;
            value3 = null;
            return oldValue;
        }
        if (Utils.equalWithHash(key, this.key4)) {
            D oldValue = value4;
            key4 = (T) HashOneElementContainer.NULL;
            value4 = null;
            return oldValue;
        }
        return null;
    }

    public int size() {
        int count = 0;
        if (this.key1 != HashOneElementContainer.NULL) {
            count++;
        }
        if (this.key2 != HashOneElementContainer.NULL) {
            count++;
        }
        if (this.key3 != HashOneElementContainer.NULL) {
            count++;
        }
        if (this.key4 != HashOneElementContainer.NULL) {
            count++;
        }
        return count;
    }

    private boolean isValue1Set() {
        return key1 != HashOneElementContainer.NULL;
    }

    private boolean isValue2Set() {
        return key2 != HashOneElementContainer.NULL;
    }

    private boolean isValue3Set() {
        return key3 != HashOneElementContainer.NULL;
    }

    private boolean isValue4Set() {
        return key4 != HashOneElementContainer.NULL;
    }

    public <E extends Functor<T, D>> E forEach(E functor) {
        if (isValue1Set()) {
            functor.apply(key1, value1);
        }
        if (isValue2Set()) {
            functor.apply(key2, value2);
        }
        if (isValue3Set()) {
            functor.apply(key3, value3);
        }
        if (isValue4Set()) {
            functor.apply(key4, value4);
        }
        return functor;
    }

    public boolean containsKey(T key) {
        return Utils.equalWithHash(key, this.key1)
                || Utils.equalWithHash(key, this.key2)
                || Utils.equalWithHash(key, this.key3)
                || Utils.equalWithHash(key, this.key4);
    }

    public <E extends FunctorAndRemove<T, D>> E applyAndRemoveIfTrue(E functor) {
        if (isValue1Set()) {
            if (functor.apply(key1, value1)) {
                key1 = (T) HashOneElementContainer.NULL;
            }
        }
        if (isValue2Set()) {
            if (functor.apply(key2, value2)) {
                key2 = (T) HashOneElementContainer.NULL;
            }
        }
        if (isValue3Set()) {
            if (functor.apply(key3, value3)) {
                key3 = (T) HashOneElementContainer.NULL;
            }
        }
        if (isValue4Set()) {
            if (functor.apply(key4, value4)) {
                key4 = (T) HashOneElementContainer.NULL;
            }
        }
        return functor;
    }
}
