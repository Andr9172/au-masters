import java.util.ArrayList;
import java.util.List;

public class Edge{

    public int weight;
    public Node userData; // To be customized to the specific context
    public Vertex[] endpoints;
    public Edge[] prev;
    public Edge[] next;
    public boolean covered = false;

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

    public Edge(int i, int j) {
        this.endpoints = new Vertex[2];
        this.endpoints[0] = Tree.getVertex(i);
        this.endpoints[1] = Tree.getVertex(j);
    }

    @Override
    public int hashCode(){
        if (endpoints[0].id < endpoints[1].id){
            return this.endpoints[0].hashCode() + this.endpoints[1].hashCode();
        } else {
            return this.endpoints[0].hashCode() + this.endpoints[1].hashCode();
        }
    }

    @Override
    public boolean equals(Object obj) {
        return this.hashCode() == obj.hashCode();
    }
}