public class BTree {
    private BTreeNode root;
   BTree(String t) {
       try {
           root = new BTreeNode(Integer.parseInt(t),true);
       }
       catch (NumberFormatException ex) {
          throw new RuntimeException("t is not a number", ex);
       }
   }
}
