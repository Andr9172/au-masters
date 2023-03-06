public class Node {

    public InternalNode parent;
    public UserInfo userInfo; // Not sure this is needed for 2-connectivity?
    public boolean isLeaf;
    public boolean flip;
    public int numBoundary;
    public int spineWeight;

    public Node(InternalNode parent, UserInfo userInfo, boolean isLeaf, boolean flip, int numBoundary) {
        this.parent = parent;
        this.userInfo = userInfo;
        this.isLeaf = isLeaf;
        this.flip = flip;
        this.numBoundary = numBoundary;
    }
}
