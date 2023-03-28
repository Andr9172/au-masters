public class Size {

    public int fullSplayCombineCost = 0;
    public int highestSplay = 0;
    public int highestSemi = 0;

    public double averageSemi = 0;
    public double averageFull = 0;
    public int numberOfSemi = 0;
    public int numberOfFull = 0;

    public Size(int fullSplayCombineCost) {
        this.fullSplayCombineCost = fullSplayCombineCost;
    }

    public void updateSemi(int semi){
        if (semi > highestSemi){
            highestSemi = semi;
        }
        averageSemi += semi;
        numberOfSemi++;
    }

    public void updateFull(int full){
        if (full > highestSplay){
            highestSplay = full;
        }
        averageFull += full;
        numberOfFull++;
    }

    public void test() {
        System.out.println("Highest full splay: " + highestSplay);
        System.out.println("Highest semi splay: " + highestSemi);
        System.out.println("Average full splay: " + averageFull/numberOfFull);
        System.out.println("Average semi splay: " + averageSemi/numberOfSemi);
    }
}
