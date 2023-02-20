import java.util.ArrayList;

public class Edge {

    public int weight;
    public Node userData; // To be customized to the specific context
    public ArrayList<Vertex> endpoints;
    public ArrayList<Edge> prev;
    public ArrayList<Edge> next;

    public Edge(int weight, Node userData, ArrayList<Vertex> endpoints, ArrayList<Edge> prev, ArrayList<Edge> next) {
        this.weight = weight;
        this.userData = userData;
        this.endpoints = endpoints;
        this.prev = prev;
        this.next = next;
    }

    public Edge(){
        this.weight = 0;
        this.userData = null;
        this.endpoints = new ArrayList<>();
        this.prev = new ArrayList<>();
        this.next = new ArrayList<>();
    }
}