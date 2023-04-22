import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class Graph {

    int numberOfVertices;

    public Edge[][] adjacencyList;

    public Graph(int numberOfVertices){
        this.numberOfVertices = numberOfVertices;

        adjacencyList = new Edge[numberOfVertices][numberOfVertices];
    }

    public void addEdge(Edge e){
        int i = e.endpoints[0].id;
        int j = e.endpoints[1].id;
        adjacencyList[i][j] = e;
        adjacencyList[j][i] = e;
    }

    public void addEdge(int i, int j){
        Edge e = new Edge();
        adjacencyList[i][j] = e;
        adjacencyList[j][i] = e;
    }

    public void removeEdge(Edge e){
        int i = e.endpoints[0].id;
        int j = e.endpoints[1].id;
        adjacencyList[i][j] = null;
        adjacencyList[j][i] = null;
    }

    public void removeEdge(int i, int j){
        adjacencyList[i][j] = null;
        adjacencyList[j][i] = null;
    }

    public Edge getEdge(Vertex u, Vertex v){
        return adjacencyList[u.id][v.id];
    }


    public boolean containEdge(int i, int j) {
        return adjacencyList[i][j] != null;
    }

    public Edge incidentEdge(int id) {
        for (int i = 0; i < numberOfVertices; i++){
            if (adjacencyList[id][i] != null){
                return adjacencyList[id][i];
            }
        }
        return null;
    }

}
