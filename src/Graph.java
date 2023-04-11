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

    public void removeEdge(Edge e){
        int i = e.endpoints[0].id;
        int j = e.endpoints[1].id;
        adjacencyList[i][j] = null;
        adjacencyList[j][i] = null;
    }

    public Edge getEdge(Vertex u, Vertex v){
        return adjacencyList[u.id][v.id];
    }


}
