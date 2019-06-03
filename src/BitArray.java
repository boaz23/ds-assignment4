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

    /**
     * Returns the length of the array, the amount of bits
     */
    public int length() {
        return length;
    }

    /**
     * Sets the bit at the given index.
     * @param index The index
     * @param value The new value of the bit
     */
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

    /**
     * Returns whether the bit at the given index is 1 or 0.
     * @param index The bit index
     */
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
        StringBuilder sb = new StringBuilder();
        if (this.length() > 0) {
            appendString(sb, this.bits[0]);
            for (int i = 1; i < this.bits.length; i++) {
                sb.append(" ");
                appendString(sb, this.bits[i]);
            }
        }

        return sb.toString();
    }

    /**
     * Appends a byte to as binary string to a string builder
     * @param sb The string builder to append the binary string to
     * @param item A byte
     */
    private void appendString(StringBuilder sb, byte item) {
        // start from the last bit of the byte
        byte bitFilter = (byte)(1 << (BITS_PER_ITEM - 1));
        for (int i = 0; i < BITS_PER_ITEM; i++) {
            sb.append(this.get(item, bitFilter) ? "1" : "0");

            // Before doing the bit shift, java convert's the byte to an int,
            // but, java byte's conversion to int keeps the value.
            // That means instead of padding it with 0's,
            // it pads it with 1's if the byte's value is negative
            // (which is the case when the bit filter is initialized,
            // as it is initialized to -128).
            // It does this in order to preserve the negative value.
            // That means that 9th bit in the resulting int will be 1
            // and therefore after shifting by 1 (whether signed or unsigned),
            // the 8th bit will be 1. We want the exact opposite, we want the 8th
            // bit to be 0.
            // So we "trick" this by casting to int ourselves and then just zeroing
            // out every bit which is not our byte bits (the first 8 bits, 0xff = 11111111).
            // Only then, we can actually perform the right shift.
            bitFilter = (byte)((((int)bitFilter) & 0xff) >>> 1);
        }
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
         * The byte value used to single out the bit we care about in the byte
         */
        private final byte bitFilter;

        private BitIndices(int index) {
            itemIndex = getItemIndex(index);

             //The index of the bit in the byte itself
            int bitIndex = index % BITS_PER_ITEM;

            // take 00000001 and left shift it by 'bitIndex'
            bitFilter = (byte)(1 << bitIndex);
        }
    }
}
