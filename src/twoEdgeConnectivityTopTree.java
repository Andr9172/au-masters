public class twoEdgeConnectivityTopTree implements TopTreeInterface {

    @Override
    public void combine(Node t) {

    }

    @Override
    public UserInfo computeCombine(Node t) {
        return null;
    }

    @Override
    public Node search(Node root) {
        return null;
    }

    @Override
    public UserInfo newUserInfo() {
        return null;
    }

    private void cover(Node n, int i, Edge e){
        twoEdgeConnectivityUserInfo info = (twoEdgeConnectivityUserInfo) n.userInfo;

        if (info.coverC <= i){
            info.coverC = i;
            info.coverEdgeC = e;
        }
        if (i < info.coverCPlus){
            // Do nothing
        }
        if (info.coverCMinus >= i && i >= info.coverCPlus){
            info.coverCPlus = i;
            info.coverEdgeC = e;
        }
        if (i > info.coverCMinus){
            info.coverCMinus = i;
            info.coverCPlus = i;
            info.coverEdgeC = e;
        }

        // For X in {size, incident} and for all ....


    }

    private void uncover(){

    }

    private void recover(){

    }

    private void swap(){

    }

    public void insert(){

    }

    public void delete(){

    }

    private void clean(){

    }

}
