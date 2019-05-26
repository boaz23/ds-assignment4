public class BTreeNode {
    Array<String> keys;
    Array<BTreeNode> children;
    int t;
    int n;
    boolean leaf;
    boolean root;

    BTreeNode(int t, boolean root) {
        this.t = t;
        leaf = true;
        this.root = root;
        n = 0;
        keys = new Array<>(new String[2*t - 1]);
        children = new Array<>(new BTreeNode[keys.capacity() + 1]);
    }

    BTreeNode(int t) {
        this(t,false);
    }

    private int minKeys() {
        return root ? 1 : t - 1;
    }
    private int maxKeys() {
        return keys.capacity();
    }

    public int size() {
        return n;
    }

    public boolean isEmpty() {
        return size() == 0;
    }
    public boolean isFull() {
        return size() == maxKeys();
    }
    public boolean needsKey() {
        return size() == minKeys();
    }

    public NodeIndexPair search(String password) {
        FoundIndexPair searchResult = localSearch(password);
        if (searchResult.found()) {
            return new NodeIndexPair(this, searchResult.index());
        }
        // we haven't found, nowhere else to look for the key
        else if (leaf) {
            return null;
        }
        // we haven't found, look for the key in the child
        else {
            return children.get(searchResult.index()).search(password);
        }
    }

    public NodeIndexPair findSuccessor(int i) {
        NodeIndexPair nodeIndexPair;
        if (leaf) {
            if (i < size() - 1) {
                nodeIndexPair = new NodeIndexPair(this, i + 1);
            }
            else {
                nodeIndexPair = new NodeIndexPair(null, i);
            }
        }
        else {
            nodeIndexPair = children.get(i + 1).findMinimum();
        }

        return nodeIndexPair;
    }

    public NodeIndexPair findPredecessor(int i) {
        NodeIndexPair nodeIndexPair;
        if (leaf) {
            if (i > 0) {
                nodeIndexPair = new NodeIndexPair(this, i - 1);
            }
            else {
                nodeIndexPair = new NodeIndexPair(null, i);
            }
        }
        else {
            nodeIndexPair = children.get(i).findMaximum();
        }

        return nodeIndexPair;
    }

    public NodeIndexPair findMinimum() {
        NodeIndexPair nodeIndexPair;
        if (isEmpty()) {
            nodeIndexPair = new NodeIndexPair(null, -1);
        }
        else {
            BTreeNode node = this;
            while (!node.leaf) {
                node = node.children.get(0);
            }

            nodeIndexPair = new NodeIndexPair(node, 0);
        }

        return nodeIndexPair;
    }

    public NodeIndexPair findMaximum() {
        NodeIndexPair nodeIndexPair;
        if (isEmpty()) {
            nodeIndexPair = new NodeIndexPair(null, -1);
        }
        else {
            BTreeNode node = this;
            while (!node.leaf) {
                node = node.children.get(node.size());
            }

            nodeIndexPair = new NodeIndexPair(node, node.size() - 1);
        }

        return nodeIndexPair;
    }

    @Override
    public String toString() {
        return isEmpty() ? "" : toString(0);
    }

    /**
     * Search only this node for the given key (without diving down to the children)
     * @param password The key to look for
     * @return An object which holds whether the key was found in this node and
     * the the first index of from the left which is bigger than the given key
     */
    private FoundIndexPair localSearch(String password) {
        int i = 0;
        int compareResult = 0;

        // find the first index such that the key is smaller than the key in that index
        while (i < size() && (compareResult = compareWithKetAt(password, i)) > 0)
            i++;

        // if we're still in bounds of this node the last compare
        // result is 0, then we found it
        boolean found = i < size() & compareResult == 0;
        return new FoundIndexPair(found, i);
    }

    BTreeNode createRootNode() {
        BTreeNode newRoot = new BTreeNode(t, root);
        newRoot.leaf = false;
        newRoot.n = 0;
        return newRoot;
    }

    void splitChild(int i) {
        BTreeNode leftChild = children.get(i);
        BTreeNode newRightChild = leftChild.createNewSplitNode();

        // take the keys t+1,...,2t-1 from the child we're splitting
        // and put them as the keys 1,...,t respectively in the new node.
        newRightChild.takeKeysFrom(leftChild, t,0, t - 1);
        if (!leftChild.leaf) {
            // take the children t+1,...,2t from the child we're splitting
            // and put them as the children 1,...,t in the new node.
            newRightChild.takeChildrenFrom(leftChild, t,0, t);
        }

        keys.insertAt(i, leftChild.keys.get(t - 1));
        children.insertAt(i + 1, newRightChild);
        leftChild.keys.set(t - 1, null);

        n++;
        leftChild.n = leftChild.minKeys();
    }

    void insertNonFull(String password) {
        int i;
        if (leaf) {
            // look for the right place for the new key and make room for it
            // while we go
            i = rightShiftKeysBiggerThan(password);
            keys.set(i + 1, password);
            n++;
        } // if
        else {
            // find the child which the key should go into
            i = localSearch(password).index();

            // we need to split the child it is full
            if (children.get(i).isFull()) {
                splitChild(i);
                if (compareWithKetAt(password, i) > 0) {
                    i++;
                }
            }

            children.get(i).insertNonFull(password);
        }
    }

    /**
     * Look for the first key from the right which is smaller than the specified key
     * and right shift all the keys when doing it
     * @param password The key
     */
    private int rightShiftKeysBiggerThan(String password) {
        int i;
        for (i = size() - 1; i >= 0 && compareWithKetAt(password, i) < 0; i--) {
            keys.set(i + 1, keys.get(i));
        }

        return i;
    }

    private BTreeNode createNewSplitNode() {
        BTreeNode splitNode = new BTreeNode(t);
        splitNode.leaf = leaf;
        splitNode.n = splitNode.minKeys();
        return splitNode;
    }

    private void takeChildrenFrom(BTreeNode from, int fromStartIndex, int thisStartIndex, int count) {
        children.takeItemsFrom(thisStartIndex, from.children, fromStartIndex, count);
    }

    private void takeKeysFrom(BTreeNode from, int fromStartIndex, int thisStartIndex, int count) {
        keys.takeItemsFrom(thisStartIndex, from.keys, fromStartIndex, count);
    }

    void deleteNotMinimumKeys(String password) {
        FoundIndexPair searchResult = localSearch(password);
        int i = searchResult.index();
        if (searchResult.found()) {
            deleteNotMinimumKeysInThis(password, i);
        }
        // password < keys[i] | i == n
        // if it's a leaf, then the key is not in the tree, so nothing to do
        else if (!leaf) {
            deleteNotMinimumKeysInChild(password, i);
        }
    }

    private void deleteNotMinimumKeysInThis(String password, int i) {
        BTreeNode leftChild = children.get(i);
        BTreeNode rightChild = children.get(i + 1);

        // case 1
        if (leaf) {
            deleteFromLeaf(i);
        }
        // case 2
        else if (!leftChild.needsKey()) {
            replaceWithPredecessorAndDeleteItRecursively(i);
        }
        // case 3
        else if (!rightChild.needsKey()) {
            replaceWithSuccessorAndDeleteItRecursively(i);
        }
        // merge with right child and delete the key recursively
        else {
            leftChild = merge(i);
            leftChild.deleteNotMinimumKeys(password);
        }
    }

    private void deleteNotMinimumKeysInChild(String password, int i) {
        BTreeNode child = children.get(i);
        if (child.needsKey()) {
            // shift with left sibling
            if (i > 0 && !children.get(i - 1).needsKey()) {
                shiftRight(i);
            }
            // shift with right sibling
            else if (i < size() && !children.get(i + 1).needsKey()) {
                shiftLeft(i);
            }
            // merge with left sibling
            else if (i > 0) {
                child = merge(i - 1);
            }
            // merge with right sibling
            else {
                child = merge(i);
            }
        }

        child.deleteNotMinimumKeys(password);
    }

    private void deleteFromLeaf(int i) {
        keys.removeAt(i);
        n--;
    }

    private void replaceWithPredecessorAndDeleteItRecursively(int i) {
        NodeIndexPair nodeIndexPair = findPredecessor(i);
        String predecessor = nodeIndexPair.node().keys.get(nodeIndexPair.index());
        children.get(i).deleteNotMinimumKeys(predecessor);
        keys.set(i, predecessor);
    }

    private void replaceWithSuccessorAndDeleteItRecursively(int i) {
        NodeIndexPair nodeIndexPair = findSuccessor(i);
        String successor = nodeIndexPair.node().keys.get(nodeIndexPair.index());
        children.get(i + 1).deleteNotMinimumKeys(successor);
        keys.set(i, successor);
    }

    BTreeNode merge(int i) {
        BTreeNode leftChild = children.get(i);

        leftChild.keys.set(t - 1, keys.removeAt(i));
        BTreeNode rightChild = children.removeAt(i + 1);
        leftChild.takeKeysFrom(rightChild, 0, t, t - 1);
        if (!leftChild.leaf) {
            leftChild.takeChildrenFrom(rightChild, 0, t, t);
        }

        n--;
        leftChild.n = leftChild.maxKeys();
        return leftChild;
    }

    private void shiftRight(int i) {
        BTreeNode child = children.get(i);
        BTreeNode leftSibling = children.get(i - 1);

        child.keys.insertAt(0, keys.get(i - 1));
        keys.set(i - 1, leftSibling.keys.removeLast());
        if (!child.leaf) {
            BTreeNode transferedChild = leftSibling.children.removeLast();
            child.children.insertAt(0, transferedChild);
        }

        child.n++;
        leftSibling.n--;
    }

    private void shiftLeft(int i) {
        BTreeNode child = children.get(i);
        BTreeNode rightSibling = children.get(i + 1);

        child.keys.insertLast(keys.get(i));
        keys.set(i, rightSibling.keys.removeAt(0));
        if (!child.leaf) {
            BTreeNode transferedChild = rightSibling.children.removeAt(0);
            child.children.insertLast(transferedChild);
        }

        child.n++;
        rightSibling.n--;
    }

    private int compareWithKetAt(String password, int i) {
        return compare(password, keys.get(i));
    }

    // if password1 > password2 we return positive number
    // if password1 = password2 we return 0
    // if password1 < password2 we return negative number
    private int compare(String password1 , String password2) {
        return password1.compareTo(password2);
    }

    private String toString(int depth) {
        String s = "";

        if (leaf) {
            s += keyString(0, depth);
            for (int i = 1; i < size(); i++) {
                s += "," + keyString(i, depth);
            } // for
        }
        else {
            s += childAndKeyString(0, depth);
            for (int i = 1; i < size(); i++) {
                s += "," + childAndKeyString(i, depth);
            } // for
            s += "," + childString(size(), depth);
        }

        return s;
    }

    private String keyString(int i, int depth) {
        return keys.get(i) + "_"  + depth;
    }

    private String childString(int i, int depth) {
        return children.get(i).toString(depth + 1);
    }

    private String childAndKeyString(int i, int depth) {
        String s = "";
        s += childString(i, depth);
        s += ",";
        s += keyString(i, depth);
        return s;
    }
}