import java.util.ArrayList;
import java.util.HashMap;

public class twoEdgeVertexUserInfo implements VertexUserInfo{


    public ArrayList<Integer> size2;
    public ArrayList<Integer> incident2;
    private int maxLevel;

    public twoEdgeVertexUserInfo(int size){
        size2 = new ArrayList<>();
        incident2 = new ArrayList<>();
        this.maxLevel = (int) Math.ceil(Math.log(size));


        for (int i = 0; i <= maxLevel; i++){
            size2.add(i, 1);
            incident2.add(i, 1);
        }
    }

}
