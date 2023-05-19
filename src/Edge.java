
public class Edge{

    public int weight;
    public Node userData; // To be customized to the specific context
    public Vertex[] endpoints;
    public Edge[] prev;
    public Edge[] next;

    public boolean covering;

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
            return 116191 * this.endpoints[0].id + 112921 * this.endpoints[1].id;
        } else {
            return 116191 * this.endpoints[1].id + 112921 * this.endpoints[0].id;
        }
    }

    @Override
    public boolean equals(Object obj) {
        return this.hashCode() == obj.hashCode();
    }
}