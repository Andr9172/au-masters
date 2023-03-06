import java.util.ArrayList;

public interface TopTreeInterface {

    public void combine(Node t);

    public Node search(Node root);

    public UserInfo newUserInfo();

    // Find root
    public default Node findRoot(Node node){
        Node tempNode = node;
        while (tempNode.parent != null){
            tempNode = tempNode.parent;
        }
        return tempNode;
    }


    // Remove edge from tree
     public default void cut(Edge edge){
        Node node = edge.userData;

        Vertex u = edge.endpoints[0];
        Vertex v = edge.endpoints[1];
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
    public default Node link(Vertex u, Vertex v, int weight){
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
        tEdge = new LeafNode(null, newUserInfo(), edge, (tu != null ? 1 : 0) + (tv != null ? 1 : 0));
        Node t = (Node) tEdge;
        combine(t);
        edge.userData = tEdge;

        if (tu != null){
            ArrayList<Node> children = new ArrayList<>();
            children.add(0, tu);
            children.add(1, t);
            tuNew = new InternalNode(null, newUserInfo(), children, tv != null ? 1 : 0);
            t.parent = tuNew;
            tu.parent = tuNew;
            t = tuNew;
            combine(t);
        }
        if (tv != null){
            ArrayList<Node> children = new ArrayList<>();
            children.add(0, t);
            children.add(1, tv);
            tvNew = new InternalNode(null, newUserInfo(), children, 0);
            t.parent = tvNew;
            tv.parent = tvNew;
            t = tvNew;
            combine(t);
            //(t);
        }

        return t;
    }

    // Deexpose vertex in underlying tree
    public default Node deExpose(Vertex v){
        Node root = null;
        Node node = findConsumingNode(v);
        while (node != null) {
            root = node;
            root.numBoundary = root.numBoundary - 1;
            combine(root);
            node = root.parent;
        }
        v.isExposed = false;
        return root;
    }

    // Expose version 1
    public default Node expose(Vertex v){
        Node node = findConsumingNode(v); // contains a semi splay
        if (node == null){
            v.isExposed = true;
            return null;
        }

        while (isPath(node)) { // rotateUp until consuming node is a point cluster
            InternalNode internalNode = (InternalNode) node;
            InternalNode parent = node.parent;
            pushFlip(internalNode);
            int nodeIndex = parent.children.get(1) == node ? 1 : 0;
            rotateUp(internalNode.children.get(nodeIndex));
            node = parent;
        }

        fullSplay(node);

        // Now depth(node) <= 1, and node is the consuming point cluster
        Node root = null;
        while (node != null) {
            root = node;
            if (root.numBoundary == 2){
                System.out.println("Trying to increase numBoundary beyond 2 :)");
            }
            root.numBoundary = root.numBoundary + 1;
            combine(node);
            node = root.parent;
        }
        v.isExposed = true;
        return root;
    }

    // Expose version 2 (from appendix)
    public default Node expose2(Vertex v){
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



    public default boolean hasLeftBoundary(Node node){
        if (node.isLeaf){
            LeafNode leaf_node = (LeafNode) node;
            Vertex endpoint = leaf_node.edge.endpoints[node.flip ? 1 : 0];
            return endpoint.isExposed || !Tree.hasAtMostOneIncidentEdge(endpoint);
        } else {
            InternalNode int_node = (InternalNode) node;
            Node child = int_node.children.get(node.flip ? 1 : 0);
            return isPath(child);
        }
    }

    // This have been copied, and modified from hasLeft could contain errors
    public default boolean hasRightBoundary(Node node){
        if (node.isLeaf){
            LeafNode leaf_node = (LeafNode) node;
            Vertex endpoints = leaf_node.edge.endpoints[!node.flip ? 1 : 0];
            return endpoints.isExposed || !Tree.hasAtMostOneIncidentEdge(endpoints);
        } else {
            InternalNode int_node = (InternalNode) node;
            Node child = int_node.children.get(!node.flip ? 1 : 0);
            return isPath(child);
        }
    }

    public default boolean hasMiddleBoundary(Node node){
        if (node.isLeaf || node.numBoundary == 0){
            return false;
        }
        InternalNode int_node = (InternalNode) node;
        boolean leftPath = isPath(int_node.children.get(0));//node.flip ? 1 : 0));
        boolean rightPath = isPath(int_node.children.get(1));//!node.flip ? 1 : 0));

        int hasMiddle = node.numBoundary - (leftPath ? 1 : 0) - (rightPath ? 1 : 0);
        if (node.numBoundary == 3){
            System.out.println("Something is wrong???");
            System.out.println("For break point");
        }
        return hasMiddle == 1;
    }

    public default Node getSibling(Node node){
        InternalNode parent = node.parent;
        if (parent == null) {
            return null;
        }
        int j = parent.children.get(0) == node ? 1 : 0;
        return parent.children.get(j);
    }

    public default void rotateUp(Node node){
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
                sibling.flip = !sibling.flip;
            }
        }

        parent.children.set(uncleIsLeftChild ? 1 : 0, sibling);
        parent.children.set(!uncleIsLeftChild ? 1 : 0, uncle);
        parent.flip = flipNewParent;
        parent.numBoundary = (newParentIsPath ? 2 : 1);

        grandParent.children.set(uncleIsLeftChild ? 1 : 0, node);
        grandParent.children.set(!uncleIsLeftChild ? 1 : 0, parent);
        grandParent.flip = flipGrandparent;

        combine(parent);
        combine(grandParent);

        node.parent = grandParent;
        uncle.parent = parent;
    }

    public default Node splayStep(Node node){
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

    public default void semiSplay(Node node){
        Node top = node;
        while(top != null){
            top = splayStep(top);
        }
    }

    public default void fullSplay(Node node){
        while (true){
            Node top = splayStep(node);
            if (top == null){
                return;
            }
            splayStep(top);
        }
    }

    public default Node findConsumingNode(Vertex vert){
        Edge start = vert.firstEdge;

        if (start == null){
            return null;
        }
        Node node = start.userData;
        semiSplay(node);
        if (Tree.hasAtMostOneIncidentEdge(vert)) {
            return node;
        }

        boolean isLeft = (start.endpoints[0] == vert) != node.flip;
        boolean isMiddle = false;
        boolean isRight = (start.endpoints[1] == vert) != node.flip;
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

    public default Node prepareExpose(Node consumingNode){
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

    public default Node exposePrepared(Node consumingNode){
        boolean fromLeft = false;
        boolean fromRight = false;
        Node node = consumingNode;

        while(true){
            if (node.numBoundary == 2){
                System.out.println("Trying to increase the number of boundary beyond 2");
            }
            node.numBoundary += 1;
            combine(node);
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

    default void deleteAllAncestors(Node node){
        Node parent = node.parent;
        if (parent != null){
            Node sibling = getSibling(node);
            deleteAllAncestors(parent);
            sibling.parent = null;

        }
    }

    default boolean isPoint(Node node) {
        return node.numBoundary < 2;
    }

    default boolean isPath(Node node) {
        return node.numBoundary == 2;
    }


    default void pushFlip(InternalNode node) {
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