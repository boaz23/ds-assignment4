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

    public int firstIndex() {
        return 0;
    }

    public int lastIndex() {
        return size() - 1;
    }

    public boolean hasRight(int i) {
        return i < lastIndex();
    }

    public boolean hasLeft(int i) {
        return i > firstIndex();
    }

    public T getFirst() {
        return get(firstIndex());
    }

    public T getLast() {
        return get(lastIndex());
    }

    public void setFirst(T item) {
        set(firstIndex(), item);
    }

    public void setLast(T item) {
        set(lastIndex(), item);
    }

    public void insertFirst(T item) {
        insertAt(firstIndex(), item);
    }

    public void insertLast(T item) {
        insertAt(size(), item);
    }

    public T removeFirst() {
        return removeAt(firstIndex());
    }

    public T removeLast() {
        return removeAt(lastIndex());
    }
}
