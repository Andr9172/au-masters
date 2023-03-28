import java.util.ArrayList;

public class twoEdgeConnectivityTopTree implements TopTreeInterface {

    int numberOfVertices = 0;
    int maxLevel = 0;

    public twoEdgeConnectivityTopTree(int numberOfVertices) {
        this.numberOfVertices = numberOfVertices;
        this.maxLevel = (int) Math.ceil(Math.log(numberOfVertices));
    }

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

    @Override
    public int combineCost(Node grandParent) {
        return 0;
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
            info.coverEdgeCPlus = e;
        }
        if (i > info.coverCMinus){
            info.coverCMinus = i;
            info.coverCPlus = i;
            info.coverEdgeCPlus = e;
        }

        // For X in {size, incident} and for all ...
        for (int j = -1; j <= i; j++){
            for (int k = -1; k <= maxLevel; k++){
                // For v in boundary nodes
                for (Node v : info.boundaryVertices){
                    info.size.get(v).get(j).set(k, info.size.get(v).get(-1).get(k));
                    info.incident.get(v).get(j).set(k, info.incident.get(v).get(-1).get(k));
                }            }
        }

    }

    private void uncover(Node n, int i){
        twoEdgeConnectivityUserInfo info = (twoEdgeConnectivityUserInfo) n.userInfo;

        if (info.coverC <= i){
            info.coverC = -1;
            info.coverEdgeC = null;
        }
        if (i < info.coverCPlus){
            // Do nothing
        }
        if (i >= info.coverCPlus){
            info.coverCPlus = -1;
            info.coverCMinus = Math.max(info.coverCMinus, i);
            info.coverEdgeCPlus = null;
        }

        // For X in {size, incident} and for all ...
        for (int j = -1; j <= i; j++){
            for (int k = -1; k <= maxLevel; k++){
                // For v in boundary nodes
                for (Node v : info.boundaryVertices){
                    info.size.get(v).get(j).set(k, info.size.get(v).get(i+1).get(k));
                    info.incident.get(v).get(j).set(k, info.incident.get(v).get(i+1).get(k));
                }
            }
        }

    }

    private void recover(Vertex v, Vertex w, int i){

    }

    private void swap(){

    }

    public void insert(){

    }

    public void delete(){

    }

    private void clean(Node n){
        twoEdgeConnectivityUserInfo info = (twoEdgeConnectivityUserInfo) n.userInfo;

        ArrayList<Node> children = n.parent.children;

        for(int i = 0; i < 2; i++){
            uncover(children.get(i), info.coverCMinus);
            cover(children.get(i), info.coverCPlus, info.coverEdgeCPlus);
        }

        info.coverCPlus = -1;
        info.coverCMinus = -1;
        info.coverEdgeCPlus = null;
    }

}
