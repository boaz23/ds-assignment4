public class BTreeNode {
    private String[] keys;
    private BTreeNode[] children;
    private int t;
    private int n;
    private boolean leaf;
    private boolean root;

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

    protected int minKeys() { return root ? 1 : t-1;}
    protected int maxKeys() { return 2*t-1;}

    public boolean isEmpty() {
        return n == 0;
    }

    public NodeIndexPair search(String password) {
        NodeIndexPair nodeIndexPair = this.searchLocal(password);
        if (isKeyInThis(nodeIndexPair)) {
            return nodeIndexPair;
        }
        // we haven't found, nowhere else to look for the key
        else if (leaf) {
            return null;
        }
        // we haven't found, nowhere else to look for the key
        else {
            return children[nodeIndexPair.getIndex()].search(password);
        }
    }

    private NodeIndexPair searchLocal(String password) {
        int i = 0;
        int compareResult = 0;

        // find the first index such that the key is smaller than the key in that index
        while (i < n && (compareResult = compare(password, keys[i])) > 0)
            i++;

        // if we're still in bounds of this node the last compare
        // result is 0, then we found it
        if (i < n & compareResult == 0)
            return new NodeIndexPair(this, i);
        else
            return new NodeIndexPair(null, i);
    }

    BTreeNode rootInsert(String password) {
        // if this node is full, need to split
        if (n == maxKeys()) {
            BTreeNode s = new BTreeNode(t, root);
            root = false;
            s.leaf = false;
            s.n = 0;
            s.children[0] = this;
            s.splitChild(0);
            s.insertNonfull(password);
            return s;
        }
        // find the right leaf and insert there
        else {
            insertNonfull(password);
            return this;
        }
    }

    protected void splitChild(int i) {
        BTreeNode y = children[i];
        BTreeNode z = createNewSplitNode(y.leaf);

        for (int j = 0; j < t-1; j++) {
            z.keys[j] = y.keys[j + t];
            y.keys[j + t] = null;
        }

        if (!y.leaf) {
            for (int j = 0; j < t; j++) {
                z.children[j] = y.children[j + t];
                y.children[j + t] = null;
            }
        } //if

        for (int j = n; j > i; j--)
            children[j+1] = children[j];

        children[i+1] = z;

        for (int j = n-1; j >= i; j--)
            keys[j+1] = keys[j];

        keys[i] = y.keys[t-1];
        y.keys[t-1] = null;

        n++;
        y.n = t-1;
    }

    private BTreeNode createNewSplitNode(boolean leaf) {
        BTreeNode z = new BTreeNode(t);
        z.leaf = leaf;
        z.n = t-1;
        return z;
    }

    private void insertNonfull(String password) {
        int i = n-1;
        if (leaf) {
            for (; i >= 0 && compare(password, keys[i]) < 0; i--) {
                keys[i+1] = keys[i];
            } //while , we doing shift to the right
            keys[i+1] = password;
            n++;
        } // if
        else {
            for (; i >= 0 && compare(password, keys[i]) < 0; i--) {}
            i++;
            if (children[i].n == children[i].maxKeys()) {
                splitChild(i);
                if (compare(password, keys[i]) > 0) {
                    i++;
                }
            }
            children[i].insertNonfull(password);
        }
    }

    BTreeNode rootDelete(String password) {
        BTreeNode root;
        if (n == minKeys() & children[0].n == children[0].minKeys() & children[1].n == children[1].minKeys()) {
            root = merge(0);
            root.root = this.root;
        }
        else {
            root = this;
        }

        root.deleteNotMinimumKeys(password);
        return root;
    }

    public void deleteNotMinimumKeys(String password) {
        NodeIndexPair nodeIndexPair = searchLocal(password);
        int i = nodeIndexPair.getIndex();
        BTreeNode y = children[i];
        if (isKeyInThis(nodeIndexPair)) {
            BTreeNode z = children[i+1];

            // case 1
            if (this.leaf) {
                deleteFromLeaf(i);
            }
            // case 2
            else if (y.n > y.minKeys()) {
                replaceWithPredecessorAndDelete(i);
            }
            // case 3
            else if (z.n > z.minKeys()) {
                replaceWithSuccessorAndDelete(i);
            }
            // case 4
            else {
                y = merge(i);
                y.deleteNotMinimumKeys(password);
            }
        }
        // password < keys[i] | i == n
        else if (!leaf) {
            if (y.n == y.minKeys()) {
                BTreeNode leftSibling = null;
                BTreeNode rightSibling = null;
                if (i > 0 && (leftSibling = children[i - 1]).n > leftSibling.minKeys()) {
                    shiftRight(i);
                }
                else if (i < n && (rightSibling = children[i + 1]).n > rightSibling.minKeys()) {
                    shiftLeft(i);
                }
                else if (leftSibling != null) {
                    y = merge(i - 1);
                }
                else { // rightSibling != null
                    y = merge(i);
                }
            }

            y.deleteNotMinimumKeys(password);
        }
    }

    private void shiftRight(int i) {
        BTreeNode y = children[i];

        // right shift all the keys in y and put keys[i - 1] in 0
        for (int j = y.n; j > 0; j--) {
            y.keys[j] = y.keys[j - 1];
        }
        y.keys[0] = keys[i - 1];
        y.n++;

        // take leftSibling.keys[leftSibling.n - 1] and put in keys[i - 1]
        BTreeNode leftSibling = children[i - 1];
        keys[i - 1] = leftSibling.keys[leftSibling.n - 1];
        leftSibling.n--;
        leftSibling.keys[leftSibling.n] = null;

        // take the right most child of leftSibling and put it as the left most child of y
        if (!y.leaf) {
            for (int j = y.n + 1; j > 0; j++) {
                y.children[j] = y.children[j - 1];
            }
            y.children[0] = leftSibling.children[leftSibling.n];
            leftSibling.children[leftSibling.n] = null;
        }
    }

    private void shiftLeft(int i) {
        BTreeNode y = children[i];
        BTreeNode rightSibling = children[i + 1];

        y.keys[y.n] = keys[i];
        y.n++;
        if (!y.leaf) {
            y.children[y.n] = rightSibling.children[0];
        }

        keys[i] = rightSibling.keys[0];
        for (int j = 0; j < rightSibling.n - 1; j++) {
            rightSibling.keys[j] = rightSibling.keys[j + 1];
            if (!rightSibling.leaf) {
                rightSibling.children[j] = rightSibling.children[j + 1];
            }
        }
        rightSibling.keys[rightSibling.n - 1] = null;
        if (!rightSibling.leaf) {
            rightSibling.children[rightSibling.n - 1] = rightSibling.children[rightSibling.n];
            rightSibling.children[rightSibling.n] = null;
        }
        rightSibling.n--;
    }

    private void deleteFromLeaf(int i) {
        for (; i < n - 1; i++) {
            keys[i] = keys[i + 1];
        }
        keys[n - 1] = null;
        n--;
    }

    private void replaceWithPredecessorAndDelete(int i) {
        NodeIndexPair nodeIndexPair = findPredecessor(i);
        String predecessor = nodeIndexPair.getNode().keys[nodeIndexPair.getIndex()];
        children[i].deleteNotMinimumKeys(predecessor);
        keys[i] = predecessor;
    }

    private void replaceWithSuccessorAndDelete(int i) {
        NodeIndexPair nodeIndexPair = findSuccessor(i);
        String successor = nodeIndexPair.getNode().keys[nodeIndexPair.getIndex()];
        children[i + 1].deleteNotMinimumKeys(successor);
        keys[i] = successor;
    }

    private BTreeNode merge(int i) {
        BTreeNode y = children[i];
        BTreeNode z = children[i+1];

        BTreeNode merged = new BTreeNode(t);
        merged.n = 2*t-1;
        merged.leaf = y.leaf;

        for (int j = 0; j < t-1; j++) {
            merged.keys[j] = y.keys[j];
            merged.children[j] = y.children[j];

            merged.keys[j + t] = z.keys[j];
            merged.children[j + t] = z.children[j];
        }
        merged.keys[t-1] = keys[i];
        merged.children[t-1] = y.children[t-1];
        merged.children[2*t-1] = z.children[t-1];

        // move keys and children 1 to the left
        for (int j = i; j < n - 1; j++) {
            keys[i] = keys[i + 1];
        }
        keys[n - 1] = null;

        children[i] = merged;
        for (int j = i + 1; j < n; j++) {
            children[j] = children[j + 1];
        }
        children[n] = null;
        n--;

        return merged;
    }

    private boolean isKeyInThis(NodeIndexPair nodeIndexPair) {
        return nodeIndexPair.getNode() == this;
    }

    public NodeIndexPair findSuccessor(int i) {
        if (leaf) {
            if (i < n - 1) {
                return new NodeIndexPair(this, i + 1);
            }

            return new NodeIndexPair(null, i);
        }

        return children[i+1].findMinimum();
    }

    public NodeIndexPair findMinimum() {
        if (n == 0) {
            return new NodeIndexPair(null, -1);
        }

        BTreeNode node = this;
        while (!node.leaf) {
            node = node.children[0];
        }

        return new NodeIndexPair(node, 0);
    }

    public NodeIndexPair findPredecessor(int i) {
        if (leaf) {
            if (i > 0) {
                return new NodeIndexPair(this, i - 1);
            }

            return new NodeIndexPair(null, i);
        }

        return children[i].findMaximum();
    }

    public NodeIndexPair findMaximum() {
        if (n == 0) {
            return new NodeIndexPair(null, -1);
        }

        BTreeNode node = this;
        while (!node.leaf) {
            node = node.children[node.n];
        }

        return new NodeIndexPair(node, node.n - 1);
    }

    // if password1 > password2 we return positive number
    // if password1 = password2 we return 0
    // if password1 < password2 we return negative number
    protected int compare(String password1 , String password2) {
        return password1.compareTo(password2);
    }

    @Override
    public String toString() {
        return isEmpty() ? "" : toString(0);
    }

    public String toString(int depth) {
        String inorder = "";

        if (leaf) {
            inorder += keys[0] + "_"  + depth;
            for (int i = 1; i < n; i++) {
                inorder += "," + keys[i] + "_" + depth;
            } // for
        }
        // not leaf
        else {
            inorder += children[0].toString(depth + 1);
            inorder += "," + keys[0] + "_" + depth;
            for (int i = 1; i < n; i++) {
                inorder += "," + children[i].toString(depth + 1);
                inorder += "," + keys[i] + "_" + depth;
            } // for
            inorder +=  "," + children[n].toString(depth+1);
        }

        return inorder;
    }
}