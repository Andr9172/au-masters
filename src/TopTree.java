import java.util.ArrayList;

public class TopTree {

    // Method just included, we don't have to do memory cleanup
    public void freeTopTree(Node node){

    }

    // Remove edge from tree
    public static void cut(Edge edge){
        Node node = edge.userData;

        Vertex u = edge.endpoints.get(0);
        Vertex v = edge.endpoints.get(1);
        fullSplay(node);
        // Now depth <= 2, and if e is a leaf edge, depth(e) <= 1
        deleteAllAncestors(node);
        Tree.destroyEdge(edge);

        u.isExposed = true;
        v.isExposed = true;

        deExpose(u);
        deExpose(v);
    }

    // Add edge to tree
    public static Node link(Vertex u, Vertex v, int weight){
        LeafNode tEdge = null;
        InternalNode tuNew = null;
        InternalNode tvNew = null;
        Edge edge = null;

        Node tu = expose(u);
        if (tu != null && hasLeftBoundary(tu)){
            tu.flip = !tu.flip;
        }
        u.isExposed = false;

        Node tv = expose(v);
        if (tv != null && hasRightBoundary(tv)){
            tv.flip = !tv.flip;
        }
        v.isExposed = false;

        // Create edge, values currently get overwritten later anyway
        edge = new Edge();

        // Init T_edge
        Tree.addEdge(edge, u, v, weight);
        tEdge = new LeafNode(null, 0, edge, (tu != null ? 1 : 0) + (tv != null ? 1 : 0));
        Node t = (Node) tEdge;
        recomputeSpineWeight(t);
        edge.userData = tEdge;

        if (tu != null){
            ArrayList<Node> children = new ArrayList<>();
            children.add(tu);
            children.add(t);
            tuNew = new InternalNode(null, 0, children, tv != null ? 1 : 0);
            t.parent = tuNew;
            tu.parent = tuNew;
            t = tuNew;
            recomputeSpineWeight(t);
        }
        if (tv != null){
            ArrayList<Node> children = new ArrayList<>();
            children.add(t);
            children.add(tv);
            tvNew = new InternalNode(null, 0, children, 0);
            t.parent = tvNew;
            tv.parent = tvNew;
            t = tvNew;
            recomputeSpineWeight(t);
        }

        return t;
    }

    // Deexpose vertex in underlying tree
    public static Node deExpose(Vertex v){
        Node root = null;
        Node node = findConsumingNode(v);
        while (node != null) {
            root = node;
            root.numBoundary = root.numBoundary - 1;
            recomputeSpineWeight(root);
            node = root.parent;
        }
        v.isExposed = false;
        return root;
    }

    // Expose version 1
    public static Node expose(Vertex v){
        Node node = findConsumingNode(v); // contains a semi splay
        if (node == null){
            v.isExposed = true;
            return null;
        }

        int counter = 0;
        ArrayList<Node> visited = new ArrayList<>();
        while (isPath(node)) { // rotateUp until consuming node is a point cluster
            InternalNode internalNode = (InternalNode) node;
            InternalNode parent = node.parent;
            pushFlip(internalNode);
            int nodeIndex = parent.children.get(1) == node ? 1 : 0;
            rotateUp(internalNode.children.get(nodeIndex));
            visited.add(node); // delete after bugfixing TODO
            node = parent;
            counter++;
        }

        fullSplay(node);

        // Now depth(node) <= 1, and node is the consuming point cluster
        Node root = null;
        while (node != null) {
            root = node;
            root.numBoundary += 1;
            recomputeSpineWeight(node);
            node = root.parent;
        }
        v.isExposed = true;
        return root;
    }

    // Expose version 2 (from appendix)
    public static Node expose2(Vertex v){
        Node consumingNode = findConsumingNode(v);
        if (consumingNode != null) {
            consumingNode = prepareExpose(consumingNode);
            Node root = exposePrepared(consumingNode);
            v.isExposed = true;
            return root;
        } else {
            v.isExposed = true;
            return null;
        }
    }

    // Query for information
    public static LeafNode findMaximum(Node root){
        Node node = root;
        while (!node.isLeaf){
            InternalNode n = (InternalNode) node;
            if (n.children.get(0).spineWeight > n.children.get(1).spineWeight){
                node = n.children.get(0);
            } else {
                node = n.children.get(1);
            }
        }
        return (LeafNode) node;
    }

    // Find root
    public static Node findRoot(Node node){
        Node tempNode = node;
        while (tempNode.parent != null){
            tempNode = tempNode.parent;
        }
        return node;
    }

    public static boolean hasLeftBoundary(Node node){
        if (node.isLeaf){
            LeafNode leaf_node = (LeafNode) node;
            Vertex endpoints = leaf_node.edge.endpoints.get(node.flip ? 1 : 0);
            return endpoints.isExposed || !Tree.hasAtMostOneIncidentEdge(endpoints);
        } else {
            InternalNode int_node = (InternalNode) node;
            Node child = int_node.children.get(node.flip ? 1 : 0);
            return isPath(child);
        }
    }

    // This have been copied, and modified from hasLeft could contain errors
    public static boolean hasRightBoundary(Node node){
        if (node.isLeaf){
            LeafNode leaf_node = (LeafNode) node;
            Vertex endpoints = leaf_node.edge.endpoints.get(!node.flip ? 1 : 0);
            return endpoints.isExposed || !Tree.hasAtMostOneIncidentEdge(endpoints);
        } else {
            InternalNode int_node = (InternalNode) node;
            Node child = int_node.children.get(!node.flip ? 1 : 0);
            return isPath(child);
        }
    }

    public static boolean hasMiddleBoundary(Node node){
        if (node.isLeaf || node.numBoundary == 0){
            return false;
        }
        InternalNode int_node = (InternalNode) node;
        boolean leftPath = isPath(int_node.children.get(node.flip ? 1 : 0));
        boolean rightPath = isPath(int_node.children.get(!node.flip ? 1 : 0));

        int hasMiddle = node.numBoundary - (leftPath ? 1 : 0) - (rightPath ? 1 : 0);
        return hasMiddle == 1;
    }

    public static Node getSibling(Node node){
        InternalNode parent = node.parent;
        if (parent == null) {
            return null;
        }
        int j = parent.children.get(0) == node ? 1 : 0;
        return parent.children.get(j);
    }

    public static void rotateUp(Node node){
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

        grandParent.children.set(uncleIsLeftChild ? 1 : 0, node);
        grandParent.children.set(!uncleIsLeftChild ? 1 : 0, parent);
        grandParent.flip = flipGrandparent;

        // This is done why?
        recomputeSpineWeight(parent);
        recomputeSpineWeight(grandParent);

        node.parent = grandParent;
        uncle.parent = parent;
    }

    public static Node splayStep(Node node){
        while (true){
            InternalNode parent = node.parent;
            if (parent == null){
                return null;
            }
            InternalNode grandParent = parent.parent;
            if (grandParent == null){
                return null;
            }

            if (isPoint(node) && isPoint(grandParent)){
                rotateUp(node);
                return grandParent;
            }

            InternalNode greatGrandParent = grandParent.parent;
            if (greatGrandParent == null){
                return null;
            }
            if (isPath(parent) && (isPath(grandParent) || isPoint(greatGrandParent))){
                pushFlip(grandParent);
                pushFlip(parent);
                boolean nodeIsLeft = parent.children.get(0) == node;
                boolean parentIsLeft = grandParent.children.get(0) == parent;
                boolean grandParentIsLeft = greatGrandParent.children.get(0) == grandParent;
                if (nodeIsLeft == parentIsLeft){
                    rotateUp(node);
                    return grandParent;
                }
                if (parentIsLeft == grandParentIsLeft){
                    rotateUp(parent);
                    return greatGrandParent;
                }
                // At this point node is left == grandparent is left
                rotateUp(getSibling(node));
                rotateUp(parent);
                return greatGrandParent;
            }
            node = parent;
        }
    }

    public static void semiSplay(Node node){
        while(node != null){
            node = splayStep(node);
        }
    }

    public static void fullSplay(Node node){
        while (true){
            Node top = splayStep(node);
            if (top == null){
                return;
            }
            splayStep(top);
        }
    }

    public static Node findConsumingNode(Vertex vert){
        Edge start = vert.firstEdge;

        if (start == null){
            return null;
        }
        Node node = start.userData;
        semiSplay(node);
        if (Tree.hasAtMostOneIncidentEdge(vert)) {
            return node;
        }

        boolean isLeft = (start.endpoints.get(0) == vert) != node.flip;
        boolean isMiddle = false;
        boolean isRight = (start.endpoints.get(1) == vert) != node.flip;
        Node lastMiddleNode = null;

        while(node.parent != null){
            InternalNode parent = node.parent;
            boolean isLeftChild = parent.children.get(0) == node;

            // Compute where v is in the parent, taking the parent's
            // flip into account
            isMiddle = isLeftChild
                    ? (isRight || (isMiddle && !hasRightBoundary(node)))
                    : (isLeft || (isMiddle && !hasLeftBoundary(node)));
            isLeft = (isLeftChild != parent.flip) && !isMiddle;
            isRight = (isLeftChild == parent.flip) && !isMiddle;

            // Go up to the parent
            node = parent;

            // if v is in the middle, then it could be the consuming node
            if (isMiddle) {
                if (!hasMiddleBoundary(node)) {
                    return node;
                }
                lastMiddleNode = node;
            }
        }
        return lastMiddleNode;
    }

    public static Node prepareExpose(Node consumingNode){
        Node node = consumingNode;
        while (node.parent != null) {
            InternalNode parent = node.parent;
            if (isPoint(node)){
                node = parent;
            } else {
                InternalNode internalNode = (InternalNode) node;

                pushFlip(parent);
                pushFlip(internalNode);

                Node sibling = getSibling(node);
                int sibling_index = parent.children.get(1) == sibling ? 1 : 0;
                Node sameSideChild = internalNode.children.get(sibling_index);
                if (isPath(sameSideChild) || isPoint(sibling)){
                    // Case (a), (b), (c), (d)
                    Node otherSideChild = internalNode.children.get(1-sibling_index);
                    rotateUp(otherSideChild);
                    if (node == consumingNode) {
                        // Case (a), (b)
                        consumingNode = parent;
                    }
                    node = parent;
                } else {
                    Node uncle = getSibling(parent);
                    InternalNode grandParent = parent.parent;
                    int uncleIndex = grandParent.children.get(1) == uncle ? 1 : 0;

                    if (sibling_index == uncleIndex){
                        // (e)
                        rotateUp(node);
                    } else {
                        // case (f)
                        rotateUp(sibling);
                    }
                }
            }
        }
        return consumingNode;
    }

    public static Node exposePrepared(Node consumingNode){
        boolean fromLeft = false;
        boolean fromRight = false;
        Node node = consumingNode;

        while(true){
            node.numBoundary += 1;
            recomputeSpineWeight(node);
            InternalNode parent = node.parent;
            if(parent == null){
                return node;
            }
            boolean isLeftChildOfParent = parent.children.get(0) == node;
            boolean isRightChildOfParent = !isLeftChildOfParent;

            if((isLeftChildOfParent && fromRight) || (isRightChildOfParent && fromLeft)){
                node.flip = !node.flip;
            }
            fromLeft = isLeftChildOfParent != parent.flip;
            fromRight = isRightChildOfParent != parent.flip;
            node = parent;
        }

    }

    private static void deleteAllAncestors(Node node){
        Node parent = node.parent;
        if (parent != null){
            Node sibling = getSibling(node);
            deleteAllAncestors(parent);
            sibling.parent = null;

        }
    }

    private static boolean isPoint(Node node) {
        return node.numBoundary < 2;
    }

    private static boolean isPath(Node node) {
        return node.numBoundary == 2;
    }

    private static void recomputeSpineWeight(Node node) {
        if (isPoint(node)){
            node.spineWeight = Integer.MIN_VALUE;
        } else if (node.isLeaf){
            LeafNode n = (LeafNode) node;
            n.spineWeight = n.edge.weight;
        } else {
            InternalNode n = (InternalNode) node;
            int spineWeight0 = n.children.get(0).spineWeight;
            int spineWeight1 = n.children.get(1).spineWeight;
            n.spineWeight = Math.max(spineWeight0, spineWeight1);
        }
    }

    private static void pushFlip(InternalNode node) {
        if (node.flip){
            node.flip = false;

            Node tmp = node.children.get(0);

            node.children.set(0, node.children.get(1));
            node.children.set(1, tmp);

            node.children.get(0).flip = !node.children.get(0).flip;
            node.children.get(1).flip = !node.children.get(1).flip;

        }
    }


}