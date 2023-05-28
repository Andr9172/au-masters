import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

public class TopTreePotentialTest {

    static TopTreeInterface topTree;
    static ArrayList<ArrayList<Integer>> edges;
    static Tree tree;
    static Random rnd;

    static File csv;
    static PrintWriter pw;

    // This class is for testing the potential of top trees.
    // In particular, we wish to test that the potential behaves as expected
    // In order to do so, we need a way to compute the potential of the entire tree
    // We are gonna use the size top tree, as it already maintains the number of leaves for us :)

    public static void main(String[] args) {

        try {
            // Prepare data logging file
            csv = new File("testPotential2.csv");
            pw = new PrintWriter(csv);
            pw.write("Operation,OperationNumber,Runtime,PotentialChange\n");


            int numberOfVertices = 100000;
            int numberOfEdges = 5000;
            edges = new ArrayList<>();
            setupTopTree(numberOfVertices);

            System.out.println("Run: " + 30000 + " " + 4);
            testPotential(numberOfVertices, 100, 0);


            pw.flush();
            pw.close();
        } catch (Exception e){
            e.printStackTrace();
        }


    }

    private static void setupTopTree(int numberOfVertices) {
        rnd = new Random();

        tree = Tree.createTree(numberOfVertices);
        topTree = new SizeTopTree();

        ArrayList<Integer> unconnected = new ArrayList<>();
        for (int i = 0; i < numberOfVertices; i++ ){
            unconnected.add(i);
        }
        ArrayList<Integer> connected = new ArrayList<>();

        int a = rnd.nextInt(numberOfVertices);
        int b = (rnd.nextInt(numberOfVertices) + a) % numberOfVertices;

        if (a == b){
            System.out.println("Unlucky rerun it");
        }

        topTree.link(tree.vertex.get(a), tree.vertex.get(b), 0);
        ArrayList<Integer> tempList = new ArrayList<>();
        tempList.add(a);
        tempList.add(b);
        edges.add(tempList);
        connected.add(a);
        connected.add(b);

        int i = 0;
        while(unconnected.size() > 0){
            System.out.println(i);
            a = unconnected.get(rnd.nextInt(unconnected.size()));
            b = connected.get(rnd.nextInt(connected.size()));
            topTree.link(tree.vertex.get(a), tree.vertex.get(b), 0);
            tempList = new ArrayList<>();
            tempList.add(a);
            tempList.add(b);
            edges.add(tempList);
            unconnected.remove(Integer.valueOf(a));
            connected.add(a);
            i++;
        }

    }


    private static void testPotential(int numberOfVertices, int expose, int seed){
        // Test the expose and deexpose operation runtime and potential change
        for (int i = 0; i < expose; i++){
            double potentialBefore = computePotentialOfAllRoots(numberOfVertices);
            int a = rnd.nextInt(numberOfVertices);
            long start = System.nanoTime();
            topTree.expose(tree.vertex.get(a));
            long end = System.nanoTime();
            double potentialAfter = computePotentialOfAllRoots(numberOfVertices);
            pw.write("Expose," + i + "," + (end - start) + "," + (potentialAfter - potentialBefore) + "\n");
            System.out.println("Potential before " + potentialBefore);
            System.out.println("Potential after " + potentialAfter);
            potentialBefore = potentialAfter;
            topTree.deExpose(tree.vertex.get(a));
        }

        for (int i = 0; i < expose; i++){
            int a = rnd.nextInt(numberOfVertices);
            topTree.expose(tree.vertex.get(a));
            double potentialBefore = computePotentialOfAllRoots(numberOfVertices);
            long start = System.nanoTime();
            topTree.deExpose(tree.vertex.get(a));
            long end = System.nanoTime();
            double potentialAfter = computePotentialOfAllRoots(numberOfVertices);
            pw.write("deExpose," + i + "," + (end - start) + "," + (potentialAfter - potentialBefore) + "\n");
            System.out.println("Potential before " + potentialBefore);
            System.out.println("Potential after " + potentialAfter);
        }


        ArrayList<ArrayList<Vertex>> EdgesRemoved = new ArrayList<>();
        // Test cut, and then re-add those in random order
        for (int i = 0; i < expose; i++){
            int a = rnd.nextInt(edges.size());
            if (tree.vertex.get(a).firstEdge == null){
                i--;
                continue;
            }
            Edge e = tree.vertex.get(a).firstEdge;
            ArrayList<Vertex> list = new ArrayList<>();
            list.add(e.endpoints[0]);
            list.add(e.endpoints[1]);
            EdgesRemoved.add(list);
            double potentialBefore = computePotentialOfAllRoots(numberOfVertices);
            long start = System.nanoTime();
            topTree.cut(e);
            long end = System.nanoTime();
            double potentialAfter = computePotentialOfAllRoots(numberOfVertices);
            pw.write("cut," + i + "," + (end - start) + "," + (potentialAfter - potentialBefore) + "\n");
            System.out.println("Potential before " + potentialBefore);
            System.out.println("Potential after " + potentialAfter);
        }

        // Test cut, and then re-add those in random order
        for (int i = 0; i < expose; i++){
            int a = rnd.nextInt(EdgesRemoved.size());
            ArrayList<Vertex> list = EdgesRemoved.get(a);
            EdgesRemoved.remove(list);

            double potentialBefore = computePotentialOfAllRoots(numberOfVertices);
            long start = System.nanoTime();
            topTree.link(list.get(0), list.get(1), 0);
            long end = System.nanoTime();
            double potentialAfter = computePotentialOfAllRoots(numberOfVertices);
            pw.write("link," + i + "," + (end - start) + "," + (potentialAfter - potentialBefore) + "\n");
            System.out.println("Potential before " + potentialBefore);
            System.out.println("Potential after " + potentialAfter);
        }

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
