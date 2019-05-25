public class NodeIndexPair {
    private final BTreeNode node;
    private final int index;

    public NodeIndexPair(BTreeNode node, int index) {
        this.node = node;
        this.index = index;
    }

    public BTreeNode node() {
        return node;
    }

    public int index() {
        return index;
    }
}
