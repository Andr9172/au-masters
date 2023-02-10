

public class Vertex {

    public Edge firstEdge;
    public boolean isExposed;

    public Vertex(Edge firstEdge, boolean isExposed) {
        this.firstEdge = firstEdge;
        this.isExposed = isExposed;
    }
}