public class LeafNode extends Node {

    Edge edge;

    public LeafNode(InternalNode parent, int spineWeight, Edge edge, int boundaryNodes) {
        super(parent, spineWeight, true, false, boundaryNodes);
        this.edge = edge;
    }
}
