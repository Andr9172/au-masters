public class MinimumSpanningTopTree implements TopTreeInterface{

    @Override
    public void combine(Node node) {
        MinimumSpanningTreeUserInfo userInfo = (MinimumSpanningTreeUserInfo) node.userInfo;
        if (isPoint(node)){
            userInfo.spineWeight = Integer.MIN_VALUE;
        } else if (node.isLeaf){
            LeafNode n = (LeafNode) node;
            userInfo.spineWeight = n.edge.weight;
        } else {
            InternalNode n = (InternalNode) node;
            MinimumSpanningTreeUserInfo userInfo1 = (MinimumSpanningTreeUserInfo) n.children.get(0).userInfo;
            MinimumSpanningTreeUserInfo userInfo2 = (MinimumSpanningTreeUserInfo) n.children.get(1).userInfo;

            int spineWeight0 = userInfo1.spineWeight;
            int spineWeight1 = userInfo2.spineWeight;
            userInfo.spineWeight = Math.max(spineWeight0, spineWeight1);
        };
    }


    @Override
    public Node search(Node root) {
        Node node = root;
        while (!node.isLeaf){
            InternalNode n = (InternalNode) node;
            MinimumSpanningTreeUserInfo userInfo1 = (MinimumSpanningTreeUserInfo) n.children.get(0).userInfo;
            MinimumSpanningTreeUserInfo userInfo2 = (MinimumSpanningTreeUserInfo) n.children.get(1).userInfo;

            int spineWeight0 = userInfo1.spineWeight;
            int spineWeight1 = userInfo2.spineWeight;
            if (spineWeight0 > spineWeight1){
                node = n.children.get(0);
            } else {
                node = n.children.get(1);
            }
        }
        return (LeafNode) node;
    }

    public LeafNode findMaximum(Node root){
        return (LeafNode) search(root);
    }

    @Override
    public UserInfo newUserInfo() {
        return new MinimumSpanningTreeUserInfo(0) ;
    }
}
