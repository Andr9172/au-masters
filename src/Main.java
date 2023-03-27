// Method for creating a top tree and testing it

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Main {

    public static void main(String[] args) {

        int numberOfVertices = 10000;
        int numberOfEdge = 1000000;
        int repeats = 4;

        for (int i = 0; i <= repeats; i++){
            int res = runCompareMode(numberOfVertices, numberOfEdge);
            if (res != 0) System.out.println("Error in compare mode");
        }

        testSizeTopTree(numberOfVertices, numberOfEdge);

        System.out.println("Unknown arguments?");

    }

    private static void testSizeTopTree(int numberOfVertices, int numberOfEdge) {
        Random rnd = new Random();

        ArrayList<Kruskal.GraphEdge> edges = new ArrayList<>();
        int j = 0;
        for (int i = 0; i < numberOfEdge; i++){
            if (i % (numberOfEdge/10) == 0){
                //System.out.println(10 * j + " percent done");
                j++;
            }
            int dest = Math.abs(rnd.nextInt() % numberOfVertices);
            int source = Math.abs(rnd.nextInt() % numberOfVertices);
            //if (edgeExistsAlready(source, dest, edges)){
            //    i--;
            //    continue;
            //}
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
                //InvariantCheck.checkInvariant(newRoot);
            }
        }
        SizeUserInfo userInfo = (SizeUserInfo) topTree.findRoot(t.vertex.get(0).firstEdge.userData).userInfo;
        int size = userInfo.size;
        System.out.println("Size of spanning tree is: " + size);

    }

    private static int runCompareMode(int numberOfVertices, int numberOfEdge) {

        // Generate GraphEdges and Vertices
        System.out.println("Generating random graph with desired number of vertices and edges");
        if (numberOfEdge > (numberOfVertices * (numberOfVertices - 1)/2)){
            System.out.println("Trying to generate a graph with more edges than exists");
        }

        Random rnd = new Random();

        ArrayList<Kruskal.GraphEdge> edges = new ArrayList<>();
        int j = 0;
        for (int i = 0; i < numberOfEdge; i++){
            if (i % (numberOfEdge/10) == 0){
                //System.out.println(10 * j + " percent done");
                j++;
            }
            int dest = Math.abs(rnd.nextInt() % numberOfVertices);
            int source = Math.abs(rnd.nextInt() % numberOfVertices);
            //if (edgeExistsAlready(source, dest, edges)){
            //    i--;
            //    continue;
            //}
            if (dest == source) {
                i--;
                continue;
            }
            int weight = Math.abs(rnd.nextInt() % 100) + 1;
            edges.add(new Kruskal.GraphEdge(source, dest, weight));
        }

        int kruskalResult = Kruskal.kruskals(numberOfVertices, edges);

        Collections.shuffle(edges);



        // Generate MST using top tree
        Tree t = Tree.createTree(numberOfVertices);
        System.out.println("Graph generated, and kruskal completed, now generating top tree");
        j = 0;
        MinimumSpanningTopTree topTree = new MinimumSpanningTopTree();
        for (int i = 0; i < numberOfEdge; i++){
            if (i % (numberOfEdge/10) == 0){
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
                MinimumSpanningTreeUserInfo userInfo = (MinimumSpanningTreeUserInfo) root1.userInfo;
                if (weight < userInfo.spineWeight){
                    insertLink = true;
                    maxEdge = topTree.findMaximum(root1);
                }
            } else {
                insertLink = true;
            }
            topTree.deExpose(t.vertex.get(a));
            topTree.deExpose(t.vertex.get(b));
            //InvariantCheck.checkInvariant(root1);
            //InvariantCheck.checkInvariant(root2);

            if(maxEdge != null){
                topTree.cut(maxEdge.edge);
            }
            if(insertLink){
                Node newRoot = topTree.link(t.vertex.get(a), t.vertex.get(b), weight);
                //InvariantCheck.checkInvariant(newRoot);
            }
        }

        // Check that the userInfo is correct
        Node v = t.vertex.get(0).firstEdge.userData;
        topTree.checkCombineFromRoot(topTree.findRoot(v));

        // Top tree should now have been built such that we can retrieve the minimum spanning tree!

        int topTreeTotalWeight = 0;
        for (int i = 0; i < numberOfVertices; i++){
            topTreeTotalWeight += sumOverAdjacentEdges(t.vertex, i);
        }
        topTreeTotalWeight = topTreeTotalWeight / 2;
        System.out.println("Top tree total weight is: " + topTreeTotalWeight);
        System.out.println("Kruskals total weight is: " + kruskalResult);
        if (topTreeTotalWeight != kruskalResult) System.out.println("Something is wrong");
        return topTreeTotalWeight == kruskalResult ? 0 : 1;
    }

    private static boolean edgeExistsAlready(int source, int dest, ArrayList<Kruskal.GraphEdge> edges) {
        for (Kruskal.GraphEdge edge: edges) {
            if (edge.dest == dest && edge.src == source) return true;
            else if(edge.src == dest && edge.dest == source) return true;
        }
        return false;
    }

    private static int sumOverAdjacentEdges(ArrayList<Vertex> vertex, int i) {
        int sum = 0;
        Edge edge = vertex.get(i).firstEdge;
        while (edge != null){
            sum += edge.weight;
            int j = edge.endpoints[1] == vertex.get(i) ? 1 : 0;
            edge = edge.next[j];
        }
        return sum;
    }


    private static void testTwoEdgeConnectivity(){



    }

}
