import java.util.ArrayList;
import java.util.HashMap;

public class twoEdgeVertexUserInfo implements VertexUserInfo{


    public HashMap<Integer, Integer> size2;
    public HashMap<Integer, Integer> incident2;
    private int maxLevel;

    public twoEdgeVertexUserInfo(int size){
        size2 = new HashMap<>();
        incident2 = new HashMap<>();
        this.maxLevel = (int) Math.ceil(Math.log(size)/Math.log(2));


        for (int i = -1; i <= maxLevel; i++){
            size2.put(i, 1);
            incident2.put(i, 0);
        }
    }

}
