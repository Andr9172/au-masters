import java.util.*;

public class Tree {
    // This is the underlying tree!

    public int size;
    public ArrayList<Vertex> vertex;

    public Tree(int size, ArrayList<Vertex> vertex) {
        this.size = size;
        this.vertex = vertex;
    }

    // Create the tree with the "correct" number of vertexes
    public static Tree createTree(int size){
        ArrayList<Vertex> vertices = new ArrayList<>();
        for (int i = 0; i < size; i++){
            vertices.add(new Vertex(null, false));
        }
        return new Tree(size, vertices);
    }

    // Remove edge from both linked lists
    public static void destroyEdge(Edge edge){
        destroyEdgeInner(edge.endpoints[0], edge.prev[0], edge.next[0]);
        destroyEdgeInner(edge.endpoints[1], edge.prev[1], edge.next[1]);
    }

    // Remove edge from one linked list
    public static void destroyEdgeInner(Vertex v, Edge prev, Edge next){
        if (prev != null){
            // Check if v is equal to the first or second endpoint
            int j = prev.endpoints[1].equals(v) ? 1 : 0;
            prev.next[j] =  next;
        } else {
            v.firstEdge = next;
        }
        if (next != null){
            // Check if v is equal to the first or second endpoint
            int j = next.endpoints[1].equals(v) ? 1 : 0;
            next.prev[j] = prev;
        }
    }

    // Add the given edge to the tree
    public static void addEdge(Edge edge, Vertex left, Vertex right, int weight){
        Edge tempLeft = left.firstEdge;
        Edge tempRight = right.firstEdge;

        left.firstEdge = edge;
        if (tempLeft != null){
            int j = tempLeft.endpoints[1] == left ? 1 : 0;
            tempLeft.prev[j] = edge;
        }

        right.firstEdge = edge;
        if (tempRight != null){
            int j = tempRight.endpoints[1] == right ? 1 : 0;
            tempRight.prev[j] = edge;
        }

        edge.weight = weight;
        edge.userData = null;
        edge.endpoints[0] = left;
        edge.endpoints[1] = right;
        edge.prev[0] = null;
        edge.prev[1] = null;
        edge.next[0] = tempLeft;
        edge.next[1] = tempRight;
    }

    public static boolean hasAtMostOneIncidentEdge(Vertex vertex){
        Edge firstEdge = vertex.firstEdge;
        if (firstEdge != null) {
            int j = firstEdge.endpoints[1] == vertex ? 1 : 0;
            return firstEdge.next[j] == null;
        } else {
            return true;
        }
    }

}