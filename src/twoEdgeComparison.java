// Code taken from https://www.geeksforgeeks.org/check-if-a-given-graph-is-2-edge-connected-or-not/

// A Java program to find bridges in a given undirected
// graph
import java.io.*;
import java.util.*;
import java.util.LinkedList;
public class twoEdgeComparison {

    private boolean debug = false;

    // This class represents a undirected graph using adjacency
    // list representation
    private final int V; // No. of vertices

    // Array of lists for Adjacency List Representation
    private final LinkedList<Integer>[] adj;
    int time = 0;
    final int NIL = -1;
    int count = 0;

    public int e1 = 0;
    public int e2 = 0;

    // Constructor
    public twoEdgeComparison(int v, boolean debug) {
        this.debug = debug;
        V = v;
        adj = new LinkedList[v];
        for (int i = 0; i < v; ++i)
            adj[i] = new LinkedList();
    }

    // Function to add an edge into the graph
    void addEdge(int v, int w) {
        adj[v].add(w); // Add w to v's list.
        adj[w].add(v); // Add v to w's list
    }

    void removeEdge(int v, int w){
        adj[v].removeFirstOccurrence(w); // Remove w to v's list.
        adj[w].removeFirstOccurrence(v); // Remove v to w's list
    }

    // A recursive function that finds and prints bridges
    // using DFS traversal
    // u --> The vertex to be visited next
    // visited[] --> keeps track of visited vertices
    // disc[] --> Stores discovery times of visited vertices
    // parent[] --> Stores parent vertices in DFS tree
    void bridgeUtil(int u, boolean[] visited, int[] disc,
                    int[] low, int[] parent) {

        // Mark the current node as visited
        visited[u] = true;

        // Initialize discovery time and low value
        disc[u] = low[u] = ++time;

        // Go through all vertices adjacent to this
        Iterator<Integer> i = adj[u].iterator();
        while (i.hasNext()) {
            int v = i.next(); // v is current adjacent of u

            // If v is not visited yet, then make it a child
            // of u in DFS tree and recur for it.
            // If v is not visited yet, then recur for it
            if (!visited[v]) {
                parent[v] = u;
                bridgeUtil(v, visited, disc, low, parent);

                // Check if the subtree rooted with v has a
                // connection to one of the ancestors of u
                low[u] = Math.min(low[u], low[v]);

                // If the lowest vertex reachable from
                // subtree under v is below u in DFS tree,
                // then u-v is a bridge
                if (low[v] > disc[u]){
                    count++;
                    e1 = u;
                    e2 = v;
                    if (debug){
                        System.out.println("id " + u + " id " + v + " is a bridge ");
                    }
                }

            }

            // Update low value of u for parent function
            // calls.
            else if (v != parent[u])
                low[u] = Math.min(low[u], disc[v]);
        }
    }

    // DFS based function to find all bridges. It uses
    // recursive function bridgeUtil()
    void bridge() {
        // Mark all the vertices as not visited
        boolean[] visited = new boolean[V];
        int[] disc = new int[V];
        int[] low = new int[V];
        int[] parent = new int[V];

        // Initialize parent and visited, and
        // ap(articulation point) arrays
        for (int i = 0; i < V; i++) {
            parent[i] = NIL;
            visited[i] = false;
        }

        // Call the recursive helper function to find
        // Bridges in DFS tree rooted with vertex 'i'
        for (int i = 0; i < V; i++)
            if (visited[i] == false)
                bridgeUtil(i, visited, disc, low, parent);

        for (int i = 0; i < V; i++){
            if (adj[i].size() == 0){
                // Something have no connected vertices which means it's 100% not connected
                e1 = i;
                e2 = V - i - 1;
                count = -1;
            }
        }
    }

}
// This code is contributed by Aakash Hasija

