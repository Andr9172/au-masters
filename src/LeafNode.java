public class LeafNode extends Node {

    Edge edge;

    public LeafNode(InternalNode parent, UserInfo userInfo, Edge edge, int boundaryNodes) {
        super(parent, userInfo, true, false, boundaryNodes);
        this.edge = edge;
    }
}
