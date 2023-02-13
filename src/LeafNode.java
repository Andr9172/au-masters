public class LeafNode extends Node {

    Edge edge;

    public LeafNode(InternalNode parent, int spineWeight, Edge edge) {
        super(parent, spineWeight, true, false, 0);
        this.edge = edge;
    }
}
