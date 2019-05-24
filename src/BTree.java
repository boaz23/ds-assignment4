public class BTree {
    private BTreeNode root;

    public BTree(String t) {
        try {
            root = new BTreeNode(Integer.parseInt(t), true);
        }
        catch (NumberFormatException ex) {
            throw new RuntimeException("t is not a number", ex);
        }
    }

    public void insert(String password) {
        root = root.insert(password.toLowerCase());
    }

    @Override
    public String toString() {
        return root.isEmpty() ? "" : root.toString(0);
    }

    public void createFullTree(String filePath) {
        Utils.iterateFileLines(filePath, password -> this.insert(password));
    }
}
