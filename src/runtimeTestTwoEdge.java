import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class runtimeTestTwoEdge {

    private static boolean debug = false;
    private static boolean debug2 = false;

    static File csv;
    static PrintWriter pw;

    public static void main(String[] args) {
        try {
            runTest();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            pw.close();
        }
    }


    private static void runTest() throws FileNotFoundException {

        // Prepare data logging file
        csv = new File("testRuntime3.csv");
        pw = new PrintWriter(csv);
        pw.write("vertices,edgesAdded,edgesDeleted,totalOperations,totalTime\n");

        for (int i = 4; i <= 4; i = i * 2){
            for (int j = 100; j < 32000; j = j * 2){
                System.out.println("Run: " + j + " " + i);
                testRuntime(j, i * j, j, 1);
            }
        }

        //testRuntime2(3200, 12800, 100, 2);

        //testRuntimeConstant(3200, 12800, 500, 2);

        pw.flush();
        pw.close();

    }

    private static void testRuntimeConstant(int numberOfVertices, int numberOfEdge, int numberOfEdgesToDelete, int seed) throws FileNotFoundException{
        File csv2 = new File("offline"+numberOfVertices+"2.csv");
        PrintWriter pw2 = new PrintWriter(csv2);
        pw2.write("Operation,number,time\n");


        // Create graphs given in above diagrams
        twoEdgeComparison g1 = new twoEdgeComparison(numberOfVertices, debug);

        // Generate GraphEdges and Vertices
        if (numberOfEdge > (numberOfVertices * (numberOfVertices - 1)/2)){
            System.out.println("Trying to generate a graph with more edges than exists");
        }

        // Generate edges
        Tree t = Tree.createTree(numberOfVertices);
        ArrayList<ArrayList<Integer>> edges = generateEdges2(numberOfVertices, numberOfEdge, seed);

        long totalTime = 0;
        long start;
        long stop;
        System.out.println("Inserting edges");
        for (int i = 0; i < numberOfEdge; i++){
            int a = edges.get(i).get(0);
            int b = edges.get(i).get(1);
            g1.addEdge(edges.get(i).get(0), edges.get(i).get(1));
        }

        for (int k = 0; k < numberOfEdgesToDelete; k++){
            //System.out.println("Delete nr " + k);
            g1.removeEdge(edges.get(k).get(0), edges.get(k).get(1));
            start = System.nanoTime();
            g1.bridge();
            stop = System.nanoTime();
            pw2.write("query,"+k+","+(stop-start)+"\n");
        }
        pw2.flush();
    }

    private static void testRuntime2(int numberOfVertices, int numberOfEdge, int numberOfEdgesToDelete, int seed) throws FileNotFoundException {
        File csv2 = new File("twoEdgeRuntime"+numberOfVertices+"3.csv");
        PrintWriter pw2 = new PrintWriter(csv2);
        pw2.write("Operation,number,time\n");


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

        long totalTime = 0;
        long start;
        long stop;
        System.out.println("Inserting edges");
        for (int i = 0; i < numberOfEdge; i++){
            int a = edges.get(i).get(0);
            int b = edges.get(i).get(1);
            g1.addEdge(edges.get(i).get(0), edges.get(i).get(1));

            start = System.nanoTime();
            topTree.insert(t.vertex.get(a),t.vertex.get(b));
            stop = System.nanoTime();
            totalTime += ( stop- start);
            pw2.write("insert,"+i+","+(stop-start)+"\n");
            pw2.flush();
        }

        for (int k = 0; k < numberOfEdgesToDelete; k++){
            //System.out.println("Delete nr " + k);
            g1.removeEdge(t.vertex.get(edges.get(k).get(0)).id, t.vertex.get(edges.get(k).get(1)).id);
            start = System.nanoTime();
            g1.bridge();
            stop = System.nanoTime();
            System.out.println((stop - start));
            //System.out.println("There is " + g1.count + " bridges");
            start = System.nanoTime();
            topTree.delete(t.vertex.get(edges.get(k).get(0)), t.vertex.get(edges.get(k).get(1)));
            stop = System.nanoTime();
            pw2.write("delete,"+k+","+(stop-start)+"\n");
            pw2.flush();
            totalTime += ( stop- start);
            /*
            if (g1.count == -1) {
                // This case the graph is disconnected
                start = System.nanoTime();
                topTree.twoEdgeConnected(t.vertex.get(g1.e1), t.vertex.get(g1.e2));
                stop = System.nanoTime();
                pw2.write("query,"+k+","+(stop-start)+"\n");
                pw2.flush();
                totalTime += ( stop- start);
            } else if (g1.count != 0){
                // Find bridge and check it is there
                start = System.nanoTime();
                topTree.twoEdgeConnected(t.vertex.get(g1.e1), t.vertex.get(g1.e2));
                stop = System.nanoTime();
                pw2.write("query,"+k+","+(stop-start)+"\n");
                pw2.flush();
                totalTime += ( stop- start);
            } else {
                start = System.nanoTime();
                topTree.twoEdgeConnected(t.vertex.get(0), t.vertex.get(1));
                stop = System.nanoTime();
                pw2.write("query,"+k+","+(stop-start)+"\n");
                pw2.flush();
                totalTime += ( stop- start);
            } */
        }

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

    private static void testRuntime(int numberOfVertices, int numberOfEdge, int numberOfEdgesToDelete, int seed) throws FileNotFoundException{
        File csv2 = new File("twoEdgeRuntime"+numberOfVertices+".csv");
        PrintWriter pw2 = new PrintWriter(csv2);
        pw2.write("Operation,number,time\n");


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
        int i = 0;
        System.out.println("Inserting edges");
        for (ArrayList<Integer> list : edges){
            int a = list.get(0);
            int b = list.get(1);

            start = System.nanoTime();
            topTree.insert(t.vertex.get(a),t.vertex.get(b));
            stop = System.nanoTime();
            totalTime += ( stop- start);
            pw2.write("insert,"+i+","+(stop-start)+"\n");
            pw2.flush();
            i++;
        }

        for (int k = 0; k < numberOfEdgesToDelete; k++){
            //System.out.println("Delete nr " + k);
            g1.removeEdge(t.vertex.get(edges.get(k).get(0)).id, t.vertex.get(edges.get(k).get(1)).id);
            g1.bridge();
            //System.out.println("There is " + g1.count + " bridges");
            start = System.nanoTime();
            topTree.delete(t.vertex.get(edges.get(k).get(0)), t.vertex.get(edges.get(k).get(1)));
            stop = System.nanoTime();
            pw2.write("delete,"+k+","+(stop-start)+"\n");
            pw2.flush();
            i++;
            totalTime += ( stop- start);
            if (g1.count == -1) {
                // This case the graph is disconnected
                start = System.nanoTime();
                topTree.twoEdgeConnected(t.vertex.get(g1.e1), t.vertex.get(g1.e2));
                stop = System.nanoTime();
                pw2.write("query,"+k+","+(stop-start)+"\n");
                pw2.flush();
                totalTime += ( stop- start);
            } else if (g1.count != 0){
                // Find bridge and check it is there
                start = System.nanoTime();
                topTree.twoEdgeConnected(t.vertex.get(g1.e1), t.vertex.get(g1.e2));
                stop = System.nanoTime();
                pw2.write("query,"+k+","+(stop-start)+"\n");
                pw2.flush();
                totalTime += ( stop- start);
            } else {
                start = System.nanoTime();
                topTree.twoEdgeConnected(t.vertex.get(0), t.vertex.get(1));
                stop = System.nanoTime();
                pw2.write("query,"+k+","+(stop-start)+"\n");
                pw2.flush();
                totalTime += ( stop- start);
            }
        }
        pw.write(numberOfVertices + "," + numberOfEdge + "," + numberOfEdgesToDelete + "," + (numberOfEdge+numberOfEdgesToDelete) + "," + totalTime + "\n");
        pw.flush();

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
            //System.out.println(e.endpoints[0].id + " + " + e.endpoints[1].id);
        }
        return chosenEdges;
    }

}
