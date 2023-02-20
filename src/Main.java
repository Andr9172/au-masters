// Method for creating a top tree and testing it

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        //if (args.length < 1) {
        //    System.out.println("Missing arguments");
        //    return;
        //}

        //if (args[0].equals("command")){
        //    runCommandMode();
        //}

        //if (args[0].equals("compare")){
        //    if (args.length <= 3) {
        //        System.out.println("Missing arguements for compare mode");
        //    }
        //}

        int numberOfVertices = 10; //Integer.parseInt(args[1]);
        int numberOfEdge = 30; //Integer.parseInt(args[2]);
        int repeats = 1;

        for (int i = 0; i <= repeats; i++){
            int res = runCompareMode(numberOfVertices, numberOfEdge);
            if (res != 0) System.out.println("Error in compare mode");
        }

        System.out.println("Unknown arguments?");

    }

    private static int runCompareMode(int numberOfVertices, int numberOfEdge) {

        // Generate GraphEdges and Vertices
        System.out.println("Generating random graph with desired number of vertices and edges");
        if (numberOfEdge > (numberOfVertices * (numberOfVertices - 1)/2)){
            System.out.println("Trying to generate a graph with more edges than exists");
        }

        Random rnd = new Random();

        ArrayList<Kruskal.GraphEdge> edges = new ArrayList<>();

        for (int i = 0; i <= numberOfEdge; i++){
            int dest = Math.abs(rnd.nextInt() % numberOfVertices);
            int source = Math.abs(rnd.nextInt() % numberOfVertices);
            if (edgeExistsAlready(source, dest, edges)){
                i--;
                continue;
            }
            int weight = 1; //Math.abs(rnd.nextInt() % 100);
            if (dest == source) {
                i--;
                continue;
            }
            edges.add(new Kruskal.GraphEdge(source, dest, weight));
        }

        int kruskalResult = Kruskal.kruskals(numberOfVertices, edges);

        // Generate MST using top tree
        Tree t = Tree.createTree(numberOfVertices);
        System.out.println("Graph generated, and kruskal completed, now generating top tree");
        for (int i = 0; i <= numberOfEdge; i++){
            int a = edges.get(i).src;
            int b = edges.get(i).dest;
            int weight = edges.get(i).weight;

            Node root1 = TopTree.expose(t.vertex.get(a));
            Node root2 = TopTree.expose(t.vertex.get(b));

            LeafNode maxEdge = null;
            boolean insertLink = false;
            if (root1 != null && (root1 == root2)){
                if (weight < root1.spineWeight){
                    insertLink = true;
                    maxEdge = TopTree.findMaximum(root1);
                }
            } else {
                insertLink = true;
            }
            TopTree.deExpose(t.vertex.get(a));
            TopTree.deExpose(t.vertex.get(b));

            if(maxEdge != null){
                TopTree.cut(maxEdge.edge);
            }
            if(insertLink){
                Node newRoot = TopTree.link(t.vertex.get(a), t.vertex.get(b), weight);
            }
        }

        // Top tree should now have been built such that we can retrieve the minimum spanning tree!

        int topTreeTotalWeight = 0;
        for (int i = 0; i < numberOfVertices; i++){
            topTreeTotalWeight += sumOverAdjacentEdges(t.vertex, i);
        }
        topTreeTotalWeight = topTreeTotalWeight / 2;
        System.out.println("Top tree total weight is: " + topTreeTotalWeight);
        return 0;
    }

    private static boolean edgeExistsAlready(int source, int dest, ArrayList<Kruskal.GraphEdge> edges) {
        for (Kruskal.GraphEdge edge: edges) {
            if (edge.dest == dest && edge.src == source) return true;
        }
        return false;
    }

    private static int sumOverAdjacentEdges(ArrayList<Vertex> vertex, int i) {
        int sum = 0;
        Edge edge = vertex.get(i).firstEdge;
        while (edge != null){
            sum += edge.weight;
            int j = edge.endpoints.get(1) == vertex.get(i) ? 1 : 0;
            edge = edge.next.get(j);
        }
        return sum;
    }

    private static void runCommandMode() {
    }

}
