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
        root = root.rootInsert(password.toLowerCase());
    }

    public NodeIndexPair search(String password) {
        return root.search(password.toLowerCase());
    }

    public void delete(String password) {
        this.root = root.rootDelete(password.toLowerCase());
    }

    @Override
    public String toString() {
        return root.toString();
    }

    public void createFullTree(String filePath) {
        Utils.iterateFileLines(filePath, password -> this.insert(password));
    }

    public String getSearchTime(String filePath) {
        long startNanoTime = System.nanoTime();
        Utils.iterateFileLines(filePath, password -> this.search(password));
        long endNanoTime = System.nanoTime();
        return Utils.formatMillisecondsDiff(startNanoTime, endNanoTime);
    }

    public void deleteKeysFromTree(String filePath) {
        Utils.iterateFileLines(filePath, password -> this.delete(password));
    }
}
