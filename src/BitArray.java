public class BitArray {
    private static final int ITEM_SIZE = 1;
    private static final int BITS_PER_BYTE = 8;
    private static final int BITS_PER_ITEM = BITS_PER_BYTE * ITEM_SIZE;

    private static final byte FALSE = 0;

    /**
     * We use all of the 8 bits of each byte
     * (except for the last byte which we might less)
     */
    private byte[] bits;

    private int length;

    public BitArray(int length) {
        if (length < 0) {
            throw new RuntimeException("The length must be a non-negative number.");
        }

        // calculate the required amount of bytes needed
        int itemsLength = length > 0 ? (getItemIndex(length - 1) + 1) : 0;
        this.bits = new byte[itemsLength];

        this.length = length;
    }

    public int length() {
        return length;
    }

    public void set(int index, boolean value) {
        validateIndex(index);

        int itemValue;
        BitIndices bitIndices = new BitIndices(index);
        if (value) {
            // we need to turn the bit on
            itemValue = this.bits[bitIndices.itemIndex] | bitIndices.bitFilter;
        }
        else {
            // we need to turn the bit off
            itemValue = this.bits[bitIndices.itemIndex] & ~bitIndices.bitFilter;
        }

        this.bits[bitIndices.itemIndex] = (byte)itemValue;
    }

    public boolean get(int index) {
        validateIndex(index);

        BitIndices bitIndices = new BitIndices(index);
        return this.get(this.bits[bitIndices.itemIndex], bitIndices.bitFilter);
    }

    private void validateIndex(int index) {
        if (index < 0 | index >= this.length()) {
            throw new RuntimeException("index out of range.");
        }
    }

    /**
     * Returns the index of the byte in the bits array of the bit in index 'index'
     * @param index The index of the bit
     */
    private static int getItemIndex(int index) {
        return index / BITS_PER_ITEM;
    }

    /**
     * Returns whether two bytes have a shared bit which is on
     * @param item The first byte
     * @param bitFilter The second byte
     */
    private boolean get(byte item, byte bitFilter) {
        return (item & bitFilter) != FALSE;
    }

    @Override
    public String toString() {
        String s = "";
        if (this.length() > 0) {
            s += getString(this.bits[0]);
            for (int i = 1; i < this.bits.length; i++) {
                s += " ";
                s += getString(this.bits[i]);
            }
        }

        return s;
    }

    /**
     * Converts a byte to a binary string
     * @param item A byte
     * @return A string representing the byte in binary base
     */
    private String getString(byte item) {
        String s = "";

        // start from the last bit of the byte
        byte bitFilter = (byte)(1 << (BITS_PER_ITEM - 1));
        for (int i = 0; i < BITS_PER_ITEM; i++) {
            s += this.get(item, bitFilter) ? "1" : "0";

            // Java byte's conversion to int keeps the value.
            // that means instead of padding it with 0's,
            // it pads it with 1's if the byte's value is negative.
            // That means that 9th bit in the resulting int will be 1
            // and therefore after shifting by 1 (whether signed or unsigned),
            // the 8th bit will be 1. We want the exact opposite, we want the 8th
            // bit to be 0.
            bitFilter = (byte)((((int)bitFilter) & 0xff) >>> 1);
        }

        return s;
    }

    /**
     * A struct to hold values important to the data structure's functions
     */
    private static class BitIndices {
        /**
         * The index of the byte in the 'bits' array
         */
        private final int itemIndex;

        /**
         * The index of the bit in the byte itself
         */
        private final int bitIndex;

        /**
         * The byte value used to single out the bit we care about in the byte
         */
        private final byte bitFilter;

        private BitIndices(int index) {
            itemIndex = getItemIndex(index);
            bitIndex = index % BITS_PER_ITEM;

            // take 00000001 and left shift it by 'bitIndex'
            bitFilter = (byte)(1 << bitIndex);
        }
    }
}
