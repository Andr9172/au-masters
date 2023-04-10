import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class twoEdgeConnectivityTopTree implements TopTreeInterface {

    public HashMap<Integer, Graph> graphs;


    int numberOfVertices = 0;
    int maxLevel = 0;

    public twoEdgeConnectivityTopTree(int numberOfVertices) {
        this.numberOfVertices = numberOfVertices;
        this.maxLevel = (int) Math.ceil(Math.log(numberOfVertices));

        // Instantiate the graphs, and make sure they are there so we can add/remove from them
        graphs = new HashMap<>();
        for (int i = 0; i <= maxLevel; i++){
            graphs.put(i, new Graph());
        }
    }

    @Override
    public void combine(Node t) {
        if (t.isLeaf){
            // TODO What to do when t is a leaf node?

            return;
        }

        // Handle internal nodes as described on page 67 of https://di.ku.dk/forskning/Publikationer/tekniske_rapporter/tekniske-rapporter-1998/98-17.pdf
        InternalNode n = (InternalNode) t;
        ArrayList<Node> children = n.children;

        twoEdgeConnectivityUserInfo userInfo = (twoEdgeConnectivityUserInfo) t.userInfo;
        // update boundary vertices
        if (isPath(t)){
            if (isPath(children.get(0)) && isPath(children.get(1))){
                // Boundary vertices belong to different clusters, and needs to be without the shared one
                twoEdgeConnectivityUserInfo c0 =  (twoEdgeConnectivityUserInfo)children.get(0).userInfo;
                twoEdgeConnectivityUserInfo c1 =  (twoEdgeConnectivityUserInfo)children.get(1).userInfo;
                // Is the shared vertex located on the first or second index of c1.boundaryvertices
                boolean index0 = c0.boundaryVertices.contains(c1.boundaryVertices.get(0));
                // Add all the boundary vertices of c0
                ArrayList<Vertex> boundaries = new ArrayList<>(c0.boundaryVertices);
                if (index0) {
                    // Remove the shared vertex, and add the other boundary
                    boundaries.remove(c1.boundaryVertices.get(0));
                    boundaries.add(c1.boundaryVertices.get(1));
                } else {
                    // Remove the shared vertex, and add the other boundary
                    boundaries.remove(c1.boundaryVertices.get(1));
                    boundaries.add(c1.boundaryVertices.get(0));
                }
                userInfo.boundaryVertices = boundaries;

            } else if (isPath(children.get(0)) && isPoint(children.get(1))) {
                // All boundary vertices belong to the path cluster
                userInfo.boundaryVertices = ((twoEdgeConnectivityUserInfo)children.get(0).userInfo).boundaryVertices;
            } else if (isPoint(children.get(0)) && isPath(children.get(1))) {
                // All boundary vertices belong to the path cluster
                userInfo.boundaryVertices = ((twoEdgeConnectivityUserInfo)children.get(1).userInfo).boundaryVertices;
            }
        } else if (isPoint(t)) {
            if (isPath(children.get(0)) && isPoint(children.get(1))) {
                // All boundary vertices belong to the path cluster, minus the shared vertex
                userInfo.boundaryVertices = ((twoEdgeConnectivityUserInfo)children.get(0).userInfo).boundaryVertices;
                userInfo.boundaryVertices.remove(((twoEdgeConnectivityUserInfo)children.get(1).userInfo).boundaryVertices.get(0));
            } else if (isPoint(children.get(0)) && isPath(children.get(1))) {
                // All boundary vertices belong to the path cluster, minus the shared vertex
                userInfo.boundaryVertices = ((twoEdgeConnectivityUserInfo)children.get(1).userInfo).boundaryVertices;
                userInfo.boundaryVertices.remove(((twoEdgeConnectivityUserInfo)children.get(0).userInfo).boundaryVertices.get(0));

            }
        }

        // update counters
        if (isPath(t)){
            // Boundary vertices a from 0'th, b from 1st c is the shared one
            Vertex a;
            Vertex b;
            Vertex c;

            twoEdgeConnectivityUserInfo c0 =  (twoEdgeConnectivityUserInfo)children.get(0).userInfo;
            twoEdgeConnectivityUserInfo c1 =  (twoEdgeConnectivityUserInfo)children.get(1).userInfo;
            // Is the shared vertex located on the first or second index of c1.boundaryvertices
            boolean index0 = c0.boundaryVertices.contains(c1.boundaryVertices.get(0));
            // Add all the boundary vertices of c0
            if (index0){
                c = c1.boundaryVertices.get(0);
            } else {
                c = c1.boundaryVertices.get(1);
            }



            twoEdgeConnectivityUserInfo child0UserInfo = (twoEdgeConnectivityUserInfo) children.get(0).userInfo;
            twoEdgeConnectivityUserInfo child1UserInfo = (twoEdgeConnectivityUserInfo) children.get(1).userInfo;

            if (child0UserInfo.coverC < child1UserInfo.coverC){
                userInfo.coverC = child0UserInfo.coverC;
                userInfo.coverEdgeC = child0UserInfo.coverEdgeC;
            } else {
                userInfo.coverC = child1UserInfo.coverC;
                userInfo.coverEdgeC = child1UserInfo.coverEdgeC;
            }
            userInfo.coverEdgeCPlus = null;
            userInfo.coverCMinus = -1;
            userInfo.coverCPlus = -1;

            // Update vertex a
            if (isPoint(children.get(0))){
                // Point cluster, so the only boundary is a
                a = child0UserInfo.boundaryVertices.get(0);

                ArrayList<ArrayList<Integer>> sizeList = new ArrayList();
                ArrayList<ArrayList<Integer>> incidentList = new ArrayList();
                for (int i = 0; i <= maxLevel; i++){
                    ArrayList<Integer> tempSizeList = new ArrayList<>();
                    ArrayList<Integer> tempIncidentList = new ArrayList<>();
                    for (int j = 0; j <= maxLevel; j++){
                        int tempSize = child0UserInfo.size3.get(a).get(j) + child1UserInfo.size4.get(a).get(i).get(j);
                        int tempIncident = child0UserInfo.incident3.get(a).get(j) + child1UserInfo.incident4.get(a).get(i).get(j);
                        tempSizeList.add(j, tempSize);
                        tempIncidentList.add(j, tempIncident);
                    }
                    sizeList.add(i, tempSizeList);
                    incidentList.add(i, tempIncidentList);
                }
                userInfo.size4.put(a, sizeList);
                userInfo.incident4.put(a, incidentList);
            } else if (isPoint(children.get(1))) {
                // Check where the shared boundary vertex is
                boolean indexa = child0UserInfo.boundaryVertices.get(0) == child1UserInfo.boundaryVertices.get(0);
                if (indexa) {
                    a = child0UserInfo.boundaryVertices.get(1);
                } else {
                    a = child0UserInfo.boundaryVertices.get(0);
                }

                ArrayList<ArrayList<Integer>> sizeList = new ArrayList();
                ArrayList<ArrayList<Integer>> incidentList = new ArrayList();
                for (int i = 0; i <= maxLevel; i++){
                    ArrayList<Integer> tempSizeList = new ArrayList<>();
                    ArrayList<Integer> tempIncidentList = new ArrayList<>();
                    for (int j = 0; j <= maxLevel; j++){
                        int tempSize = child0UserInfo.size4.get(a).get(i).get(j);
                        int tempIncident = child0UserInfo.incident4.get(a).get(i).get(j);
                        if (userInfo.coverC >= i){
                            tempSize += child1UserInfo.size3.get(c).get(j);
                            tempIncident += child1UserInfo.incident3.get(c).get(j);
                        }
                        tempSizeList.add(j, tempSize);
                        tempIncidentList.add(j, tempIncident);
                    }
                    sizeList.add(i, tempSizeList);
                    incidentList.add(i, tempIncidentList);
                }
                userInfo.size4.put(a, sizeList);
                userInfo.incident4.put(a, incidentList);
            } else {
                // Find which boundary vertex is also in child1
                boolean indexa =  child1UserInfo.boundaryVertices.contains(child0UserInfo.boundaryVertices.get(0));
                if (indexa) {
                    a = child0UserInfo.boundaryVertices.get(1);
                } else {
                    a = child0UserInfo.boundaryVertices.get(0);
                }

                ArrayList<ArrayList<Integer>> sizeList = new ArrayList();
                ArrayList<ArrayList<Integer>> incidentList = new ArrayList();
                for (int i = 0; i <= maxLevel; i++){
                    ArrayList<Integer> tempSizeList = new ArrayList<>();
                    ArrayList<Integer> tempIncidentList = new ArrayList<>();
                    for (int j = 0; j <= maxLevel; j++){
                        int tempSize = child0UserInfo.size4.get(a).get(i).get(j);
                        int tempIncident = child0UserInfo.incident4.get(a).get(i).get(j);
                        if (userInfo.coverC >= i){
                            twoEdgeVertexUserInfo info = (twoEdgeVertexUserInfo) c.userInfo;

                            tempSize += info.size2.get(j);
                            tempIncident += info.incident2.get(j);
                            tempSize += child1UserInfo.size4.get(c).get(i).get(j);
                            tempIncident += child1UserInfo.incident4.get(c).get(i).get(j);
                        }
                        tempSizeList.add(j, tempSize);
                        tempIncidentList.add(j, tempIncident);
                    }
                    sizeList.add(i, tempSizeList);
                    incidentList.add(i, tempIncidentList);
                }
                userInfo.size4.put(a, sizeList);
                userInfo.incident4.put(a, incidentList);
            }
            // Update vertex b
            if (isPoint(children.get(1))){
                // Point cluster, so the only boundary is b
                b = child1UserInfo.boundaryVertices.get(0);

                ArrayList<ArrayList<Integer>> sizeList = new ArrayList();
                ArrayList<ArrayList<Integer>> incidentList = new ArrayList();
                for (int i = 0; i <= maxLevel; i++){
                    ArrayList<Integer> tempSizeList = new ArrayList<>();
                    ArrayList<Integer> tempIncidentList = new ArrayList<>();
                    for (int j = 0; j <= maxLevel; j++){
                        int tempSize = child1UserInfo.size3.get(b).get(j) + child0UserInfo.size4.get(b).get(i).get(j);
                        int tempIncident = child1UserInfo.incident3.get(b).get(j) + child0UserInfo.incident4.get(b).get(i).get(j);
                        tempSizeList.add(j, tempSize);
                        tempIncidentList.add(j, tempIncident);
                    }
                    sizeList.add(i, tempSizeList);
                    incidentList.add(i, tempIncidentList);
                }
                userInfo.size4.put(b, sizeList);
                userInfo.incident4.put(b, incidentList);
            } else if (isPoint(children.get(0))) {
                // Check where the shared boundary vertex is
                boolean indexb = child1UserInfo.boundaryVertices.get(0) == child0UserInfo.boundaryVertices.get(0);
                if (indexb) {
                    b = child1UserInfo.boundaryVertices.get(1);
                } else {
                    b = child1UserInfo.boundaryVertices.get(0);
                }
                ArrayList<ArrayList<Integer>> sizeList = new ArrayList();
                ArrayList<ArrayList<Integer>> incidentList = new ArrayList();
                for (int i = 0; i <= maxLevel; i++){
                    ArrayList<Integer> tempSizeList = new ArrayList<>();
                    ArrayList<Integer> tempIncidentList = new ArrayList<>();
                    for (int j = 0; j <= maxLevel; j++){
                        int tempSize = child1UserInfo.size4.get(b).get(i).get(j);
                        int tempIncident = child1UserInfo.incident4.get(b).get(i).get(j);
                        if (userInfo.coverC >= i){
                            tempSize += child0UserInfo.size3.get(c).get(j);
                            tempIncident += child0UserInfo.incident3.get(c).get(j);
                        }
                        tempSizeList.add(j, tempSize);
                        tempIncidentList.add(j, tempIncident);
                    }
                    sizeList.add(i, tempSizeList);
                    incidentList.add(i, tempIncidentList);
                }
                userInfo.size4.put(b, sizeList);
                userInfo.incident4.put(b, incidentList);
            } else {
                // Find which boundary vertex is also in child1
                boolean indexb =  child0UserInfo.boundaryVertices.contains(child1UserInfo.boundaryVertices.get(0));
                if (indexb) {
                    b = child1UserInfo.boundaryVertices.get(1);
                } else {
                    b = child1UserInfo.boundaryVertices.get(0);
                }

                ArrayList<ArrayList<Integer>> sizeList = new ArrayList();
                ArrayList<ArrayList<Integer>> incidentList = new ArrayList();
                for (int i = 0; i <= maxLevel; i++){
                    ArrayList<Integer> tempSizeList = new ArrayList<>();
                    ArrayList<Integer> tempIncidentList = new ArrayList<>();
                    for (int j = 0; j <= maxLevel; j++){
                        int tempSize = child1UserInfo.size4.get(b).get(i).get(j);
                        int tempIncident = child1UserInfo.incident4.get(b).get(i).get(j);
                        if (userInfo.coverC >= i){
                            twoEdgeVertexUserInfo info = (twoEdgeVertexUserInfo) c.userInfo;

                            tempSize += info.size2.get(j);
                            tempIncident += info.incident2.get(j);
                            tempSize += child0UserInfo.size4.get(c).get(i).get(j);
                            tempIncident += child0UserInfo.incident4.get(c).get(i).get(j);
                        }
                        tempSizeList.add(j, tempSize);
                        tempIncidentList.add(j, tempIncident);
                    }
                    sizeList.add(i, tempSizeList);
                    incidentList.add(i, tempIncidentList);
                }
                userInfo.size4.put(b, sizeList);
                userInfo.incident4.put(b, incidentList);
            }


        } else if (isPoint(t)){ // Not really required to check this, but w/e
            twoEdgeConnectivityUserInfo child0UserInfo = (twoEdgeConnectivityUserInfo) children.get(0).userInfo;
            twoEdgeConnectivityUserInfo child1UserInfo = (twoEdgeConnectivityUserInfo) children.get(1).userInfo;
            Vertex boundary = userInfo.boundaryVertices.get(0);

            if (children.get(0).numBoundary == 2) {
                ArrayList<Integer> size = new ArrayList<>();
                ArrayList<Integer> incident = new ArrayList<>();
                for (int j = 0; j <= maxLevel; j++){
                    int tempSize = child0UserInfo.size4.get(boundary).get(j).get(j);
                    int tempIncident = child0UserInfo.incident4.get(boundary).get(j).get(j);
                    if (child0UserInfo.coverC >= j){
                        Vertex tempVertex = child1UserInfo.boundaryVertices.get(0);
                        twoEdgeVertexUserInfo info = (twoEdgeVertexUserInfo) tempVertex.userInfo;

                        tempSize += info.size2.get(j);
                        tempIncident += info.incident2.get(j);
                        tempSize += child1UserInfo.size3.get(tempVertex).get(j);
                        tempIncident += child1UserInfo.incident3.get(tempVertex).get(j);
                    }
                    size.add(tempSize);
                    incident.add(tempIncident);
                }
                userInfo.size3.put(boundary, size);
                userInfo.incident3.put(boundary, incident);

            } else if (children.get(1).numBoundary == 2) {
                // Copied from the above, might contain mistakes
                ArrayList<Integer> size = new ArrayList<>();
                ArrayList<Integer> incident = new ArrayList<>();
                for (int j = 0; j <= maxLevel; j++){
                    int tempSize = child1UserInfo.size4.get(boundary).get(j).get(j);
                    int tempIncident = child1UserInfo.incident4.get(boundary).get(j).get(j);
                    if (child1UserInfo.coverC >= j){
                        Vertex tempVertex = child0UserInfo.boundaryVertices.get(0);
                        twoEdgeVertexUserInfo info = (twoEdgeVertexUserInfo) tempVertex.userInfo;

                        tempSize += info.size2.get(j);
                        tempIncident += info.incident2.get(j);
                        tempSize += child0UserInfo.size3.get(tempVertex).get(j);
                        tempIncident += child0UserInfo.incident3.get(tempVertex).get(j);
                    }
                    size.add(tempSize);
                    incident.add(tempIncident);
                }
                userInfo.size3.put(boundary, size);
                userInfo.incident3.put(boundary, incident);
            } else {
                ArrayList<Integer> size = new ArrayList<>();
                ArrayList<Integer> incident = new ArrayList<>();
                for (int j = 0; j <= maxLevel; j++){
                    int tempSize = child0UserInfo.size3.get(boundary).get(j) + child1UserInfo.size3.get(boundary).get(j);
                    int tempIncident = child0UserInfo.incident3.get(boundary).get(j) + child1UserInfo.incident3.get(boundary).get(j);
                    size.add(tempSize);
                    incident.add(tempIncident);
                }
                userInfo.size3.put(boundary, size);
                userInfo.incident3.put(boundary, incident);
            }
        }
    }

    @Override
    public UserInfo computeCombine(Node t) {
        combine(t);
        return t.userInfo;
    }

    @Override
    public Node search(Node root) {
        return null;
    }

    @Override
    public UserInfo newUserInfo() {
        return new twoEdgeConnectivityUserInfo();
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
                for (Vertex v : info.boundaryVertices){
                    info.size4.get(v).get(j).set(k, info.size4.get(v).get(-1).get(k));
                    info.incident4.get(v).get(j).set(k, info.incident4.get(v).get(-1).get(k));
                }
            }
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
                for (Vertex v : info.boundaryVertices){
                    info.size4.get(v).get(j).set(k, info.size4.get(v).get(i+1).get(k));
                    info.incident4.get(v).get(j).set(k, info.incident4.get(v).get(i+1).get(k));
                }
            }
        }

    }

    private void recover(Vertex v, Vertex w, int i){

    }

    private void swap(){
        // ???
    }

    public void insert(){

    }

    public void delete(Node n){
        clean(n);
        // Delete c
    }

    public void split(Node n){
        clean(n);
        // Delete C
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

    // Query for result
    public boolean twoEdgeConnected(Vertex u, Vertex v){
        expose(u);
        expose(v);
        Node root = findRoot(u.firstEdge.userData);
        twoEdgeConnectivityUserInfo userinfo = (twoEdgeConnectivityUserInfo) root.userInfo;
        boolean result = userinfo.coverC >= 0;
        deExpose(u);
        deExpose(v);
        return result;
    }


    public void coverReal(Vertex u, Vertex v, int i){
        // The implementations of cover on page 66
        // https://di.ku.dk/forskning/Publikationer/tekniske_rapporter/tekniske-rapporter-1998/98-17.pdf
        expose(u);
        expose(v);
        Node root = findRoot(u.firstEdge.userData);
        // TODO this call needs the edge, how do we get that
        // cover(root, i);
        deExpose(u);
        deExpose(v);
    }

    public void uncoverReal(Vertex u, Vertex v, int i){
        // The implementations of uncover on page 66
        // https://di.ku.dk/forskning/Publikationer/tekniske_rapporter/tekniske-rapporter-1998/98-17.pdf
        expose(u);
        expose(v);
        Node root = findRoot(u.firstEdge.userData);
        uncover(root, i);
        deExpose(u);
        deExpose(v);
    }

}
