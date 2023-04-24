import java.util.ArrayList;
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
            graphs.put(i, new Graph(numberOfVertices));
        }
    }

    private void updateBoundaries(Node t){
        // update boundary vertices
        twoEdgeConnectivityUserInfo userInfo = (twoEdgeConnectivityUserInfo) t.userInfo;

        if (t.isLeaf){

            LeafNode leaf = (LeafNode) t;
            Edge e = leaf.edge;
            if (t.numBoundary == 2){
                ArrayList<Vertex> boundaryVertices = new ArrayList<>();
                for (Vertex v : e.endpoints){
                    boundaryVertices.add(v);
                }
                userInfo.boundaryVertices = boundaryVertices;
                return;
            }
            ArrayList<Vertex> boundaryVertices = new ArrayList<>();
            for (Vertex v : e.endpoints){
                if (Tree.hasAtMostOneIncidentEdge(v)){
                    continue;
                } else {
                 boundaryVertices.add(v);
                }
            }
            userInfo.boundaryVertices = boundaryVertices;

        } else {
            InternalNode n = (InternalNode) t;
            ArrayList<Node> children = n.children;
            // TODO Temporarily
            updateBoundaries(children.get(0));
            updateBoundaries(children.get(1));

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

                } else {
                    // Both children are point clusters, but their shared vertex is a middle boundary vertex
                    userInfo.boundaryVertices = ((twoEdgeConnectivityUserInfo)children.get(1).userInfo).boundaryVertices;
                }
            }
        }


    }

    @Override
    public void combine(Node t) {
        updateBoundaries(t);
        // TODO TEMP
        if (!t.isLeaf){
            InternalNode temp = (InternalNode) t;
            //combine(temp.children.get(0));
            //combine(temp.children.get(1));
        }

        if (t.isLeaf){
            // TODO I think this is the desired values, but may have to be redone
            twoEdgeConnectivityUserInfo userInfo = (twoEdgeConnectivityUserInfo) t.userInfo;
            ArrayList<Vertex> boundary = userInfo.boundaryVertices;
            LeafNode leaf = (LeafNode) t;

            if (isPath(t)){
                twoEdgeVertexUserInfo b0 = (twoEdgeVertexUserInfo) boundary.get(0).userInfo;
                twoEdgeVertexUserInfo b1 = (twoEdgeVertexUserInfo) boundary.get(1).userInfo;
                for (Vertex v: userInfo.boundaryVertices){
                    HashMap<Integer, HashMap<Integer, Integer >> sizeList = new HashMap<>();
                    HashMap<Integer, HashMap<Integer, Integer >> incidentList = new HashMap<>();

                    //ArrayList<ArrayList<Integer>> sizeList = new ArrayList();
                    //ArrayList<ArrayList<Integer>> incidentList = new ArrayList();
                    for (int i = -1; i <= maxLevel; i++){
                        HashMap<Integer, Integer> tempSizeList = new HashMap<>();
                        HashMap<Integer, Integer> tempIncidentList = new HashMap<>();
                        for (int j = -1; j <= maxLevel; j++){
                            tempSizeList.put(j, b0.size2.get(i) + b1.size2.get(i)); // Unsure if this should be i
                            tempIncidentList.put(j, b0.incident2.get(i) + b1.incident2.get(i)); // Unsure if this should be i
                        }
                        sizeList.put(i, tempSizeList);
                        incidentList.put(i, tempIncidentList);
                    }
                    userInfo.size4.put(v, sizeList);
                    userInfo.incident4.put(v, incidentList);
                }
            } else if (boundary.size() == 1){
                //twoEdgeVertexUserInfo b0 = (twoEdgeVertexUserInfo) boundary.get(0).userInfo;
                twoEdgeVertexUserInfo b0 = (twoEdgeVertexUserInfo) leaf.edge.endpoints[0].userInfo;
                twoEdgeVertexUserInfo b1 = (twoEdgeVertexUserInfo) leaf.edge.endpoints[1].userInfo;

                HashMap<Integer, Integer> sizeList = new HashMap<>();
                HashMap<Integer, Integer> incidentList = new HashMap<>();

                for (int i = -1; i <= maxLevel; i++){
                    //sizeList.put(i, b0.size2.get(i)); // Should the other endpoint also be added?
                    //incidentList.put(i, b0.incident2.get(i)); // Should the other endpoint also be added?
                    sizeList.put(i, b0.size2.get(i) + b1.size2.get(i));
                    incidentList.put(i,b0.incident2.get(i) + b1.incident2.get(i));
                }
                userInfo.size3.put(boundary.get(0), sizeList);
                userInfo.incident3.put(boundary.get(0), incidentList);
            } else {
                //System.out.println("Does this happen?");
                // TODO
                // It will do nothing for now
            }
            return;
        }
        t.userInfo = new twoEdgeConnectivityUserInfo();
        updateBoundaries(t);
        // Handle internal nodes as described on page 67 of https://di.ku.dk/forskning/Publikationer/tekniske_rapporter/tekniske-rapporter-1998/98-17.pdf
        InternalNode n = (InternalNode) t;
        ArrayList<Node> children = n.children;

        twoEdgeConnectivityUserInfo userInfo = (twoEdgeConnectivityUserInfo) t.userInfo;

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

            if (isPath(children.get(0)) && isPath(children.get(1))){
                if (child0UserInfo.coverC < child1UserInfo.coverC){
                    userInfo.coverC = child0UserInfo.coverC;
                    userInfo.coverEdgeC = child0UserInfo.coverEdgeC;
                } else {
                    userInfo.coverC = child1UserInfo.coverC;
                    userInfo.coverEdgeC = child1UserInfo.coverEdgeC;
                }
            } else if (isPath(children.get(0))){
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

                HashMap<Integer, HashMap<Integer, Integer>> sizeList = new HashMap<>();
                HashMap<Integer, HashMap<Integer, Integer>> incidentList = new HashMap<>();
                for (int i = -1; i <= maxLevel; i++){
                    HashMap<Integer, Integer> tempSizeList = new HashMap<>();
                    HashMap<Integer, Integer> tempIncidentList = new HashMap<>();
                    for (int j = -1; j <= maxLevel; j++){
                        int tempSize = child0UserInfo.size3.get(a).get(j) + child1UserInfo.size4.get(a).get(i).get(j);
                        int tempIncident = child0UserInfo.incident3.get(a).get(j) + child1UserInfo.incident4.get(a).get(i).get(j);
                        tempSizeList.put(j, tempSize);
                        tempIncidentList.put(j, tempIncident);
                    }
                    sizeList.put(i, tempSizeList);
                    incidentList.put(i, tempIncidentList);
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

                HashMap<Integer, HashMap<Integer, Integer>> sizeList = new HashMap<>();
                HashMap<Integer, HashMap<Integer, Integer>> incidentList = new HashMap<>();
                for (int i = -1; i <= maxLevel; i++){
                    HashMap<Integer, Integer> tempSizeList = new HashMap<>();
                    HashMap<Integer, Integer> tempIncidentList = new HashMap<>();
                    for (int j = -1; j <= maxLevel; j++){
                        int tempSize = child0UserInfo.size4.get(a).get(i).get(j);
                        int tempIncident = child0UserInfo.incident4.get(a).get(i).get(j);
                        if (userInfo.coverC >= i){
                            tempSize += child1UserInfo.size3.get(c).get(j);
                            tempIncident += child1UserInfo.incident3.get(c).get(j);
                        }
                        tempSizeList.put(j, tempSize);
                        tempIncidentList.put(j, tempIncident);
                    }
                    sizeList.put(i, tempSizeList);
                    incidentList.put(i, tempIncidentList);
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

                HashMap<Integer, HashMap<Integer, Integer>> sizeList = new HashMap<>();
                HashMap<Integer, HashMap<Integer, Integer>> incidentList = new HashMap<>();
                for (int i = -1; i <= maxLevel; i++){
                    HashMap<Integer, Integer> tempSizeList = new HashMap<>();
                    HashMap<Integer, Integer> tempIncidentList = new HashMap<>();
                    for (int j = -1; j <= maxLevel; j++){
                        int tempSize = child0UserInfo.size4.get(a).get(i).get(j);
                        int tempIncident = child0UserInfo.incident4.get(a).get(i).get(j);
                        if (userInfo.coverC >= i){
                            twoEdgeVertexUserInfo info = (twoEdgeVertexUserInfo) c.userInfo;

                            tempSize += info.size2.get(j);
                            tempIncident += info.incident2.get(j);
                            tempSize += child1UserInfo.size4.get(c).get(i).get(j);
                            tempIncident += child1UserInfo.incident4.get(c).get(i).get(j);
                        }
                        tempSizeList.put(j, tempSize);
                        tempIncidentList.put(j, tempIncident);
                    }
                    sizeList.put(i, tempSizeList);
                    incidentList.put(i, tempIncidentList);
                }
                userInfo.size4.put(a, sizeList);
                userInfo.incident4.put(a, incidentList);
            }
            // Update vertex b
            if (isPoint(children.get(1))){
                // Point cluster, so the only boundary is b
                b = child1UserInfo.boundaryVertices.get(0);

                HashMap<Integer, HashMap<Integer, Integer>> sizeList = new HashMap<>();
                HashMap<Integer, HashMap<Integer, Integer>> incidentList = new HashMap<>();
                for (int i = -1; i <= maxLevel; i++){
                    HashMap<Integer, Integer> tempSizeList = new HashMap<>();
                    HashMap<Integer, Integer> tempIncidentList = new HashMap<>();
                    for (int j = -1; j <= maxLevel; j++){
                        int tempSize = child1UserInfo.size3.get(b).get(j) + child0UserInfo.size4.get(b).get(i).get(j);
                        int tempIncident = child1UserInfo.incident3.get(b).get(j) + child0UserInfo.incident4.get(b).get(i).get(j);
                        tempSizeList.put(j, tempSize);
                        tempIncidentList.put(j, tempIncident);
                    }
                    sizeList.put(i, tempSizeList);
                    incidentList.put(i, tempIncidentList);
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
                HashMap<Integer, HashMap<Integer, Integer>> sizeList = new HashMap<>();
                HashMap<Integer, HashMap<Integer, Integer>> incidentList = new HashMap<>();
                for (int i = -1; i <= maxLevel; i++){
                    HashMap<Integer, Integer> tempSizeList = new HashMap<>();
                    HashMap<Integer, Integer> tempIncidentList = new HashMap<>();
                    for (int j = -1; j <= maxLevel; j++){
                        int tempSize = child1UserInfo.size4.get(b).get(i).get(j);
                        int tempIncident = child1UserInfo.incident4.get(b).get(i).get(j);
                        if (userInfo.coverC >= i){
                            tempSize += child0UserInfo.size3.get(c).get(j);
                            tempIncident += child0UserInfo.incident3.get(c).get(j);
                        }
                        tempSizeList.put(j, tempSize);
                        tempIncidentList.put(j, tempIncident);
                    }
                    sizeList.put(i, tempSizeList);
                    incidentList.put(i, tempIncidentList);
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

                HashMap<Integer, HashMap<Integer, Integer>> sizeList = new HashMap<>();
                HashMap<Integer, HashMap<Integer, Integer>> incidentList = new HashMap<>();
                for (int i = -1; i <= maxLevel; i++){
                    HashMap<Integer, Integer> tempSizeList = new HashMap<>();
                    HashMap<Integer, Integer> tempIncidentList = new HashMap<>();
                    for (int j = -1; j <= maxLevel; j++){
                        int tempSize = child1UserInfo.size4.get(b).get(i).get(j);
                        int tempIncident = child1UserInfo.incident4.get(b).get(i).get(j);
                        if (userInfo.coverC >= i){
                            twoEdgeVertexUserInfo info = (twoEdgeVertexUserInfo) c.userInfo;

                            tempSize += info.size2.get(j);
                            tempIncident += info.incident2.get(j);
                            tempSize += child0UserInfo.size4.get(c).get(i).get(j);
                            tempIncident += child0UserInfo.incident4.get(c).get(i).get(j);
                        }
                        tempSizeList.put(j, tempSize);
                        tempIncidentList.put(j, tempIncident);
                    }
                    sizeList.put(i, tempSizeList);
                    incidentList.put(i, tempIncidentList);
                }
                userInfo.size4.put(b, sizeList);
                userInfo.incident4.put(b, incidentList);
            }


        } else if (t.numBoundary == 1){ // Not really required to check this, but w/e
            twoEdgeConnectivityUserInfo child0UserInfo = (twoEdgeConnectivityUserInfo) children.get(0).userInfo;
            twoEdgeConnectivityUserInfo child1UserInfo = (twoEdgeConnectivityUserInfo) children.get(1).userInfo;
            Vertex boundary = userInfo.boundaryVertices.get(0);

            if (children.get(0).numBoundary == 2) {
                HashMap<Integer, Integer> size = new HashMap<>();
                HashMap<Integer, Integer> incident = new HashMap<>();
                for (int j = -1; j <= maxLevel; j++){
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
                    size.put(j, tempSize);
                    incident.put(j, tempIncident);
                }
                userInfo.size3.put(boundary, size);
                userInfo.incident3.put(boundary, incident);

            } else if (children.get(1).numBoundary == 2) {
                // Copied from the above, might contain mistakes
                HashMap<Integer, Integer> size = new HashMap<>();
                HashMap<Integer, Integer> incident = new HashMap<>();
                for (int j = -1; j <= maxLevel; j++){
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
                    size.put(j, tempSize);
                    incident.put(j, tempIncident);
                }
                userInfo.size3.put(boundary, size);
                userInfo.incident3.put(boundary, incident);
            } else {
                HashMap<Integer, Integer> size = new HashMap<>();
                HashMap<Integer, Integer> incident = new HashMap<>();
                for (int j = -1; j <= maxLevel; j++){
                    int tempSize = child0UserInfo.size3.get(boundary).get(j) + child1UserInfo.size3.get(boundary).get(j);
                    int tempIncident = child0UserInfo.incident3.get(boundary).get(j) + child1UserInfo.incident3.get(boundary).get(j);
                    size.put(j, tempSize);
                    incident.put(j, tempIncident);
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

        if (info.coverC < i){
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
        // TODO using 0 instead of -1 for reasons
        for (int j = -1; j <= i; j++){
            for (int k = -1; k <= maxLevel; k++){
                // For v in boundary nodes
                for (Vertex v : info.boundaryVertices){
                    info.size4.get(v).get(j).put(k, info.size4.get(v).get(-1).get(k));
                    info.incident4.get(v).get(j).put(k, info.incident4.get(v).get(-1).get(k));
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
        // TODO using 0 instead of -1 for reasons
        for (int j = -1; j <= i; j++){
            for (int k = -1; k <= maxLevel; k++){
                // For v in boundary nodes
                for (Vertex v : info.boundaryVertices){
                    info.size4.get(v).get(j).put(k, info.size4.get(v).get(i+1).get(k));
                    info.incident4.get(v).get(j).put(k, info.incident4.get(v).get(i+1).get(k));
                }
            }
        }
    }

    private Edge find(Vertex u, Node c, int i){
        twoEdgeVertexUserInfo uinfo = (twoEdgeVertexUserInfo) u.userInfo;
        if (uinfo.incident2.get(i) > 0){
            return findIncidentEdge(u, c, i);
        } else {
            if (c.isLeaf) {
                Vertex otherBoundary = findOtherEndpoint(u, c);
                return find(otherBoundary, c, i);
            }

            clean(c);
            InternalNode internalNode = (InternalNode) c;
            ArrayList<Node> children = internalNode.children;

            twoEdgeConnectivityUserInfo c0 = (twoEdgeConnectivityUserInfo) children.get(0).userInfo;
            twoEdgeConnectivityUserInfo c1 = (twoEdgeConnectivityUserInfo) children.get(1).userInfo;

            if (c0.boundaryVertices.contains(u) && c1.boundaryVertices.contains(u)) {
                // u is boundary of both clusters
                if (isPoint(children.get(0)) && isPoint(children.get(1))){
                    if (c0.incident3.get(u).get(i) > 0) {
                        return find(u, children.get(0), i);
                    }
                    if (c1.incident3.get(u).get(i) > 0) {
                        return find(u, children.get(1), i);
                    }
                }
                if (isPoint(children.get(0))) {
                    // c0 is a point cluster
                    if (c0.incident3.get(u).get(i) > 0) {
                        return find(u, children.get(0), i);
                    }
                    // Find (b, B, i)
                    Vertex v = findNearestBoundary(u, c, i);
                    return find(v, children.get(1), i);
                } else {
                    // c1 is a point cluster

                    // Find (b, B, i)
                    Vertex v = findNearestBoundary(u, c, i);
                    return find(v, children.get(0), i);
                }
            } else if (c0.boundaryVertices.contains(u)) {
                // Only c0 contains u, and c0 is a path cluster
                if (c0.incident4.get(u).get(-1).get(i) > 0) {
                    return find(u, children.get(0), i);
                }
                Vertex v = findNearestBoundary(u, c, i);
                return find(v, children.get(1), i);
            } else {
                // Only c1 contains u, and c1 is a path cluster
                if (c1.incident4.get(u).get(-1).get(i) > 0) {
                    return find(u, children.get(1), i);
                }
                // Find (b, B, i)
                Vertex v = findNearestBoundary(u, c, i);
                return find(v, children.get(0), i);
            }
        }
    }

    private boolean bothIncidentZero(Vertex u, Node c, int i) {
        LeafNode leaf = (LeafNode) c;
        twoEdgeVertexUserInfo e0 = (twoEdgeVertexUserInfo) leaf.edge.endpoints[0].userInfo;
        twoEdgeVertexUserInfo e1 = (twoEdgeVertexUserInfo) leaf.edge.endpoints[0].userInfo;

        if (e0.incident2.get(i) <= 0 && e1.incident2.get(i) <= 0){
            return true;
        } else {
            return false;
        }
    }

    private Vertex findOtherBoundary(Vertex u, Node c) {
        twoEdgeConnectivityUserInfo userInfo = (twoEdgeConnectivityUserInfo) c.userInfo;
        int i = userInfo.boundaryVertices.lastIndexOf(u);
        return userInfo.boundaryVertices.get(1 - i);
    }

    private Vertex findOtherEndpoint(Vertex u, Node c){
        LeafNode leaf = (LeafNode) c;
        if (leaf.edge.endpoints[0] == u){
            return leaf.edge.endpoints[1];
        } else {
            return leaf.edge.endpoints[0];
        }
    }

    private Vertex findNearestBoundary(Vertex u, Node c, int i) {
        /*if (isPoint(c)){
            // Retrieve the sibling cluster
            InternalNode parent = c.parent;
            Node sibling = parent.children.get(1 - parent.children.indexOf(c));
            c = sibling;
        }*/

        InternalNode internalNode = (InternalNode) c;
        ArrayList<Node> children = internalNode.children;

        twoEdgeConnectivityUserInfo c0 = (twoEdgeConnectivityUserInfo) children.get(0).userInfo;
        twoEdgeConnectivityUserInfo c1 = (twoEdgeConnectivityUserInfo) children.get(1).userInfo;



        if (c0.boundaryVertices.contains(u) && c1.boundaryVertices.contains(u)){
            if (c0.boundaryVertices.size() > 1){
                // c0 have two boundaries
                int index = c0.boundaryVertices.lastIndexOf(u);
                return c0.boundaryVertices.get(1-index);
            } else {
                // c1 have two boundaries
                int index = c1.boundaryVertices.lastIndexOf(u);
                return c1.boundaryVertices.get(1-index);
            }
        } else if (c0.boundaryVertices.contains(u)) {
            // get boundary vertex from c1
            // c0 have two boundaries, get the shared vertex
            int index = c0.boundaryVertices.lastIndexOf(u);
            return c0.boundaryVertices.get(1-index);
        } else {
            // get boundary vertex from c0
            // c1 have two boundaries, get the shared vertex
            int index = c1.boundaryVertices.lastIndexOf(u);
            return c1.boundaryVertices.get(1-index);
        }

    }

    private Edge findIncidentEdge(Vertex u, Node c, int i) {
        Graph g = graphs.get(i);
        return g.incidentEdge(u.id);

    }

    private void recoverInner(Vertex v, Vertex w, Vertex u, int i){
        // Expose v, w and retrieve the root
        expose(v);
        expose(w);
        Node c = findRoot(v.firstEdge.userData);
        computeAllCombine(c);
        twoEdgeConnectivityUserInfo cinfo = (twoEdgeConnectivityUserInfo) c.userInfo;
        twoEdgeVertexUserInfo uinfo = (twoEdgeVertexUserInfo) u.userInfo;
        // deExpose, so we can expose new vertices in the while loop
        boolean notStopped = true;
        while (cinfo.incident4.get(u).get(0).get(i) + uinfo.incident2.get(i) > 0 && notStopped){
            Edge e = find(u, c, i);
            deExpose(v);
            deExpose(w);

            Vertex q = e.endpoints[0];
            Vertex r = e.endpoints[1];

            expose(q);
            expose(r);
            Node d = findRoot(q.firstEdge.userData);
            twoEdgeConnectivityUserInfo dinfo = (twoEdgeConnectivityUserInfo) d.userInfo;

            if (dinfo.size4.get(q).get(-1).get(i) + 2 > numberOfVertices/(2^i)){
                cover(d, i, e);
                pushDownInfo(d);
                notStopped = false;
            } else {
                twoEdgeVertexUserInfo qinfo = (twoEdgeVertexUserInfo) q.userInfo;
                twoEdgeVertexUserInfo rinfo = (twoEdgeVertexUserInfo) r.userInfo;

                increaseLevel(e, i, i+1);

                // This may be the wrong counter getting changed
                qinfo.incident2.put(i, qinfo.incident2.get(i) - 1);
                qinfo.incident2.put(i + 1, qinfo.incident2.get(i + 1) + 1);

                rinfo.incident2.put(i, rinfo.incident2.get(i) - 1);
                rinfo.incident2.put(i + 1, rinfo.incident2.get(i + 1) + 1);

                cover(d, i + 1, e);
                pushDownInfo(d);

            }
            Node da = findRoot(q.firstEdge.userData);

            deExpose(q);
            deExpose(r);

            // ???
            expose(v);
            expose(w);
            c = findRoot(v.firstEdge.userData);
            pushDownInfo(c);
        }
        deExpose(v);
        deExpose(w);
    }

    /*
    * Method to change the level of edges, and update the graphs
    */
    private void increaseLevel(Edge e, int levelFrom, int levelTo) {
        for (int i = levelFrom; i <= levelTo; i++){
            graphs.get(i).addEdge(e);
        }
    }

    private void recover(Vertex v, Vertex w, int i){
        recoverInner(v, w, v, i);
        recoverInner(v, w, w, i);
    }

    private void swap(Vertex u, Vertex v){
        expose(u);
        expose(v);
        Node c = findRoot(u.firstEdge.userData);
        twoEdgeConnectivityUserInfo userInfo = (twoEdgeConnectivityUserInfo) c.userInfo;
        deExpose(v);
        deExpose(u);
        Edge coverEdge = userInfo.coverEdgeC;

        if (userInfo.coverC >= 0){
            for (int i = 0; i <= userInfo.coverC; i++){
                graphs.get(i).removeEdge(coverEdge);
                graphs.get(i).addEdge(u.id, v.id);
                //graphs.get(i).removeEdge(u.id, v.id);
                //graphs.get(i).addEdge(userInfo.coverEdgeC);

                // Update incident numbers
                twoEdgeVertexUserInfo uinfo = (twoEdgeVertexUserInfo) u.userInfo;
                twoEdgeVertexUserInfo vinfo = (twoEdgeVertexUserInfo) v.userInfo;
                uinfo.incident2.put(i, uinfo.incident2.get(i) + 1);
                vinfo.incident2.put(i, vinfo.incident2.get(i) + 1);

                twoEdgeVertexUserInfo qinfo = (twoEdgeVertexUserInfo) coverEdge.endpoints[0].userInfo;
                twoEdgeVertexUserInfo rinfo = (twoEdgeVertexUserInfo) coverEdge.endpoints[1].userInfo;
                qinfo.incident2.put(i, qinfo.incident2.get(i) - 1);
                rinfo.incident2.put(i, rinfo.incident2.get(i) - 1);

            }
            Edge e = Tree.adjacencyList[u.id][v.id];
            cut(e);
            //System.out.println("Swapping edge " + u.id + v.id + " with " + userInfo.coverEdgeC.endpoints[0].id + userInfo.coverEdgeC.endpoints[1].id);
            link(userInfo.coverEdgeC.endpoints[0], userInfo.coverEdgeC.endpoints[1], 1);
            System.out.println("Swapped edge " + u.id + v.id + " with " + userInfo.coverEdgeC.endpoints[0].id + userInfo.coverEdgeC.endpoints[1].id);
        }


    }

    public void insert(Vertex u, Vertex v){
        if (u.firstEdge != null && v.firstEdge != null && findRoot(u.firstEdge.userData).equals(findRoot(v.firstEdge.userData))){
            // u & v is in the same top tree already
            Edge e = new Edge();
            e.endpoints[0] = u;
            e.endpoints[1] = v;
            e.weight = 1;
            graphs.get(0).addEdge(e);

            coverReal(u, v, 0);

            twoEdgeVertexUserInfo vinfo = (twoEdgeVertexUserInfo) v.userInfo;
            twoEdgeVertexUserInfo uinfo = (twoEdgeVertexUserInfo) u.userInfo;
            vinfo.incident2.put(0, vinfo.incident2.get(0) + 1);
            uinfo.incident2.put(0, uinfo.incident2.get(0) + 1);
            System.out.println("Insterted " + u.id + v.id + " into the cover edges");

        } else {
            link(u,v,1);
            System.out.println("Insterted " + u.id + v.id + " into the spanning tree");
        }
    }

    public void delete(Vertex u, Vertex v){
        Edge e = null;

        if (Tree.adjacencyList[u.id][v.id] != null) {
            e = Tree.adjacencyList[u.id][v.id];
        } else {
            // (u,v) is not in the spanning tree
            int i = findLevel(u, v);
            uncoverReal(u, v, i);
            deleteEdge(u, v, i);
            recover(u,v,i);
            return;
        }

        // Check if (u,v) is tree edge
        LeafNode c = (LeafNode) e.userData;
        twoEdgeConnectivityUserInfo userInfo = (twoEdgeConnectivityUserInfo) c.userInfo;


        // If tree edge, and bridge remove it
        if (userInfo.coverC == -1){
            // if coverC == -1, we have no edges covering us and we can safely remove it
            cut(c.edge);
        } else {
            int i = userInfo.coverC;
            // It is not a bridge
            // swap, uncover, recover, finally delete the edge
            swap(u, v);
            uncoverReal(u, v, i);
            deleteEdge(u, v, i);
            recover(u, v, i);
        }

        Node d = findRoot(c);
    }

    private int findLevel(Vertex u, Vertex v) {
        for (int i = maxLevel; i >= 0; i--){
            if (graphs.get(i).containEdge(u.id, v.id)) {
                return i;
            }
        }
        return -1;
    }

    private void deleteEdge(Vertex u, Vertex v, int i) {
        for (int j = 0; j <= i; j++){
            graphs.get(j).removeEdge(u.id, v.id);
            twoEdgeVertexUserInfo uinfo = (twoEdgeVertexUserInfo) u.userInfo;
            twoEdgeVertexUserInfo vinfo = (twoEdgeVertexUserInfo) v.userInfo;
            uinfo.incident2.put(i, uinfo.incident2.get(i) - 1);
            vinfo.incident2.put(i, vinfo.incident2.get(i) - 1);
        }
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
        if (n.isLeaf){
            return;
        }
        twoEdgeConnectivityUserInfo info = (twoEdgeConnectivityUserInfo) n.userInfo;

        InternalNode internalNode = (InternalNode) n;

        ArrayList<Node> children = internalNode.children;

        for(int i = 0; i < 2; i++){
            if (isPath(children.get(i))){
                uncover(children.get(i), info.coverCMinus);
                cover(children.get(i), info.coverCPlus, info.coverEdgeCPlus);
            }
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
        // A bit scuffed way of getting the edge
        cover(root, i, graphs.get(0).getEdge(u,v));
        pushDownInfo(root); // This makes it work, but likely breaks the runtime too
        //clean(root);
        deExpose(u);
        deExpose(v);
    }

    private void pushDownInfo(Node n) {
        clean(n);
        if (n.isLeaf){
            return;
        } else {
            InternalNode internalNode = (InternalNode) n;

            ArrayList<Node> children = internalNode.children;

            for(int i = 0; i < 2; i++){
                pushDownInfo(children.get(i));
            }
        }
    }

    public void uncoverReal(Vertex u, Vertex v, int i){
        // The implementations of uncover on page 66
        // https://di.ku.dk/forskning/Publikationer/tekniske_rapporter/tekniske-rapporter-1998/98-17.pdf
        Node root1 = expose(u);
        Node root2 = expose(v);
        if (root1 != root2){
            System.out.println("u and v are not in the same tree");
        }
        Node root = findRoot(u.firstEdge.userData);
        uncover(root, i);
        pushDownInfo(root); // TEMP
        deExpose(u);
        deExpose(v);
    }

    public void computeAllCombine(Node n){
        if (n.isLeaf){
            combine(n);
        } else {
            InternalNode node = (InternalNode) n;
            computeAllCombine(node.children.get(0));
            computeAllCombine(node.children.get(1));
            combine(n);
        }
    }

    public boolean findBridge(Tree t){
        // Virker ikke
        Vertex v = t.vertex.get(0);
        expose(v);
        Node c = findRoot(v.firstEdge.userData);
        twoEdgeConnectivityUserInfo userInfo = (twoEdgeConnectivityUserInfo) c.userInfo;

        return userInfo.coverC >= 0;
    }

}
