public class Vertex {

    public Edge firstEdge;
    public boolean isExposed;
    public VertexUserInfo userInfo;
    public int id;

    public Vertex(Edge firstEdge, boolean isExposed, VertexUserInfo userInfo, int id) {
        this.firstEdge = firstEdge;
        this.isExposed = isExposed;
        this.userInfo = userInfo;
        this.id = id;
    }
}