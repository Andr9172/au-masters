import java.util.ArrayList;

public class Edge {

    public int weight;
    public Node userData; // To be customized to the specific context
    public Vertex[] endpoints;
    public Edge[] prev;
    public Edge[] next;

    public Edge(int weight, Node userData, Vertex[] endpoints, Edge[] prev, Edge[] next) {
        this.weight = weight;
        this.userData = userData;
        this.endpoints = endpoints;
        this.prev = prev;
        this.next = next;
    }

    public Edge(){
        this.weight = 0;
        this.userData = null;
        this.endpoints = new Vertex[2];
        this.prev = new Edge[2];
        this.next = new Edge[2];
    }
}