public class SizeTopTree implements TopTreeInterface {
    @Override
    public void combine(Node node) {
        SizeUserInfo userInfo = (SizeUserInfo) node.userInfo;
        if (node.isLeaf){
            userInfo.size = 1;
        } else {
            InternalNode n = (InternalNode) node;
            SizeUserInfo userInfo1 = (SizeUserInfo) n.children.get(0).userInfo;
            SizeUserInfo userInfo2 = (SizeUserInfo) n.children.get(1).userInfo;

            int size0 = userInfo1.size;
            int size1 = userInfo2.size;
            userInfo.size = size0 + size1;
        };
    }

    @Override
    public Node search(Node root) {
        return null;
    }

    @Override
    public UserInfo newUserInfo() {
        return new SizeUserInfo(0);
    }
}
