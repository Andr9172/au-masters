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
        Tree t = new Tree(size, vertices);
        return t;
    }

    // Method temporarily included, we don't have to do explicit memory handling in java
    // This was used for clean up in the c-code
    public void destroyTree(){
    }

    // Remove edge from both linked lists
    public static void destroyEdge(Edge edge){
        destroyEdgeInner(edge.endpoints.get(0), edge.prev.get(0), edge.next.get(0));
        destroyEdgeInner(edge.endpoints.get(1), edge.prev.get(1), edge.next.get(1));
    }

    // Remove edge from one linked list
    public static void destroyEdgeInner(Vertex v, Edge prev, Edge next){
        if (prev != null){
            // Check if v is equal to the first or second endpoint
            int j = prev.endpoints.get(1).equals(v) ? 1 : 0;
            prev.next.add(j, next);
        } else {
            v.firstEdge = next;
        }
        if (next != null){
            // Check if v is equal to the first or second endpoint
            int j = next.endpoints.get(1).equals(v) ? 1 : 0;
            next.prev.add(j, prev);
        }
    }

    // Add the given edge to the tree
    public static void addEdge(Edge edge, Vertex left, Vertex right, int weight){
        Edge tempLeft = left.firstEdge;
        Edge tempRight = right.firstEdge;

        left.firstEdge = edge;
        if (tempLeft != null){
            int j = tempLeft.endpoints.get(1) == left ? 1 : 0;
            tempLeft.prev.add(j, edge);
        }

        right.firstEdge = edge;
        if (tempRight != null){
            int j = tempRight.endpoints.get(1) == right ? 1 : 0;
            tempRight.prev.add(j, edge);
        }

        edge.weight = weight;
        edge.userData = null;
        edge.endpoints.add(0, left);
        edge.endpoints.add(1, right);
        edge.prev.add(0, null);
        edge.prev.add(1, null);
        edge.next.add(0, tempLeft);
        edge.next.add(1, tempRight);
    }

    public static boolean hasAtMostOneIncidentEdge(Vertex vertex){
        Edge firstEdge = vertex.firstEdge;
        if (firstEdge != null) {
            int j = firstEdge.endpoints.get(1) == vertex ? 1 : 0;
            return firstEdge.next.get(j) == null;
        } else {
            return true;
        }
    }

}