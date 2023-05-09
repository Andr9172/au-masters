import java.lang.reflect.Array;
import java.util.*;

public class Graph {

    int numberOfVertices;

    //public Edge[][] adjacencyList;
    public HashSet<Edge> edgeList;
    public ArrayList<PriorityQueue<Integer>> list;

    public Graph(int numberOfVertices){
        this.numberOfVertices = numberOfVertices;

        //adjacencyList = new Edge[numberOfVertices][numberOfVertices];
        edgeList = new HashSet<>();
        list = new ArrayList<>();
        for (int i = 0; i < numberOfVertices; i++){
            list.add(i, new PriorityQueue<>());
        }
    }

    public void addEdge(Edge e){
        int i = e.endpoints[0].id;
        int j = e.endpoints[1].id;
        edgeList.add(e);

        list.get(i).add(j);
        list.get(j).add(i);
        //adjacencyList[i][j] = e;
        //adjacencyList[j][i] = e;
    }

    public void addEdge(int i, int j){
        Edge e = new Edge(i , j);
        edgeList.add(e);

        list.get(i).add(j);
        list.get(j).add(i);
        //adjacencyList[i][j] = e;
        //adjacencyList[j][i] = e;
    }

    public void removeEdge(Edge e){
        int i = e.endpoints[0].id;
        int j = e.endpoints[1].id;
        edgeList.remove(e);

        list.get(i).remove(j);
        list.get(j).remove(i);
        //adjacencyList[i][j] = null;
        //adjacencyList[j][i] = null;
    }

    public void removeEdge(int i, int j){
        Edge e = new Edge(i , j);
        edgeList.add(e);

        list.get(i).remove(j);
        list.get(j).remove(i);
        //adjacencyList[i][j] = null;
        //adjacencyList[j][i] = null;
    }

    public Edge getEdge(Vertex u, Vertex v){
        Edge e = new Edge(u.id , v.id);
        edgeList.add(e);
        if (edgeList.contains(e)){
            return e;
        }
        else {
            return null;
        }

        //return adjacencyList[u.id][v.id];
    }


    public boolean containEdge(int i, int j) {
        Edge e = new Edge(i , j);
        edgeList.add(e);
        if (edgeList.contains(e)){
            return true;
        }
        else {
            return false;
        }
        //return adjacencyList[i][j] != null;
    }

    public Edge incidentEdge(int id) {
        //System.out.println("Find edge incident to " + id);
        //System.out.println("Possible edges: " + Arrays.toString(adjacencyList[id]));
        /*for (int i = 0; i < numberOfVertices; i++){
            if (adjacencyList[id][i] != null){
                return adjacencyList[id][i];
            }
        }*/
        /*for (Edge e : edgeList){
            if (e.endpoints[0].id == id || e.endpoints[1].id == id ){
                return e;
            }
        }*/
        Integer temp = list.get(id).peek();
        if (temp != null){
            return new Edge(id, temp);
        } else {
            return null;
        }
    }

}