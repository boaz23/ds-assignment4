public class BTreeNode {
    private final Array<String> keys;
    final Array<BTreeNode> children;
    private final int t;
    private int n;
    private boolean leaf;
    boolean root;

    BTreeNode(int t, boolean root) {
        this.t = t;
        leaf = true;
        this.root = root;
        n = 0;
        keys = new Array<>(new String[2*t - 1]);
        children = new Array<>(new BTreeNode[keys.capacity() + 1]);
    }

    private BTreeNode(int t) {
        this(t,false);
    }

    /**
     * Returns whether this node is a leaf
     */
    public boolean isLeaf() {
        return leaf;
    }

    /**
     * Returns the minimum amount of keys this node can hold
     */
    private int minKeys() {
        return root ? 1 : t - 1;
    }

    /**
     * Returns the maximum amount of keys this node can hold
     */
    private int maxKeys() {
        return keys.capacity();
    }

    /**
     * Returns the size of this node,
     * which is the amount of keys this node holds
     * and also the amount of children this node has minus 1 (if it's not a leaf)
     */
    public int size() {
        return n;
    }

    /**
     * Returns whether this node isn't holding any keys
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Returns whether this node holds the maximum allowed keys
     */
    public boolean isFull() {
        return size() == maxKeys();
    }

    /**
     * Returns whether this node holds the minimum allowed keys
     */
    public boolean needsKey() {
        return size() == minKeys();
    }

    /**
     * Searches this node for the specified password
     * @param password The password to look for
     */
    public NodeIndexPair search(String password) {
        FoundIndexPair searchResult = localSearch(password);
        if (searchResult.found()) {
            return new NodeIndexPair(this, searchResult.index());
        }
        // we haven't found, nowhere else to look for the key
        else if (isLeaf()) {
            return null;
        }
        // we haven't found, look for the key in the child
        else {
            return children.get(searchResult.index()).search(password);
        }
    }

    /**
     * Finds the successor of the key at the specified index
     * @param i The index
     */
    public NodeIndexPair findSuccessor(int i) {
        NodeIndexPair nodeIndexPair;
        if (isLeaf()) {
            if (keys.hasRight(i)) {
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

    /**
     * Finds the predecessor of the key at the specified index
     * @param i The index
     */
    public NodeIndexPair findPredecessor(int i) {
        NodeIndexPair nodeIndexPair;
        if (isLeaf()) {
            if (keys.hasLeft(i)) {
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

    /**
     * Finds the minimum key in the subtree rooted at this node
     */
    public NodeIndexPair findMinimum() {
        NodeIndexPair nodeIndexPair;
        if (isEmpty()) {
            nodeIndexPair = new NodeIndexPair(null, -1);
        }
        else {
            BTreeNode node = this;
            while (!node.isLeaf()) {
                node = node.children.getFirst();
            }

            nodeIndexPair = new NodeIndexPair(node, node.keys.firstIndex());
        }

        return nodeIndexPair;
    }

    /**
     * Finds the maximum key in the subtree rooted at this node
     */
    public NodeIndexPair findMaximum() {
        NodeIndexPair nodeIndexPair;
        if (isEmpty()) {
            nodeIndexPair = new NodeIndexPair(null, -1);
        }
        else {
            BTreeNode node = this;
            while (!node.isLeaf()) {
                node = node.children.getLast();
            }

            nodeIndexPair = new NodeIndexPair(node, node.keys.lastIndex());
        }

        return nodeIndexPair;
    }

    @Override
    public String toString() {
        String s;
        if (isEmpty()) {
            s = "";
        }
        else {
            StringBuilder sb = new StringBuilder();
            appendString(sb, 0);
            s = sb.toString();
        }

        return s;
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
        while (i < size() && (compareResult = compareWithKeyAt(password, i)) > 0)
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

    /**
     * Splits the child at the specified index to 2 nodes
     * @param i The index of the child
     */
    void splitChild(int i) {
        BTreeNode child = children.get(i);
        BTreeNode splitChild = child.createNewSplitNode();

        // take the keys t+1,...,2t-1 from the child we're splitting
        // and put them as the keys 1,...,t respectively in the new node.
        splitChild.takeKeysFrom(child, t);
        if (!child.isLeaf()) {
            // take the children t+1,...,2t from the child we're splitting
            // and put them as the children 1,...,t respectively in the new node.
            splitChild.takeChildrenFrom(child, t);
        }

        // take the middle key of the child and insert it right in between
        // the child and new child
        keys.insertAt(i, child.keys.removeLast());

        // insert the new node as a child after the child we split
        children.insertAt(i + 1, splitChild);

        n++;
        child.n = child.minKeys();
    }

    /**
     * Inserts a password to the subtree rooted at this node
     * (knowing this node is not full)
     * @param password The password to insert
     */
    void insertNonFull(String password) {
        if (isLeaf()) {
            insertItemToLeaf(password);
        } // if
        else {
            insertItemToSubtree(password);
        }
    }

    /**
     * Inserts a password to the subtree rooted at this node
     * @param password The password to insert
     */
    private void insertItemToSubtree(String password) {
        int i = localSearch(password).index();
        insertItemToChild(password, i);

    }

    /**
     * Inserts a password to the subtree rooted at the child at the specified index
     * @param password The password to insert
     * @param i The child index
     */
    private void insertItemToChild(String password, int i) {
        // we need to split the child it is full
        if (children.get(i).isFull()) {
            splitChild(i);
            if (compareWithKeyAt(password, i) > 0) {
                i++;
            }
        }

        children.get(i).insertNonFull(password);
    }

    /**
     * Inserts a password to this node (knowing it's a leaf)
     * @param password The password to insert
     */
    private void insertItemToLeaf(String password) {
        int i;// look for the right place for the new key and make room for it
        // while we go
        i = rightShiftKeysBiggerThan(password);
        keys.set(i + 1, password);
        n++;
        keys.setSize(n);
    }

    /**
     * Look for the first key from the right which is smaller than the specified key
     * and right shift all the keys when doing it
     * @param password The key
     */
    private int rightShiftKeysBiggerThan(String password) {
        int i;
        for (i = keys.lastIndex(); i >= 0 && compareWithKeyAt(password, i) < 0; i--) {
            keys.set(i + 1, keys.get(i));
        }

        return i;
    }

    private BTreeNode createNewSplitNode() {
        BTreeNode splitNode = new BTreeNode(t);
        splitNode.leaf = isLeaf();
        splitNode.n = splitNode.minKeys();
        return splitNode;
    }

    /**
     * Takes keys from the given node and places them at the end one after the other.
     * The other node keys are set to null.
     * @param from The node to take keys from
     * @param startIndex The starting index in given node which keys should be taken from
     */
    private void takeKeysFrom(BTreeNode from, int startIndex) {
        keys.takeItemsFrom(from.keys, startIndex);
    }

    /**
     * Takes children from the given node and places them at the end one after the other.
     * The other node keys are set to null.
     * @param from The node to take children from
     * @param startIndex The starting index in given node which children should be taken from
     */
    private void takeChildrenFrom(BTreeNode from, int startIndex) {
        children.takeItemsFrom(from.children, startIndex);
    }

    /**
     * Deletes the password from the subtree rooted at this node
     * (if it is found in it)
     * @param password The password to delete
     */
    void deleteNotMinimumKeys(String password) {
        FoundIndexPair searchResult = localSearch(password);
        int i = searchResult.index();
        if (searchResult.found()) {
            deleteNotMinimumKeysInThis(password, i);
        }
        // password < keys[i] | i == n
        // if it's a leaf, then the key is not in the tree, so nothing to do
        else if (!isLeaf()) {
            deleteNotMinimumKeysInChild(password, i);
        }
    }

    /**
     * Deletes the password from this node
     * @param password The password to delete
     * @param i The index of the password in the keys array
     */
    private void deleteNotMinimumKeysInThis(String password, int i) {
        BTreeNode leftChild = children.get(i);
        BTreeNode rightChild = children.get(i + 1);

        // case 1
        if (isLeaf()) {
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

    /**
     * Deletes the password from the subtree rooted at the child at the specified index
     * (if it is found in it)
     * @param password The password to delete
     * @param i The child index
     */
    private void deleteNotMinimumKeysInChild(String password, int i) {
        BTreeNode child = children.get(i);
        if (child.needsKey()) {
            // shift with left sibling
            if (children.hasLeft(i) && !children.get(i - 1).needsKey()) {
                shiftRight(i);
            }
            // shift with right sibling
            else if (children.hasRight(i) && !children.get(i + 1).needsKey()) {
                shiftLeft(i);
            }
            // merge with left sibling
            else if (children.hasLeft(i)) {
                child = merge(i - 1);
            }
            // merge with right sibling
            else {
                child = merge(i);
            }
        }

        child.deleteNotMinimumKeys(password);
    }

    /**
     * Deletes the keys from this node (knowing it's a leaf)
     * @param i The index of the key to remove
     */
    private void deleteFromLeaf(int i) {
        keys.removeAt(i);
        n--;
    }

    /**
     * Replaces the key at the specified index with it's predecessor and
     * then recursively delete the predecessor from the subtree
     * @param i The index of the key
     */
    private void replaceWithPredecessorAndDeleteItRecursively(int i) {
        NodeIndexPair nodeIndexPair = findPredecessor(i);
        String predecessor = nodeIndexPair.node().keys.get(nodeIndexPair.index());
        children.get(i).deleteNotMinimumKeys(predecessor);
        keys.set(i, predecessor);
    }

    /**
     * Replaces the key at the specified index with it's successor and
     * then recursively delete the successor from the subtree
     * @param i The index of the key
     */
    private void replaceWithSuccessorAndDeleteItRecursively(int i) {
        NodeIndexPair nodeIndexPair = findSuccessor(i);
        String successor = nodeIndexPair.node().keys.get(nodeIndexPair.index());
        children.get(i + 1).deleteNotMinimumKeys(successor);
        keys.set(i, successor);
    }

    /**
     * Merges the child at the specified index with it's right sibling
     * @param i The child index
     * @return The merged node
     */
    BTreeNode merge(int i) {
        BTreeNode leftChild = children.get(i);

        // take the key "separating" both children and insert it
        // to the left child as the last key.
        leftChild.keys.insertLast(keys.removeAt(i));

        // remove the right child, as it will be merged into the left child
        BTreeNode rightChild = children.removeAt(i + 1);

        // take the keys 1,...,t-1 from the right child
        // and put them as the keys t+1,...,2t-1 respectively in the left child.
        leftChild.takeKeysFrom(rightChild, 0);
        if (!leftChild.isLeaf()) {
            // take the children 1,...,t from the right child
            // and put them as the children t+1,...,2t respectively in the left child.
            leftChild.takeChildrenFrom(rightChild, 0);
        }

        n--;
        leftChild.n = leftChild.maxKeys();
        return leftChild;
    }

    /**
     * Perform a right shift of keys and a child with the left sibling of the child at specified index
     * @param i The child index
     */
    private void shiftRight(int i) {
        BTreeNode child = children.get(i);
        BTreeNode leftSibling = children.get(i - 1);

        // take the key that "separates" both children and insert it
        // to the child as the first key
        child.keys.insertAt(0, keys.get(i - 1));

        // take the last key of the left sibling and replace the key
        // we took earlier from this node
        keys.set(i - 1, leftSibling.keys.removeLast());
        if (!child.isLeaf()) {
            // take the last child of the left sibling and insert it
            // to the child as the first child
            BTreeNode transferredChild = leftSibling.children.removeLast();
            child.children.insertAt(0, transferredChild);
        }

        child.n++;
        leftSibling.n--;
    }

    /**
     * Perform a left shift of keys and a child with the right sibling of the child at specified index
     * @param i The child index
     */
    private void shiftLeft(int i) {
        BTreeNode child = children.get(i);
        BTreeNode rightSibling = children.get(i + 1);

        // take the key that "separates" both children and insert it
        // to the child as the last key
        child.keys.insertLast(keys.get(i));

        // take the first key of the right sibling and replace the key
        // we took earlier from this node
        keys.set(i, rightSibling.keys.removeAt(0));
        if (!child.isLeaf()) {
            // take the first child of the right sibling and insert it
            // to the child as the last child
            BTreeNode transferredChild = rightSibling.children.removeAt(0);
            child.children.insertLast(transferredChild);
        }

        child.n++;
        rightSibling.n--;
    }

    /**
     * Compare the given key with the key at specified index
     * @param password The key
     * @param i The index of the key in this node
     * @return
     */
    private int compareWithKeyAt(String password, int i) {
        return compare(password, keys.get(i));
    }

    /**
     * Compares two keys
     *
     * if password1 > password2 we return positive number
     * if password1 = password2 we return 0
     * if password1 < password2 we return negative number
     *
     * @param password1 The first key
     * @param password2 The second key
     */
    private int compare(String password1 , String password2) {
        return password1.compareTo(password2);
    }

    /**
     * Appends the string representation of the subtree rooted at this node
     * to the specified string builder starting at the specified depth
     * @param sb The string builder to append to
     * @param depth The depth of this node
     */
    private void appendString(StringBuilder sb, int depth) {
        if (isLeaf()) {
            appendLeafString(sb, depth);
        }
        else {
            appendInnerNodeString(sb, depth);
        }
    }

    private void appendLeafString(StringBuilder sb, int depth) {
        int lastKeyIndex = keys.lastIndex();
        for (int i = 0; i < lastKeyIndex; i++) {
            appendKeyString(sb, i, depth);
            sb.append(",");
        } // for
        appendKeyString(sb, lastKeyIndex, depth);
    }

    private void appendInnerNodeString(StringBuilder sb, int depth) {
        for (int i = 0; i < size(); i++) {
            appendChildString(sb, i, depth);
            sb.append(",");
            appendKeyString(sb, i, depth);
            sb.append(",");
        } // for
        appendChildString(sb, size(), depth);
    }

    private void appendKeyString(StringBuilder sb, int i, int depth) {
        sb.append(keys.get(i))
          .append("_")
          .append(depth);
    }

    private void appendChildString(StringBuilder sb, int i, int depth) {
        children.get(i).appendString(sb, depth + 1);
    }
}