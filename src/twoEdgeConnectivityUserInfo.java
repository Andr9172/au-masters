import java.util.ArrayList;
import java.util.HashMap;

public class twoEdgeConnectivityUserInfo implements UserInfo {

    public int coverC;
    public int coverCPlus;
    public int coverCMinus;

    public Edge coverEdgeC;
    public Edge coverEdgeCPlus;

    public ArrayList<Vertex> boundaryVertices;





    // Size
    public HashMap<Vertex, ArrayList<Integer>> size2;
    public HashMap<Vertex, ArrayList<Integer>> size3;
    public HashMap<Vertex, ArrayList<ArrayList<Integer>>> size4;
    // Incident
    public HashMap<Vertex, ArrayList<Integer>> incident2;
    public HashMap<Vertex, ArrayList<Integer>> incident3;
    public HashMap<Vertex, ArrayList<ArrayList<Integer>>> incident4;


}