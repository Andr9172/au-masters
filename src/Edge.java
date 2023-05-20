import java.util.ArrayList;
import java.util.List;

public class Edge{

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

    public Edge(int i, int j) {
        this.endpoints = new Vertex[2];
        this.endpoints[0] = Tree.getEdge(i);
        this.endpoints[1] = Tree.getEdge(j);
    }

    @Override
    public int hashCode(){
        if (endpoints[0].id < endpoints[1].id){
            return this.endpoints[0].hashCode() + this.endpoints[1].hashCode();
            //return 116191 * this.endpoints[0].id + 112921 * 2 * this.endpoints[1].id;
        } else {
            return this.endpoints[0].hashCode() + this.endpoints[1].hashCode();
            //return 116191 * this.endpoints[1].id + 112921 * 2 * this.endpoints[0].id;
        }
    }

    @Override
    public boolean equals(Object obj) {
        return this.hashCode() == obj.hashCode();
        /*if (obj != null){
            if (obj instanceof Edge){
                Edge other = (Edge) obj;
                for (Vertex v: this.endpoints){
                    List<Vertex> list = new ArrayList<>();
                    list.add(other.endpoints[0]);
                    list.add(other.endpoints[1]);
                    if (!list.contains(v)){
                        return false;
                    };
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
        return true;*/
    }
}