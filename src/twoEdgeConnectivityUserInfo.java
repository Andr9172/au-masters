import java.util.ArrayList;
import java.util.HashMap;

public class twoEdgeConnectivityUserInfo implements UserInfo {

    public int coverC;
    public int coverCPlus;
    public int coverCMinus;

    public Edge coverEdgeC;
    public Edge coverEdgeCPlus;

    public ArrayList<Node> boundaryVertices;


    // Size
    public HashMap<Node, ArrayList<ArrayList<Integer>>> size;
    // Incident
    public HashMap<Node, ArrayList<ArrayList<Integer>>> incident;


}
