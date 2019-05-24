public class BTreeNode {
    private String[] passwords;
    private BTreeNode[] children;
    private int t;
    private int n;
    private boolean leaf;
    private boolean root;

    BTreeNode(int t, boolean root) {
        this.t = t;
        leaf = true;
        this.root = root;
        passwords = new String[2*t - 1];
        children = new BTreeNode[2*t];
        n=0;
    }

    BTreeNode(int t) {
        this(t,false);
    }

    protected int minKeys() { return root? 1:t-1;}
    protected int maxKeys() { return 2*t-1;}

    public void insert() {}

    public NodeIndexPair search(String password) {
        int i=0;
        int compareResult;
        while (i < n & (compareResult = compare(password, passwords[i])) > 0)
            i++;
        if (i < n & compareResult == 0)
            return new NodeIndexPair(this,i);
        else if (this.leaf)
            return null;
        else
            return children[i].search(password);
    }

    public void insert(String password) {



    }

    protected void splitChild(int i) {
        BTreeNode y = this.children[i];
        BTreeNode z = createNewSplitNode(y.leaf);
        for (int j = 0; j < t-1; j++)
            z.passwords[j] = y.passwords[j+t];

        if (!y.leaf) {
            for (int j = 0; j < t; j++)
                z.children[j] = y.children[j+t];
        } //if

        for (int j = n; n > i; j--)
            children[j+1] = children[j];

        children[i+1] = z;

        for (int j = n-1; n >= i; j--)
            passwords[j+1] = passwords[j];

        passwords[i] = y.passwords[t-1];

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
            for (;i >= 0 && compare(password,passwords[i]) < 0; i--) {
                passwords[i+1] = passwords[i];
            } //while , we doing shift to the right
            passwords[i+1] = password;
            n++;
        } // if

        else {
            for (; i >= 0 && compare(password, passwords[i]) < 0; i--) {}
            i++;
            if (children[i].n == children[i].maxKeys()) {
                children[i].splitChild(i);
                if (compare(password,passwords[i]) > 0) {
                    i++;
                }
            }
            children[i].insertNonfull(password);
        }
    }

//    private int binarySerchInNode() {
//
//
//
//    } idea for improvmant




    // if password1 > password2 we return positive number
    // if password1 = password2 we return 0
    // if password1 < password2 we return nagitive number

    protected int compare(String password1 , String password2) {
        return password1.toLowerCase().compareTo(password2.toLowerCase());
    }
}


