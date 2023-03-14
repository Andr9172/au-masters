public class SizeUserInfo implements UserInfo {

    public int size;

    public SizeUserInfo(int size){this.size = size;}


    public boolean equals(Object o) {
        if (o instanceof SizeUserInfo){
            return this.size == ((SizeUserInfo) o).size;
        }
        return false;
    }

}
