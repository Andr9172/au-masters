import java.util.ArrayList;

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
            LeafNode leaf_node = (LeafNode) node;
            Vertex endpoints = leaf_node.edge.endpoints.get(node.flip ? 1 : 0);
            return endpoints.isExposed || !hasAtMostOneIncidentEdge(endpoints);
        } else {
            InternalNode int_node = (InternalNode) node;
            Node child = int_node.children.get(node.flip ? 1 : 0);
            return isPath(child);
        }
    }

    // This have been copied, and modified from hasLeft could contain errors
    public boolean hasRightBoundary(Node node){
        if (node.isLeaf){
            LeafNode leaf_node = (LeafNode) node;
            Vertex endpoints = leaf_node.edge.endpoints.get(!node.flip ? 1 : 0);
            return endpoints.isExposed || !hasAtMostOneIncidentEdge(endpoints);
        } else {
            InternalNode int_node = (InternalNode) node;
            Node child = int_node.children.get(!node.flip ? 1 : 0);
            return isPath(child);
        }
    }

    public boolean hasMiddleBoundary(Node node){
        if (node.isLeaf || node.numBoundary == 0){
            return false;
        }
        InternalNode int_node = (InternalNode) node;
        boolean leftPath = isPath(int_node.children.get(node.flip ? 1 : 0));
        boolean rightPath = isPath(int_node.children.get(!node.flip ? 1 : 0));

        int hasMiddle = node.numBoundary - (leftPath ? 1 : 0) - (rightPath ? 1 : 0);
        if (hasMiddle == 1){
            return true;
        }
        return false;
    }

    public Node getSibling(Node node){
        InternalNode parent = node.parent;
        if (parent == null) {
            return null;
        }
        int j = parent.children.get(0) == node ? 1 : 0;
        return parent.children.get(j);
    }

    public void rotateUp(Node node){
        InternalNode parent = node.parent;
        InternalNode grandParent = parent.parent;
        Node sibling = getSibling(node);
        Node uncle = getSibling(parent);

        pushFlip(grandParent);
        pushFlip(parent);

        boolean uncleIsLeftChild = grandParent.children.get(0) == uncle;
        boolean siblingIsLeftChild = parent.children.get(0) == sibling;
        boolean toSameSides = uncleIsLeftChild == siblingIsLeftChild;
        boolean siblingIsPath = isPath(sibling);
        boolean uncleIsPath = isPath(uncle);
        boolean gpIsPath = isPath(grandParent);
        boolean newParentIsPath, flipNewParent, flipGrandparent;

        if (toSameSides && siblingIsPath) {
            // Rotation on path
            boolean gpMiddle = hasMiddleBoundary(grandParent);
            newParentIsPath = gpMiddle || uncleIsPath;
            flipNewParent = false;
            flipGrandparent = false;
            if(gpMiddle && !gpIsPath){
                InternalNode ggp = grandParent.parent;
                if (ggp != null){
                    boolean gpIsLeftChild = ggp.children.get(0) == grandParent;
                    flipGrandparent = gpIsLeftChild == uncleIsLeftChild;
                }
            }
        } else {
            // Rotation on star
            if (!toSameSides){
                newParentIsPath = siblingIsPath || uncleIsPath;
                flipNewParent = siblingIsPath;
                flipGrandparent = siblingIsPath;
                node.flip = !node.flip;
            } else {
                newParentIsPath = uncleIsPath;
                flipNewParent = false;
                flipGrandparent = false;
                sibling.flip = !node.flip;
            }
        }

        parent.children.set(uncleIsLeftChild ? 1 : 0, sibling);
        parent.children.set(!uncleIsLeftChild ? 1 : 0, uncle);
        parent.flip = flipNewParent;
        parent.numBoundary = (newParentIsPath ? 1 : 0) + 1;

    }

    private void pushFlip(InternalNode grandParent) {
    }

    private boolean isPath(Node child) {
    }

    private boolean hasAtMostOneIncidentEdge(Vertex endpoints) {
    }


}