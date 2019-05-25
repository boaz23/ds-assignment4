public class BTreeNode {
    String[] keys;
    BTreeNode[] children;
    int t;
    int n;
    boolean leaf;
    boolean root;

    BTreeNode(int t, boolean root) {
        this.t = t;
        leaf = true;
        this.root = root;
        keys = new String[2*t - 1];
        children = new BTreeNode[2*t];
        n = 0;
    }

    BTreeNode(int t) {
        this(t,false);
    }

    private int minKeys() { return root ? 1 : t - 1;}
    private int maxKeys() { return 2*t - 1;}

    public boolean isEmpty() {
        return n == 0;
    }
    public boolean isFull() {
        return n == 2*t - 1;
    }
    public boolean needsKey() {
        return n == t - 1;
    }

    public NodeIndexPair search(String password) {
        FoundIndexPair searchResult = localSearch(password);
        if (keyIsInThisNode(searchResult)) {
            return new NodeIndexPair(this, searchResult.index());
        }
        // we haven't found, nowhere else to look for the key
        else if (leaf) {
            return null;
        }
        // we haven't found, look for the key in the child
        else {
            return children[searchResult.index()].search(password);
        }
    }

    public NodeIndexPair findSuccessor(int i) {
        NodeIndexPair nodeIndexPair;
        if (leaf) {
            if (i < n - 1) {
                nodeIndexPair = new NodeIndexPair(this, i + 1);
            }
            else {
                nodeIndexPair = new NodeIndexPair(null, i);
            }
        }
        else {
            nodeIndexPair = children[i + 1].findMinimum();
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
            nodeIndexPair = children[i].findMaximum();
        }

        return nodeIndexPair;
    }

    public NodeIndexPair findMinimum() {
        NodeIndexPair nodeIndexPair;
        if (n == 0) {
            nodeIndexPair = new NodeIndexPair(null, -1);
        }
        else {
            BTreeNode node = this;
            while (!node.leaf) {
                node = node.children[0];
            }

            nodeIndexPair = new NodeIndexPair(node, 0);
        }

        return nodeIndexPair;
    }

    public NodeIndexPair findMaximum() {
        NodeIndexPair nodeIndexPair;
        if (n == 0) {
            nodeIndexPair = new NodeIndexPair(null, -1);
        }
        else {
            BTreeNode node = this;
            while (!node.leaf) {
                node = node.children[node.n];
            }

            nodeIndexPair = new NodeIndexPair(node, node.n - 1);
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
        while (i < n && (compareResult = compare(password, keys[i])) > 0)
            i++;

        // if we're still in bounds of this node the last compare
        // result is 0, then we found it
        boolean found = i < n & compareResult == 0;
        return new FoundIndexPair(found, i);
    }

    BTreeNode createRootNode() {
        BTreeNode newRoot = new BTreeNode(t, root);
        newRoot.leaf = false;
        newRoot.n = 0;
        return newRoot;
    }

    void splitChild(int i) {
        BTreeNode leftChild = children[i];
        BTreeNode newRightChild = leftChild.createNewSplitNode();

        // take the keys t+1,...,2t-1 from the child we're splitting
        // and put them as the keys 1,...,t respectively in the new node.
        newRightChild.takeKeysFrom(leftChild, t,0, t - 1);
        if (!leftChild.leaf) {
            // take the children t+1,...,2t from the child we're splitting
            // and put them as the children 1,...,t in the new node.
            newRightChild.takeChildrenFrom(leftChild, t,0, t);
        }

        // make room for the key number t (the middle key) of the full node
        // so we can place it as the i+1 key
        Utils.rightShift(keys, i,n - 1);
        takeKeyFrom(leftChild, t - 1, i);

        // make room for the new node (which will be placed after the node we're splitting
        Utils.rightShift(children,i + 1, n);
        children[i + 1] = newRightChild;

        n++;
        leftChild.n = leftChild.minKeys();
    }

    void insertNonFull(String password) {
        int i;
        if (leaf) {
            // look for the right place for the new key and make room for it
            // while we go
            i = rightShiftKeysBiggerThan(password);
            keys[i + 1] = password;
            n++;
        } // if
        else {
            // find the child which the key should go into
            i = localSearch(password).index();

            // we need to split the child it is full
            if (children[i].isFull()) {
                splitChild(i);
                if (compare(password, keys[i]) > 0) {
                    i++;
                }
            }
            children[i].insertNonFull(password);
        }
    }

    /**
     * Look for the first key from the right which is smaller than the specified key
     * and right shift all the keys when doing it
     * @param password The key
     */
    private int rightShiftKeysBiggerThan(String password) {
        int i;
        for (i = n - 1; i >= 0 && compare(password, keys[i]) < 0; i--) {
            keys[i + 1] = keys[i];
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
        Utils.takeItems(from.children, fromStartIndex, children, thisStartIndex, count);
    }

    private void takeKeysFrom(BTreeNode from, int fromStartIndex, int thisStartIndex, int count) {
        Utils.takeItems(from.keys, fromStartIndex, keys, thisStartIndex, count);
    }

    private void takeKeyFrom(BTreeNode from, int fromKeyIndex, int thisKeyIndex) {
        takeKeysFrom(from, fromKeyIndex, thisKeyIndex, 1);
    }

    private void takeChildFrom(BTreeNode from, int fromChildIndex, int thisChildIndex) {
        takeChildrenFrom(from, fromChildIndex, thisChildIndex, 1);
    }


    void deleteNotMinimumKeys(String password) {
        FoundIndexPair searchResult = localSearch(password);
        int i = searchResult.index();
        if (keyIsInThisNode(searchResult)) {
            deleteNotMinimumKeysInThis(password, i);
        }
        // password < keys[i] | i == n
        // if it's a leaf, then the key is not in the tree, so nothing to do
        else if (!leaf) {
            deleteNotMinimumKeysInChild(password, i);
        }
    }

    private void deleteNotMinimumKeysInThis(String password, int i) {
        BTreeNode leftChild = children[i];
        BTreeNode rightChild = children[i + 1];

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
        BTreeNode leftChild = children[i];
        if (leftChild.needsKey()) {
            // shift with left sibling
            if (i > 0 && !children[i - 1].needsKey()) {
                shiftRight(i);
            }
            // shift with right sibling
            else if (i < n && !children[i + 1].needsKey()) {
                shiftLeft(i);
            }
            // merge with left sibling
            else if (i > 0) {
                leftChild = merge(i - 1);
            }
            // merge with right sibling
            else {
                leftChild = merge(i);
            }
        }

        leftChild.deleteNotMinimumKeys(password);
    }

    private void deleteFromLeaf(int i) {
        Utils.leftShift(keys, i + 1, n - 1);
        keys[n - 1] = null;
        n--;
    }

    private void replaceWithPredecessorAndDeleteItRecursively(int i) {
        NodeIndexPair nodeIndexPair = findPredecessor(i);
        String predecessor = nodeIndexPair.node().keys[nodeIndexPair.index()];
        children[i].deleteNotMinimumKeys(predecessor);
        keys[i] = predecessor;
    }

    private void replaceWithSuccessorAndDeleteItRecursively(int i) {
        NodeIndexPair nodeIndexPair = findSuccessor(i);
        String successor = nodeIndexPair.node().keys[nodeIndexPair.index()];
        children[i + 1].deleteNotMinimumKeys(successor);
        keys[i] = successor;
    }

    BTreeNode merge(int i) {
        BTreeNode leftChild = children[i];
        BTreeNode rightChild = children[i + 1];

        leftChild.takeKeyFrom(this, i, t - 1);
        leftChild.takeKeysFrom(rightChild, 0, t, t - 1);
        if (!leftChild.leaf) {
            leftChild.takeChildrenFrom(rightChild, 0, t, t);
        }

        // we now have a free space in both arrays, we need to close it
        Utils.leftShift(keys, i + 1, n - 1);
        Utils.leftShift(children, i + 2, n);
        keys[n - 1] = null;
        children[n] = null;

        n--;
        leftChild.n = leftChild.maxKeys();

        return leftChild;
    }

    private void shiftRight(int i) {
        BTreeNode leftChild = children[i];
        BTreeNode leftSibling = children[i - 1];

        // make room for the previous key
        Utils.rightShift(leftChild.keys, 0, leftChild.n - 1);
        leftChild.takeKeyFrom(this, i - 1, 0);

        // take leftSibling.keys[leftSibling.n - 1] and put in keys[i - 1]
        takeKeyFrom(leftSibling, leftSibling.n - 1, i - 1);

        // take the right most child of leftSibling and put it as the left most child of leftChild
        if (!leftChild.leaf) {
            Utils.rightShift(leftChild.children, 0, leftChild.n);
            leftChild.takeChildFrom(leftSibling, leftSibling.n, 0);
        }

        leftChild.n++;
        leftSibling.n--;
    }

    private void shiftLeft(int i) {
        BTreeNode leftChild = children[i];
        BTreeNode rightSibling = children[i + 1];

        leftChild.takeKeyFrom(this, i, leftChild.n);
        takeKeyFrom(rightSibling, 0, i);
        Utils.leftShift(rightSibling.keys, 1, rightSibling.n - 1);
        rightSibling.keys[rightSibling.n - 1] = null;

        if (!leftChild.leaf) {
            leftChild.takeChildFrom(rightSibling, 0, leftChild.n + 1);
            Utils.leftShift(rightSibling.children, 1, rightSibling.n);
            rightSibling.children[n] = null;
        }

        leftChild.n++;
        rightSibling.n--;
    }

    private boolean keyIsInThisNode(FoundIndexPair localSearchResult) {
        return localSearchResult.found();
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
            for (int i = 1; i < n; i++) {
                s += "," + keyString(i, depth);
            } // for
        }
        else {
            s += childAndKeyString(0, depth);
            for (int i = 1; i < n; i++) {
                s += "," + childAndKeyString(i, depth);
            } // for
            s += "," + childString(n, depth);
        }

        return s;
    }

    private String keyString(int i, int depth) {
        return keys[i] + "_"  + depth;
    }

    private String childString(int i, int depth) {
        return children[i].toString(depth + 1);
    }

    private String childAndKeyString(int i, int depth) {
        String s = "";
        s += childString(i, depth);
        s += ",";
        s += keyString(i, depth);
        return s;
    }
}