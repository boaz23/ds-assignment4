public class Array<T> {
    private T[] items;
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

    public void setSize(int size) {
        if (size < 0) {
            throw new RuntimeException("size must be a non-negative number.");
        }

        this.size = size;
    }

    public int capacity() {
        return items.length;
    }

    public T get(int index) {
        return items[index];
    }

    public void set(int index, T item) {
        items[index] = item;
    }

    /**
     * Takes items from the source array and puts them into this array
     * (the source array items are set to null)
     * @param startIndex The index in this array to start placing items in
     * @param source The source array to take items from
     * @param sourceStartIndex The index in the source array to start taking items from
     * @param count The amount of items to take
     */
    public void takeItemsFrom(int startIndex, Array<T> source, int sourceStartIndex, int count) {
        for (int i = 0; i < count; i++) {
            set(startIndex + i, source.get(sourceStartIndex + i));
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
        rightShift(insertIndex, size() - 1);
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
        leftShift(removeIndex + 1, size() - 1);
        set(size() - 1, null);
        size--;
        return item;
    }

    public void insertFirst(T item) {
        insertAt(0, item);
    }

    public void insertLast(T item) {
        insertAt(size(), item);
    }

    public T removeFirst() {
        return removeAt(0);
    }

    public T removeLast() {
        return removeAt(size() - 1);
    }
}
