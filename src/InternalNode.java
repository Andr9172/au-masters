import java.lang.reflect.Array;
import java.util.ArrayList;

public class InternalNode extends Node {

    public ArrayList<Node> children;

    public InternalNode(InternalNode parent, int spineWeight, ArrayList<Node> children) {
        super(parent, spineWeight, false, false, 0);

        this.children = children;
    }
}
