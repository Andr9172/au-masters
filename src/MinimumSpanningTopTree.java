public class MinimumSpanningTopTree implements TopTreeInterface{

    @Override
    public void combine(Node node) {
        node.userInfo = computeCombine(node);
    }

    @Override
    public UserInfo computeCombine(Node t) {
        MinimumSpanningTreeUserInfo userInfo = (MinimumSpanningTreeUserInfo) newUserInfo();
        if (isPoint(t)){
            userInfo.spineWeight = Integer.MIN_VALUE;
        } else if (t.isLeaf){
            LeafNode n = (LeafNode) t;
            userInfo.spineWeight = n.edge.weight;
        } else {
            InternalNode n = (InternalNode) t;
            MinimumSpanningTreeUserInfo userInfo1 = (MinimumSpanningTreeUserInfo) n.children.get(0).userInfo;
            MinimumSpanningTreeUserInfo userInfo2 = (MinimumSpanningTreeUserInfo) n.children.get(1).userInfo;

            int spineWeight0 = userInfo1.spineWeight;
            int spineWeight1 = userInfo2.spineWeight;
            userInfo.spineWeight = Math.max(spineWeight0, spineWeight1);
        }
        return userInfo;
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
        return node;
    }

    public LeafNode findMaximum(Node root){
        return (LeafNode) search(root);
    }

    @Override
    public UserInfo newUserInfo() {
        return new MinimumSpanningTreeUserInfo(0) ;
    }

    @Override
    public int combineCost(Node grandParent) {
        return 0;
    }

    @Override
    public void split(Node n) {

    }

    @Override
    public void pushDown(Node n) {

    }
}
