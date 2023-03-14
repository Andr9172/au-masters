public class MinimumSpanningTreeUserInfo implements UserInfo {

    public int spineWeight;

    public MinimumSpanningTreeUserInfo(int spineWeight){
        this.spineWeight = spineWeight;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MinimumSpanningTreeUserInfo){
            return this.spineWeight == ((MinimumSpanningTreeUserInfo) o).spineWeight;
        }
        return false;
    }

}
