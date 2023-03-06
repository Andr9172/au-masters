import java.lang.reflect.Array;
import java.util.ArrayList;

public class InternalNode extends Node {

    public ArrayList<Node> children;

    public InternalNode(InternalNode parent, UserInfo userInfo, ArrayList<Node> children, int boundaryNode) {
        super(parent, userInfo, false, false, boundaryNode);

        this.children = children;
    }
}
