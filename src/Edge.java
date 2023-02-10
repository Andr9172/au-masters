import java.util.ArrayList;

public class Edge {

    public int weight;
    public int userData; // To be customized to the specific context
    public ArrayList<Vertex>[] endpoints;
    public ArrayList<Edge>[] prev;
    public ArrayList<Edge>[] next;

    public Edge(int weight, int userData, ArrayList<Vertex>[] endpoints, ArrayList<Edge>[] prev, ArrayList<Edge>[] next) {
        this.weight = weight;
        this.userData = userData;
        this.endpoints = endpoints;
        this.prev = prev;
        this.next = next;
    }
}