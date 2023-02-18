

public class TopTree {



    // Remove edge from tree
    public void cut(Edge edge){

    }

    // Add edge to tree
    public Node link(Vertex u, Vertex v, int weight){
        return null;
    }

    // Deexpose vertex in underlying tree
    public Node deExpose(Vertex v){
        return null;
    }

    // Expose version 1
    public Node expose(Vertex v){
        return null;
    }

    // Expose version 2 (from appendix)
    public Node expose2(Vertex v){
        return null;
    }

    // Query for information
    public LeafNode findMaximum(Node root){
        return  null;
    }

    // Find root
    public Node findRoot(Node node){
        Node tempNode = node;
        if (tempNode.parent != null){
            tempNode = tempNode.parent;
        }
        return node;
    }

    public boolean hasLeftBoundary(Node node){
        if (node.isLeaf){

        }
        return false;
    }


}