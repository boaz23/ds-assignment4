public class NodeIndexPair {
    private final BTreeNode node;
    private final int index;

    public NodeIndexPair(BTreeNode node, int index) {
        this.node = node;
        this.index = index;
    }

    public BTreeNode getNode() {
        return node;
    }

    public int getIndex() {
        return index;
    }
}
