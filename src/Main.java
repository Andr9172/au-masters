// Method for creating a top tree and testing it

import java.io.*;
import java.util.*;

public class Main {

    private static boolean debug = false;
    private static boolean debug2 = false;

    static File csv;
    static PrintWriter pw;

    public static void main(String[] args) {
        //if (args.length < 1) {
        //    System.out.println("Missing arguments");
        //    return;
        //}

        //if (args[0].equals("command")){
        //    runCommandMode();
        //}

        // Normal debugging of top tree
        debug = false;
        // This is tracking statements for longer runs
        debug2 = true;
        boolean specific = true;
        int numberOfVertices = 5000;
        int numberOfEdge = 25000;
        int seed = numberOfEdge * numberOfVertices;
        int repeats = 10000;
        int numberOfEdgeToDelete = numberOfVertices;

        /* for (int i = 0; i <= repeats; i++){
            int res = runCompareMode(numberOfVertices, numberOfEdge);
            if (res != 0) System.out.println("Error in compare mode");
        } */

        //testSizeTopTree(numberOfVertices, numberOfEdge);
        /*try {
            runTest();
        } catch (Exception e){
            e.printStackTrace();
        }*/


        if (specific){
            testTwoEdgeConnectivity(numberOfVertices, numberOfEdge, seed , 0, 1, numberOfEdgeToDelete);
        } else {
            for (int i = 0; i < repeats; i++){
                System.out.println("iteration " + i);
                testTwoEdgeConnectivity(numberOfVertices, numberOfEdge, i , 0, 1, numberOfEdgeToDelete);
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


    private static void testTwoEdgeConnectivity(int numberOfVertices, int numberOfEdge, int seed, int v1, int v2, int numberOfEdgesToDelete){

        // Create graphs given in above diagrams

        twoEdgeComparison g1 = new twoEdgeComparison(numberOfVertices, debug);

        // Generate GraphEdges and Vertices
        if (numberOfEdge > (numberOfVertices * (numberOfVertices - 1)/2)){
            System.out.println("Trying to generate a graph with more edges than exists");
        }

        // Test of 2-edge connectivity top tree
        Tree t = Tree.createTree(numberOfVertices);
        twoEdgeConnectivityTopTree topTree = new twoEdgeConnectivityTopTree(numberOfVertices, debug);
        // Generate edges
        ArrayList<ArrayList<Integer>> edges = generateEdges2(numberOfVertices, numberOfEdge, seed);

        for (ArrayList<Integer> list : edges){
            g1.addEdge(list.get(0), list.get(1));
        }
        //g1.bridge();

        int i = 0;
        int j = 0;
        System.out.println("Inserting edges");
        for (ArrayList<Integer> list : edges){
            if (i % (numberOfEdge / 10) == 0 && debug2){
                System.out.println(j * 10 + "% done");
                j++;
            }
            //System.out.println("Edge from " + list.get(0) + " to " + list.get(1) );

            int a = list.get(0);
            int b = list.get(1);
            int weight = 1;

            if (topTree.debug){
                System.out.print("Edge " + i + " " );//+ t.vertex.get(a).id + " " + t.vertex.get(b).id);
            }
            topTree.insert(t.vertex.get(a),t.vertex.get(b));
            i++;
        }
        //topTree.computeAllCombine(t.vertex.get(0).firstEdge.userData);
        //g1.removeEdge(t.vertex.get(edges.get(0).get(0)).id, t.vertex.get(edges.get(0).get(1)).id);
        //g1.removeEdge(t.vertex.get(edges.get(1).get(0)).id, t.vertex.get(edges.get(1).get(1)).id);
        //g1.removeEdge(t.vertex.get(edges.get(2).get(0)).id, t.vertex.get(edges.get(2).get(1)).id);
        //g1.removeEdge(t.vertex.get(edges.get(3).get(0)).id, t.vertex.get(edges.get(3).get(1)).id);
        if (debug2){
            System.out.println("Running Comparison");
        }
        if(debug2){
            System.out.println("Deleting edges");
        }

        for (int k = 0; k < numberOfEdgesToDelete; k++){
            System.out.println("Delete nr " + k);
            g1.removeEdge(t.vertex.get(edges.get(k).get(0)).id, t.vertex.get(edges.get(k).get(1)).id);
            g1.bridge();
            System.out.println("There is " + g1.count + " bridges");

            topTree.delete(t.vertex.get(edges.get(k).get(0)), t.vertex.get(edges.get(k).get(1)));
            if (g1.count == -1) {
                // This case the graph is disconnected
                topTree.twoEdgeConnected(t.vertex.get(g1.e1), t.vertex.get(g1.e2));
            } else if (g1.count != 0){
                // Find bridge and check it is there
                topTree.twoEdgeConnected(t.vertex.get(g1.e1), t.vertex.get(g1.e2));
            } else {
                topTree.twoEdgeConnected(t.vertex.get(0), t.vertex.get(1));
            }
        }



        boolean topTreeConnected = false;

        if (g1.count == -1) {
            // This case the graph is disconnected
            topTreeConnected = topTree.twoEdgeConnected(t.vertex.get(g1.e1), t.vertex.get(g1.e2));
        } else if (g1.count != 0){
            // Find bridge and check it is there
            topTreeConnected = topTree.twoEdgeConnected(t.vertex.get(g1.e1), t.vertex.get(g1.e2));
        } else {
            topTreeConnected = topTree.twoEdgeConnected(t.vertex.get(v1), t.vertex.get(v2));
        }


        if ((g1.count == 0 & topTreeConnected)
            || ((!(g1.count == 0)) & (!topTreeConnected))) {
            System.out.println("Agreement");
        } else {
            System.out.println("Something went wrong");
            throw new RuntimeException();
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
        System.out.println("Generating edges");
        Random rnd = new Random();

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

    private static ArrayList<ArrayList<Integer>> generateEdges2(int numberOfVertices, int numberOfEdge, int seed) {
        System.out.println("Generating edges");
        Random rnd = new Random();

        rnd.setSeed(seed);
        HashSet<Edge> edgeSet = new HashSet<>();

        while (edgeSet.size() < numberOfEdge){
            int i = rnd.nextInt(numberOfVertices);
            int j = rnd.nextInt(numberOfVertices);
            if (i == j){
                continue;
            }
            edgeSet.add(new Edge(i, j));
        }

        ArrayList<Edge> convertList = new ArrayList<>(edgeSet);
        ArrayList<ArrayList<Integer>> chosenEdges = new ArrayList<ArrayList<Integer>>();

        for (Edge e : convertList) {
            ArrayList<Integer> edge = new ArrayList<>();
            edge.add(e.endpoints[0].id);
            edge.add(e.endpoints[1].id);
            edge.add(rnd.nextInt(100));
            chosenEdges.add(edge);
            //System.out.println(e.endpoints[1].id + " + " + e.endpoints[0].id);
        }
        return chosenEdges;
    }

    private static void runTest() throws FileNotFoundException{

        // Prepare data logging file
        csv = new File("testRuntime.csv");
        pw = new PrintWriter(csv);
        pw.write("vertices,edgesAdded,edgesDeleted,totalOperations,totalTime\n");


        for (int j = 40; j < 2000; j = j * 2){
            for (int i = 4 * j; i <= 16 * j; i = i * 2){
                System.out.println("Run: " + j + " " + i);
                testRuntime(j, i, j, j*i);
            }
        }

        pw.flush();
        pw.close();

    }

    private static void testRuntime(int numberOfVertices, int numberOfEdge, int numberOfEdgesToDelete, int seed){

        // Create graphs given in above diagrams

        twoEdgeComparison g1 = new twoEdgeComparison(numberOfVertices, debug);

        // Generate GraphEdges and Vertices
        if (numberOfEdge > (numberOfVertices * (numberOfVertices - 1)/2)){
            System.out.println("Trying to generate a graph with more edges than exists");
        }

        // Test of 2-edge connectivity top tree
        Tree t = Tree.createTree(numberOfVertices);
        twoEdgeConnectivityTopTree topTree = new twoEdgeConnectivityTopTree(numberOfVertices, debug);
        // Generate edges
        ArrayList<ArrayList<Integer>> edges = generateEdges2(numberOfVertices, numberOfEdge, seed);

        for (ArrayList<Integer> list : edges){
            g1.addEdge(list.get(0), list.get(1));
        }
        long totalTime = 0;
        long start;
        long stop;
        start = System.nanoTime();
        System.out.println("Inserting edges");
        for (ArrayList<Integer> list : edges){
            int a = list.get(0);
            int b = list.get(1);

            topTree.insert(t.vertex.get(a),t.vertex.get(b));
        }
        stop = System.nanoTime();
        totalTime += ( stop- start);
        for (int k = 0; k < numberOfEdgesToDelete; k++){
            //System.out.println("Delete nr " + k);
            g1.removeEdge(t.vertex.get(edges.get(k).get(0)).id, t.vertex.get(edges.get(k).get(1)).id);
            g1.bridge();
            //System.out.println("There is " + g1.count + " bridges");
            start = System.nanoTime();
            topTree.delete(t.vertex.get(edges.get(k).get(0)), t.vertex.get(edges.get(k).get(1)));
            stop = System.nanoTime();
            totalTime += ( stop- start);
            if (g1.count == -1) {
                // This case the graph is disconnected
                start = System.nanoTime();
                topTree.twoEdgeConnected(t.vertex.get(g1.e1), t.vertex.get(g1.e2));
                stop = System.nanoTime();
                totalTime += ( stop- start);
            } else if (g1.count != 0){
                // Find bridge and check it is there
                start = System.nanoTime();
                topTree.twoEdgeConnected(t.vertex.get(g1.e1), t.vertex.get(g1.e2));
                stop = System.nanoTime();
                totalTime += ( stop- start);
            } else {
                start = System.nanoTime();
                topTree.twoEdgeConnected(t.vertex.get(0), t.vertex.get(1));
                stop = System.nanoTime();
                totalTime += ( stop- start);
            }
        }
        pw.write(numberOfVertices + "," + numberOfEdge + "," + numberOfEdgesToDelete + "," + (numberOfEdge+numberOfEdgesToDelete) + "," + totalTime + "\n");

        boolean topTreeConnected = false;
        g1.bridge();

        if (g1.count == -1) {
            // This case the graph is disconnected
            topTreeConnected = topTree.twoEdgeConnected(t.vertex.get(g1.e1), t.vertex.get(g1.e2));
        } else if (g1.count != 0){
            // Find bridge and check it is there
            topTreeConnected = topTree.twoEdgeConnected(t.vertex.get(g1.e1), t.vertex.get(g1.e2));
        } else {
            topTreeConnected = topTree.twoEdgeConnected(t.vertex.get(0), t.vertex.get(1));
        }


        if ((g1.count == 0 & topTreeConnected)
                || ((!(g1.count == 0)) & (!topTreeConnected))) {
            System.out.println("Agreement");
        } else {
            System.out.println("Something went wrong");
            throw new RuntimeException();
        }
    }

    private static void runTestMST() throws  FileNotFoundException{

    }

    private static void testRuntimeMST(int numberOfVertices, int numberOfEdge, int numberOfEdgesToDelete, int seed){
        // Method to test the runtime of top tree functions through a constant combine
        ArrayList<ArrayList<Integer>> edges = generateEdges2(numberOfVertices, numberOfEdge, seed);

        // Generate MST using top tree
        Tree t = Tree.createTree(numberOfVertices);
        System.out.println("Graph generated, and kruskal completed, now generating top tree");
        int j = 0;
        MinimumSpanningTopTree topTree = new MinimumSpanningTopTree();
        for (int i = 0; i < numberOfEdge; i++){

            int a = edges.get(i).get(0);
            int b = edges.get(i).get(1);
            int weight = edges.get(i).get(2);

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


            if(maxEdge != null){
                topTree.cut(maxEdge.edge);
            }
            if(insertLink){
                Node newRoot = topTree.link(t.vertex.get(a), t.vertex.get(b), weight);
            }
        }

    }


}
