import java.util.ArrayList;
import java.util.Random;

public class TopTreePotentialTest {

    static TopTreeInterface topTree;

    // This class is for testing the potential of top trees.
    // In particular, we wish to test that the potential behaves as expected
    // In order to do so, we need a way to compute the potential of the entire tree
    // We are gonna use the size top tree, as it already maintains the number of leaves for us :)

    public static void main(String[] args) {



    }


    private static void testPotential(int numberOfVertices, int numberOfEdge){
        Random rnd = new Random();

        ArrayList<Kruskal.GraphEdge> edges = new ArrayList<>();
        int j = 0;
        for (int i = 0; i < numberOfEdge; i++){
            if (i % (numberOfEdge/10) == 0){
                j++;
            }
            int dest = Math.abs(rnd.nextInt() % numberOfVertices);
            int source = Math.abs(rnd.nextInt() % numberOfVertices);

            if (dest == source) {
                i--;
                continue;
            }
            int weight = Math.abs(rnd.nextInt() % 100) + 1;
            edges.add(new Kruskal.GraphEdge(source, dest, weight));
        }
        Tree t = Tree.createTree(numberOfVertices);
        SizeTopTree topTree = new SizeTopTree();

        for (int i = 0; i < numberOfEdge; i++) {
            if (i % (numberOfEdge / 10) == 0) {
                //System.out.println(10 * j + " percent done");
                j++;
            }
            int a = edges.get(i).src;
            int b = edges.get(i).dest;
            int weight = edges.get(i).weight;
            Node root1 = topTree.expose(t.vertex.get(a));
            Node root2 = topTree.expose(t.vertex.get(b));

            LeafNode maxEdge = null;
            boolean insertLink = false;
            if (root1 != null && (root1.equals(root2))){
                // Skip edge
            } else {
                insertLink = true;
            }
            topTree.deExpose(t.vertex.get(a));
            topTree.deExpose(t.vertex.get(b));
            if(insertLink){
                Node newRoot = topTree.link(t.vertex.get(a), t.vertex.get(b), weight);
            }
        }
        SizeUserInfo userInfo = (SizeUserInfo) topTree.findRoot(t.vertex.get(0).firstEdge.userData).userInfo;
        int size = userInfo.size;
        System.out.println("Size of spanning tree is: " + size);
        topTree.testSplay();

    }

    private static double computePotentialOfAllRoots(int numberOfVertices){
        ArrayList<Node> rootsConsidered = new ArrayList<>();

        double potential = 0;

        for (int i = 0; i < numberOfVertices; i++){
            Vertex v = Tree.vertex.get(i);
            if (v.firstEdge != null){
                Node n = v.firstEdge.userData;
                Node root = topTree.findRoot(n);
                if (rootsConsidered.contains(root)){
                    continue;
                }
                potential += computePotential(root);
                rootsConsidered.add(root);
            }
        }

        return potential;
    }

    private static double computePotential(Node node){
        // The potential is computed as
        // sum over nodes log_2(#leaves)
        SizeUserInfo info = (SizeUserInfo) node.userInfo;

        if (node.isLeaf){
            return Math.log(info.size)/Math.log(2);
        }
        InternalNode internalNode = (InternalNode) node;
        return Math.log(info.size)/Math.log(2) + computePotential(internalNode.children.get(0)) + computePotential(internalNode.children.get(1));
    }



}
