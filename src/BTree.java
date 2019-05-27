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
        rootInsert(password.toLowerCase());
    }

    public NodeIndexPair search(String password) {
        return root.search(password.toLowerCase());
    }

    public void delete(String password) {
        rootDelete(password.toLowerCase());
    }

    /**
     * Inserts the key, should be called on the root only
     * @param password The key to insert
     */
    private void rootInsert(String password) {
        // if the root is full, we need to split it and make a new root
        if (root.isFull()) {
            // make a new root node
            BTreeNode newRoot = root.createRootNode();
            root.root = false;
            newRoot.children.insertFirst(root);

            newRoot.splitChild(0);
            root = newRoot;
        }

        // find the right leaf and insertAt there
        root.insertNonFull(password);
    }


    /**
     * Deletes the key, should be called on the root only
     * @param password The key to delete
     */
    private void rootDelete(String password) {
        if (root.needsKey() & root.children.get(0).needsKey() & root.children.get(1).needsKey()) {
            BTreeNode newRoot = root.merge(0);
            newRoot.root = root.root;
            root = newRoot;
        }

        root.deleteNotMinimumKeys(password);
    }

    @Override
    public String toString() {
        return root.toString();
    }

    public void createFullTree(String filePath) {
        Utils.iterateFileLines(filePath, password -> this.insert(password));
    }

    public String getSearchTime(String filePath) {
        long time = 0;
        try (FileLinesIterator linesIterator = new FileLinesIterator(filePath)) {
            for (String password : linesIterator) {
                long startNanoTime = System.nanoTime();
                this.search(password);
                long endNanoTime = System.nanoTime();
                time += endNanoTime - startNanoTime;
            }
        }

        return Utils.formatMillisecondsDiff(time);
    }

    public void deleteKeysFromTree(String filePath) {
        Utils.iterateFileLines(filePath, password -> this.delete(password));
    }
}
