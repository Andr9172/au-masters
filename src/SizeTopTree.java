public class SizeTopTree implements TopTreeInterface {
    @Override
    public void combine(Node node) {
        node.userInfo = computeCombine(node);
    }

    @Override
    public UserInfo computeCombine(Node t) {
        SizeUserInfo userInfo = (SizeUserInfo) newUserInfo();
        if (t.isLeaf) {
            userInfo.size = 1;
        } else {
            InternalNode n = (InternalNode) t;
            SizeUserInfo userInfo1 = (SizeUserInfo) n.children.get(0).userInfo;
            SizeUserInfo userInfo2 = (SizeUserInfo) n.children.get(1).userInfo;

            int size0 = userInfo1.size;
            int size1 = userInfo2.size;
            userInfo.size = size0 + size1;
        }
        return userInfo;
    }

    @Override
    public Node search(Node root) {
        return null;
    }

    @Override
    public UserInfo newUserInfo() {
        return new SizeUserInfo(0);
    }

    @Override
    public void lazyCombine(Node n) {

    }

    @Override
    public void computeCombines(Node n) {

    }

    @Override
    public int combineCost(Node node) {
        return ((SizeUserInfo) node.userInfo).size;
    }

    @Override
    public void checkUserInfo(Node real, Node copy) {

    }

    @Override
    public void split(Node n) {

    }

    @Override
    public void pushDown(Node n) {

    }
}
