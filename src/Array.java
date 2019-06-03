/**
 * A fixed sized array (does not increase capacity) which supports extra operations
 * such as insertion of an item in the middle of the array
 * @param <T> The item type
 */
public class Array<T> {
    private T[] items;

    /**
     * The amount of items the array takes care of starting at the first item (index 0).
     * Only items at indices 0,...,size-1 are taken care of in various operations
     * such as insert, delete, right shift and left shift.
     */
    private int size;

    public Array(T[] items) {
        if (items == null) {
            throw new RuntimeException("items is null.");
        }

        this.items = items;
        size = 0;
    }

    public int size() {
        return size;
    }

    /**
     * Sets the amount of items to care about.
     * @param size The new size
     */
    public void setSize(int size) {
        if (size < 0) {
            throw new RuntimeException("size must be a non-negative number.");
        }

        this.size = size;
    }

    /**
     * Returns the capacity of the array.
     * The array cannot hold more than this amount of items
     */
    public int capacity() {
        return items.length;
    }

    /**
     * Returns the item at the
     * @param index The index
     */
    public T get(int index) {
        return items[index];
    }

    /**
     * Sets the item at given index to specified value
     * @param index The item index
     * @param item The new value
     */
    public void set(int index, T item) {
        items[index] = item;
    }

    /**
     * Takes items from the other array starting at the given start index
     * until the end of the other array and puts them into this array
     * starting from the end.
     * The other array items are set to null.
     * @param other The other array to take items from
     * @param startIndex The index in the other array to start taking items from
     */
    public void takeItemsFrom(Array<T> other, int startIndex) {
        takeItemsFrom(size(), other, startIndex, other.size() - startIndex);
    }

    /**
     * Takes items from the source array and puts them into this array
     * The source array items are set to null.
     * @param insertionStartIndex The index in this array to start placing items in
     * @param source The source array to take items from
     * @param sourceStartIndex The index in the source array to start taking items from
     * @param count The amount of items to take
     */
    public void takeItemsFrom(int insertionStartIndex, Array<T> source, int sourceStartIndex, int count) {
        for (int i = 0; i < count; i++) {
            set(insertionStartIndex + i, source.get(sourceStartIndex + i));
            source.set(sourceStartIndex + i, null);
        }

        size += count;
        source.size -= count;
    }

    /**
     * Shifts all items of indices in the range [leftIndex, rightIndex] to the right by 1.
     * @param leftIndex The lower index bound (inclusive)
     * @param rightIndex The upper index bound (inclusive)
     */
    public void rightShift(int leftIndex, int rightIndex) {
        for (int i = rightIndex; i >= leftIndex; i--) {
            set(i + 1, get(i));
        }
    }

    /**
     * Shifts all items of indices in the range [leftIndex, rightIndex] to the left by 1
     * @param leftIndex The lower index bound (inclusive)
     * @param rightIndex The upper index bound (inclusive)
     */
    public void leftShift(int leftIndex, int rightIndex) {
        for (int i = leftIndex - 1; i < rightIndex; i++) {
            set(i, get(i + 1));
        }
    }

    /**
     * Inserts an item to array (assuming it has enough space for it) at the insertion index.
     * @param insertIndex The index the item should be inserted into
     * @param item The item to insertAt
     */
    public void insertAt(int insertIndex, T item) {
        rightShift(insertIndex, lastIndex());
        set(insertIndex, item);
        size++;
    }

    /**
     * Removes the item in the index of removal index and then sets null at lastIndex.
     * @param removeIndex The item index to removeAt from the array
     * @return The removed item
     */
    public T removeAt(int removeIndex) {
        T item = get(removeIndex);
        leftShift(removeIndex + 1, lastIndex());
        set(lastIndex(), null);
        size--;
        return item;
    }

    /**
     * @return The first index in the array
     */
    public int firstIndex() {
        return 0;
    }

    /**
     * @return The last index in the array that has an item
     */
    public int lastIndex() {
        return size() - 1;
    }

    /**
     * Returns whether the index has an index on it's right that is taken care of by the array
     * @param i The index
     */
    public boolean hasRight(int i) {
        return i < lastIndex();
    }

    /**
     * Returns whether the index has an index on it's left that is taken care of by the array
     * @param i The index
     */
    public boolean hasLeft(int i) {
        return i > firstIndex();
    }

    /**
     * Gets the first item in the array
     */
    public T getFirst() {
        return get(firstIndex());
    }

    /**
     * Gets the last item in the array
     */
    public T getLast() {
        return get(lastIndex());
    }

    /**
     * Sets the first item of the array
     * @param item The new value
     */
    public void setFirst(T item) {
        set(firstIndex(), item);
    }

    /**
     * Sets the last item of the array
     * @param item The new value
     */
    public void setLast(T item) {
        set(lastIndex(), item);
    }

    /**
     * Inserts a new item to array as the first item
     * @param item The item to insert
     */
    public void insertFirst(T item) {
        insertAt(firstIndex(), item);
    }

    /**
     * Inserts a new item to array as the last item
     * @param item The item to insert
     */
    public void insertLast(T item) {
        insertAt(size(), item);
    }

    /**
     * Removes the first item in the array
     * @return The value of removed item
     */
    public T removeFirst() {
        return removeAt(firstIndex());
    }

    /**
     * Removes the last item in the array
     * @return The value of removed item
     */
    public T removeLast() {
        return removeAt(lastIndex());
    }
}
