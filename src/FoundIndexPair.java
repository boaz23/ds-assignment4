public class FoundIndexPair {
    private final boolean found;
    private final int index;

    public FoundIndexPair(boolean found, int index) {
        this.found = found;
        this.index = index;
    }

    public boolean found() {
        return found;
    }

    public int index() {
        return index;
    }
}
