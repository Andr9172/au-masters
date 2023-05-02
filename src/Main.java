// Method for creating a top tree and testing it

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;

public class Main {

    private static  boolean debug = false;

    public static void main(String[] args) {
        //if (args.length < 1) {
        //    System.out.println("Missing arguments");
        //    return;
        //}

        //if (args[0].equals("command")){
        //    runCommandMode();
        //}

        // Fail infinite loop
        //int numberOfVertices = 10;
        //int numberOfEdge = 20;
        //int seed = 15;
        //int repeats = 1;
        boolean specific = true;
        int numberOfVertices = 8;
        int numberOfEdge = 16;
        int seed = 7341;
        int repeats = 10000;

        /* for (int i = 0; i <= repeats; i++){
            int res = runCompareMode(numberOfVertices, numberOfEdge);
            if (res != 0) System.out.println("Error in compare mode");
        } */

        //testSizeTopTree(numberOfVertices, numberOfEdge);

        if (specific){
            testTwoEdgeConnectivity(numberOfVertices, numberOfEdge, seed , 0, 1);
        } else {
            for (int i = 0; i < repeats; i++){
                System.out.println("iteration " + i);
                testTwoEdgeConnectivity(numberOfVertices, numberOfEdge, i , 0, 1);
            }
        }

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
        topTree.testSplay();

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


    private static void testTwoEdgeConnectivity(int numberOfVertices, int numberOfEdge, int seed, int v1, int v2){

        // Create graphs given in above diagrams
        //System.out.println("Bridges in first graph ");

        twoEdgeComparison g1 = new twoEdgeComparison(numberOfVertices);

        // Generate GraphEdges and Vertices
        //System.out.println("Generating random graph with desired number of vertices and edges");
        if (numberOfEdge > (numberOfVertices * (numberOfVertices - 1)/2)){
            System.out.println("Trying to generate a graph with more edges than exists");
        }

        //Random rnd = new Random();
        //rnd.setSeed(3); // connected
        //rnd.setSeed(1); // not connected



        ArrayList<ArrayList<Integer>> edges = generateEdges(numberOfVertices, numberOfEdge, seed);

        for (ArrayList<Integer> edge: edges) {
            //System.out.println("Edge from " + edge.get(0) + " to " + edge.get(1) );
        }

        for (ArrayList<Integer> list : edges){
            g1.addEdge(list.get(0), list.get(1));
        }

        g1.bridge();
        /*if (g1.count == 0) {
            System.out.println(
                    "Given graph is 2-edge connected:");
        }
        else {
            System.out.println(
                    "Given graph is not 2-edge connected:");
        }*/


        // Test of 2-edge connectivity top tree
        Tree t = Tree.createTree(numberOfVertices);
        twoEdgeConnectivityTopTree topTree = new twoEdgeConnectivityTopTree(numberOfVertices);
        debug = topTree.debug;

        int i = 0;
        for (ArrayList<Integer> list : edges){
            //System.out.println("Edge from " + list.get(0) + " to " + list.get(1) );

            int a = list.get(0);
            int b = list.get(1);
            int weight = 1;

            if (topTree.debug){
                System.out.print("Edge " + i + " ");
            }
            topTree.insert(t.vertex.get(a),t.vertex.get(b));
            i++;
        }
        //topTree.computeAllCombine(t.vertex.get(0).firstEdge.userData);


        topTree.delete(t.vertex.get(edges.get(0).get(0)), t.vertex.get(edges.get(0).get(1)));
        toptreeConnected(topTree.twoEdgeConnected(t.vertex.get(0), t.vertex.get(1)));
        topTree.delete(t.vertex.get(edges.get(1).get(0)), t.vertex.get(edges.get(1).get(1)));
        toptreeConnected(topTree.twoEdgeConnected(t.vertex.get(0), t.vertex.get(1)));
        topTree.delete(t.vertex.get(edges.get(2).get(0)), t.vertex.get(edges.get(2).get(1)));
        toptreeConnected(topTree.twoEdgeConnected(t.vertex.get(0), t.vertex.get(1)));
        topTree.delete(t.vertex.get(edges.get(3).get(0)), t.vertex.get(edges.get(3).get(1)));
        toptreeConnected(topTree.twoEdgeConnected(t.vertex.get(0), t.vertex.get(1)));



        g1.removeEdge(t.vertex.get(edges.get(0).get(0)).id, t.vertex.get(edges.get(0).get(1)).id);
        g1.removeEdge(t.vertex.get(edges.get(1).get(0)).id, t.vertex.get(edges.get(1).get(1)).id);
        g1.removeEdge(t.vertex.get(edges.get(2).get(0)).id, t.vertex.get(edges.get(2).get(1)).id);
        g1.removeEdge(t.vertex.get(edges.get(3).get(0)).id, t.vertex.get(edges.get(3).get(1)).id);


        //Vertex test = t.vertex.get(0);
        //Node root = topTree.findRoot(test.firstEdge.userData);
        //twoEdgeConnectivityUserInfo userInfo = (twoEdgeConnectivityUserInfo) root.userInfo;
        /*HashSet<Node> roots = new HashSet<>();
        for (int i = 0; i < numberOfVertices; i++){
            roots.add(topTree.findRoot(t.vertex.get(i).firstEdge.userData));
        }
        if (roots.size() >= 2){
            System.out.println("There is more than 1 top tree, this is why top Tree disagree");
        }
        Vertex test = t.vertex.get(0);
        Node root = topTree.findRoot(test.firstEdge.userData);
        twoEdgeConnectivityUserInfo userInfo = (twoEdgeConnectivityUserInfo) root.userInfo;
        if (userInfo.coverC >= 0) {
            System.out.println("Manual method says Top tree are 2 edge connected!");
        } else {
            System.out.println("Manual method says Top tree are it is not 2 edge connected!");
        };
        if (topTree.twoEdgeConnected(t.vertex.get(1), t.vertex.get(0))){
            System.out.println("Automatic method says Top tree are 2 edge connected!");
        } else {
            System.out.println("Automatic method says Top tree are it is not 2 edge connected!");
        }*/
        g1.bridge();

        boolean topTreeConnected = topTree.twoEdgeConnected(t.vertex.get(v1), t.vertex.get(v2));

        if ((g1.count == 0 & topTreeConnected)
            || ((!(g1.count == 0)) & (!topTreeConnected))) {
            System.out.println("Agreement");
        } else {
            System.out.println("Something went wrong");
        }

    }

    private static void toptreeConnected(boolean twoEdgeConnected) {
        if (twoEdgeConnected){
            if (debug){
                System.out.println("Connected");
            }
        } else {
            if (debug){
                System.out.println("Not connected");
            }
        }
    }

    private static ArrayList<ArrayList<Integer>> generateEdges(int numberOfVertices, int numberOfEdge, int seed) {
        Random rnd = new Random();
        //rnd.setSeed(1); // Not connected
        //rnd.setSeed(2); // Connected

        rnd.setSeed(seed);
        ArrayList<ArrayList<Integer>> allEdges = new ArrayList<>();
        for (int i = 0; i < numberOfVertices; i++){
            for (int j = 0; j < numberOfVertices; j++){
                if (j <= i) {
                    continue;
                } else {
                    ArrayList<Integer> edge = new ArrayList<>();
                    edge.add(i);
                    edge.add(j);
                    allEdges.add(edge);
                }
            }
        }
        //System.out.println("All edges size " + allEdges.size());
        Collections.shuffle(allEdges, rnd);


        // Select the edges
        ArrayList<ArrayList<Integer>> chosenEdges = new ArrayList<>();
        for (int i = 0; i < numberOfEdge; i++){
            int index = rnd.nextInt(allEdges.size());
            chosenEdges.add(allEdges.get(index));
            allEdges.remove(index);
        }
        return chosenEdges;
    }

}
