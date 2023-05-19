import java.lang.reflect.Array;
import java.util.*;

public class Graph {

    int numberOfVertices;

    public Edge[][] adjacencyList;
    public ArrayList<HashMap<Integer, Edge>> list;

    public Graph(int numberOfVertices){
        this.numberOfVertices = numberOfVertices;

        adjacencyList = new Edge[numberOfVertices][numberOfVertices];
        list = new ArrayList<>();
        for (int i = 0; i < numberOfVertices; i++){
            list.add(new HashMap<>());
        }


    }

    public void addEdge(Edge e){
        int i = e.endpoints[0].id;
        int j = e.endpoints[1].id;

        list.get(i).put(j, e);
        list.get(j).put(i, e);

        adjacencyList[i][j] = e;
        adjacencyList[j][i] = e;
    }

    public void addEdge(int i, int j){
        Edge e = new Edge(i , j);

        list.get(i).put(j, e);
        list.get(j).put(i, e);

        adjacencyList[i][j] = e;
        adjacencyList[j][i] = e;
    }

    public void removeEdge(Edge e){
        int i = e.endpoints[0].id;
        int j = e.endpoints[1].id;

        list.get(i).remove(j);
        list.get(j).remove(i);

        adjacencyList[i][j] = null;
        adjacencyList[j][i] = null;
    }

    public void removeEdge(int i, int j){
        Edge e = new Edge(i , j);

        list.get(i).remove(j);
        list.get(j).remove(i);

        adjacencyList[i][j] = null;
        adjacencyList[j][i] = null;
    }

    public Edge getEdge(Vertex u, Vertex v){
        Edge e = new Edge(u.id , v.id);

        //return e;

        return adjacencyList[u.id][v.id];
    }


    public boolean containEdge(int i, int j) {
        Edge e = new Edge(i , j);

        //return list.get(i).get(j) != null;

        return adjacencyList[i][j] != null;
    }

    public Edge incidentEdge(int id) {
        //System.out.println("Find edge incident to " + id);
        //System.out.println("Possible edges: " + Arrays.toString(adjacencyList[id]));
        //int key = list.get(id).keySet().iterator().next();
        //return list.get(id).get(key);
        //return list.get(id).values().iterator().next();

        for (int i = 0; i < numberOfVertices; i++){
            if (adjacencyList[id][i] != null){
                return adjacencyList[id][i];
            }
        }

        return null;
    }

}