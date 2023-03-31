public class Vertex {

    public Edge firstEdge;
    public boolean isExposed;
    public VertexUserInfo userInfo;

    public Vertex(Edge firstEdge, boolean isExposed, VertexUserInfo userInfo) {
        this.firstEdge = firstEdge;
        this.isExposed = isExposed;
        this.userInfo = userInfo;
    }
}