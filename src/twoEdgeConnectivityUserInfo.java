import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class twoEdgeConnectivityUserInfo implements UserInfo {

    public int coverC;
    public int coverCPlus;
    public int coverCMinus;

    public Edge coverEdgeC;
    public Edge coverEdgeCPlus;
    public HashSet<Edge> coverEdgeCMinus;

    public ArrayList<Vertex> boundaryVertices;

    // Size
    //public HashMap<Vertex, ArrayList<Integer>> size2;
    public HashMap<Vertex, HashMap<Integer, Integer>> size3;
    public HashMap<Vertex, HashMap<Integer, HashMap<Integer, Integer>>> size4;
    // Incident
    //public HashMap<Vertex, ArrayList<Integer>> incident2;
    public HashMap<Vertex, HashMap<Integer, Integer>> incident3;
    public HashMap<Vertex, HashMap<Integer, HashMap<Integer, Integer>>> incident4;

    public twoEdgeConnectivityUserInfo(){
        size3 = new HashMap<>();
        size4 = new HashMap<>();
        incident3 = new HashMap<>();
        incident4 = new HashMap<>();
        coverC = -1;
        coverCPlus = -1;
        coverCMinus = -1;
        coverEdgeC = null;
        coverEdgeCPlus = null;
        coverEdgeCMinus = new HashSet<>();
    }

    public twoEdgeConnectivityUserInfo(UserInfo info){
        twoEdgeConnectivityUserInfo uinfo = (twoEdgeConnectivityUserInfo) info;


        size3 = new HashMap<>();
        size4 = new HashMap<>();
        incident3 = new HashMap<>();
        incident4 = new HashMap<>();
        this.coverC = uinfo.coverC;
        this.coverCPlus = uinfo.coverCPlus;
        this.coverCMinus = uinfo.coverCMinus;
        this.coverEdgeC = uinfo.coverEdgeC;
        this.coverEdgeCPlus = uinfo.coverEdgeCPlus;
        this.coverEdgeCMinus = uinfo.coverEdgeCMinus;
    }


}
