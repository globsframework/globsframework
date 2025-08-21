package org.globsframework.core.model.utils;

import org.globsframework.core.utils.NanoChrono;
import org.junit.jupiter.api.Test;

public class LongBitOperationTest {

    @Test
    public void name() {
//        BitSet bitSet = new BitSet();
//        LongMultiBitOperationWithIf bitSet = new LongMultiBitOperationWithIf();
        LongBitOperation bitSet = new LongBitOperation();
        NanoChrono nanoChrono = NanoChrono.start();
        for (int i = 0; i < 10000000; i++) {
            bitSet.set(i % 32);
            if (!bitSet.isSet(i % 32)) {
                throw new RuntimeException("BUG");
            }
        }
        double elapsedTimeInMS = nanoChrono.getElapsedTimeInMS();
        System.out.println("LongBitOperationTest.name " + elapsedTimeInMS + "ms");

        for (int i = 0; i < 10000000; i++) {
            if (!bitSet.isSet(i % 32)) {
                throw new RuntimeException("BUG");
            }
        }
    }

    public static class LongBitOperation {
        private final static int ADDRESS_BITS_PER_WORD = 5;
        private final static int BITS_PER_WORD = 1 << ADDRESS_BITS_PER_WORD;
        private final static int BIT_INDEX_MASK = BITS_PER_WORD - 1;

        /* Used to shift left or right for a partial word mask */
        private static final long WORD_MASK = 0xffffffffffffffffL;

        int val1;

        void set(int index) {
            val1 |= 1 << index;
        }

        void clean(int index) {
            val1 &= ~(1 << index);
        }

        boolean isSet(int index) {
            return (val1 & (1 << index)) != 0;
        }
    }

    public static class LongMultiBitOperationWithIf {
        private final static int ADDRESS_BITS_PER_WORD = 6;
        private final static int BITS_PER_WORD = 1 << ADDRESS_BITS_PER_WORD;
        private final static int BIT_INDEX_MASK = BITS_PER_WORD - 1;

        /* Used to shift left or right for a partial word mask */
        private static final long WORD_MASK = 0xffffffffffffffffL;

        long val1;
        long val2;

        void set(int index) {
            if (index < 64) {
                val1 |= 1L << index;
            } else {
                val2 |= 1L << index;
            }
            /*
            switch (wordIndex(index)) {
                case 0:
                    val1 |= 1L << index;
                    break;
                case 1:
                    break;
                default:
                    throw new RuntimeException("index " + index + " out of bound ");
            }*/
        }

        void clean(int index) {
            if (index < 64) {
                val1 &= ~(1L << index);
            } else {
                val2 &= ~(1L << index);
            }
        }

        boolean isSet(int index) {
            if (index < 64) {
                return (val1 & (1L << index)) != 0;
            } else {
                return (val2 & (1L << index)) != 0;
            }
        }

        private static int wordIndex(int bitIndex) {
            return bitIndex >> ADDRESS_BITS_PER_WORD;
        }
    }

    public static class LongMultiBitOperationWithCase {
        private final static int ADDRESS_BITS_PER_WORD = 6;
        private final static int BITS_PER_WORD = 1 << ADDRESS_BITS_PER_WORD;
        private final static int BIT_INDEX_MASK = BITS_PER_WORD - 1;

        /* Used to shift left or right for a partial word mask */
        private static final long WORD_MASK = 0xffffffffffffffffL;

        long val1;
        long val2;

        void set(int index) {
            switch (wordIndex(index)) {
                case 0:
                    val1 |= 1L << index;
                    break;
                case 1:
                    val2 |= 1L << index;
                    break;
                default:
                    throw new RuntimeException("index " + index + " out of bound ");
            }
        }

        void clean(int index) {
            switch (wordIndex(index)) {
                case 0:
                    val1 &= ~(1L << index);
                    break;
                case 1:
                    val2 &= ~(1L << index);
                    break;
                default:
                    throw new RuntimeException("index " + index + " out of bound ");
            }
        }

        boolean isSet(int index) {
            switch (wordIndex(index)) {
                case 0:
                    return (val1 & (1L << index)) != 0;
                case 1:
                    return (val2 & (1L << index)) != 0;
                default:
                    throw new RuntimeException("index " + index + " out of bound ");
            }
        }

        private static int wordIndex(int bitIndex) {
            return bitIndex >> ADDRESS_BITS_PER_WORD;
        }
    }


}
