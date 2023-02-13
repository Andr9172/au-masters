public class Node {

    public InternalNode parent;
    public int spineWeight; // Not sure this is needed for 2-connectivity?
    public boolean isLeaf;
    public boolean flip;
    public int numBoundary;

    public Node(InternalNode parent, int spineWeight, boolean isLeaf, boolean flip, int numBoundary) {
        this.parent = parent;
        this.spineWeight = spineWeight;
        this.isLeaf = isLeaf;
        this.flip = flip;
        this.numBoundary = numBoundary;
    }
}
