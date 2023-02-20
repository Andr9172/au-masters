public class InvariantCheck {

    public static class Result {
        Vertex left;
        Vertex middle;
        Vertex right;

        public Result(){

        }
    }

    private static Result checkInvariantInner(Node node){
        Result res = new Result();
        res.left = null;
        res.middle = null;
        res.right = null;

        if (node.isLeaf){
            LeafNode leaf = (LeafNode) node;
            Vertex leftEp = leaf.edge.endpoints.get(leaf.flip ? 1 : 0);
            Vertex rightEp = leaf.edge.endpoints.get(!leaf.flip ? 1 : 0);
            if (leftEp.isExposed || !Tree.hasAtMostOneIncidentEdge(leftEp)){
                res.left = leftEp;
            }
            if (rightEp.isExposed || !Tree.hasAtMostOneIncidentEdge(rightEp)){
                res.right = rightEp;
            }

            if ((res.left == null  ?  0 : 1) + (res.middle == null ? 0 : 1) + (res.right == null ? 0 : 1) != node.numBoundary){
                System.out.println("Number of boundary vertices doesn't match in leaf");
            }
        } else {
            InternalNode internalNode = (InternalNode) node;
            Result bl = checkInvariantInner(internalNode.children.get(0));
            Result br = checkInvariantInner(internalNode.children.get(1));

            Vertex blRightmost = bl.right != null ? bl.right : bl.middle;
            Vertex brRightmost = br.right != null ? br.right : br.middle;
            Vertex blLeftmost = bl.left != null ? bl.left : bl.middle;
            Vertex brLeftmost = br.left != null ? br.left : br.middle;
            if (blRightmost != brLeftmost){
                System.out.println("left->rightmost != right->leftMost");
            }

            int leaves = countLeavesWith(node, blRightmost);
            int deg = getDegree(blRightmost);
            if (blRightmost.isExposed || (leaves < deg)){
                res.middle = blRightmost;
            }
            if (blLeftmost != blRightmost){
                res.left = blLeftmost;
            }
            if (brLeftmost != brRightmost){
                res.right = brRightmost;
            }

            if (internalNode.flip){
                Vertex tmp = res.left;
                res.left = res.right;
                res.right = tmp;
            }

            if ((res.left == null ? 0 : 1) + (res.middle == null ? 0 : 1) + (res.right == null ? 0 : 1) != node.numBoundary) {
                System.out.println("num_boundary mismatch");
            }

        }

        if (node.parent == null){
            if ((res.left != null && !res.left.isExposed)
                || (res.middle != null && !res.middle.isExposed)
                || (res.right != null && !res.right.isExposed)){
                System.out.println("Root is wrong");
            }
        }
        return res;
    }

    private static int getDegree(Vertex v) {
        int count = 0;
        Edge edge = v.firstEdge;
        while (edge != null){
            count += 1;
            int j = edge.endpoints.get(1) == v ? 1 : 0;
            edge = edge.next.get(j);
        }
        return count;
    }

    private static int countLeavesWith(Node node, Vertex v) {
        if (node.isLeaf){
            LeafNode leaf = (LeafNode) node;
            return (leaf.edge.endpoints.get(0) == v || leaf.edge.endpoints.get(1) == v) ? 1 : 0;
        } else {
         InternalNode internalNode = (InternalNode) node;
         return countLeavesWith(internalNode.children.get(0), v) + countLeavesWith(internalNode.children.get(1), v);
        }
    }

    public static void checkInvariant(Node node){
        if (node != null) checkInvariantInner(node);
    }

}
